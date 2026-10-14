/*
 * Copyright (C) 2026  Karelisio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.karelisio.rushio.background

import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.core.RushLogger
import com.shub39.rush.shared.core.dataclasses.SongMeta
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.core.dataclasses.TrackQuery
import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.core.interfaces.SyncOffsetManager
import com.shub39.rush.shared.core.listener.MediaListener
import com.shub39.rush.shared.core.util.TTMLParser
import com.shub39.rush.shared.core.util.TrackMatcher
import com.shub39.rush.shared.ui.lyrics.LyricsState
import com.shub39.rush.shared.ui.lyrics.breakLyrics
import com.shub39.rush.shared.ui.lyrics.parseLyrics
import com.shub39.rush.shared.ui.lyrics.toSongUi
import com.shub39.rush.shared.ui.viewmodels.SharedStates
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

/** A synced lyrics line. */
data class TimedText(val startMs: Long, val text: String)

/**
 * Line sung at [positionMs] and the next non empty line, empty strings when there is none. An
 * instrumental (empty) current line stays empty so the display can show a music note.
 */
fun linesAt(lines: List<TimedText>, positionMs: Long): Pair<String, String> {
    val index = lines.indexOfLast { it.startMs <= positionMs }
    val current = lines.getOrNull(index)?.text.orEmpty()
    val next = lines.drop(index + 1).firstOrNull { it.text.isNotBlank() }?.text.orEmpty()
    return current to next
}

/** What the background lyrics display (overlay or notification) shows. */
data class NowPlayingLyrics(
    val status: Status = Status.IDLE,
    val title: String = "",
    val artist: String = "",
    val currentLine: String = "",
    val nextLine: String = "",
    val isPlaying: Boolean = false,
) {
    enum class Status {
        /** Nothing is playing. */
        IDLE,
        LOADING,
        NO_LYRICS,
        /** Only unsynced lyrics were found. */
        NOT_SYNCED,
        SYNCED,
    }
}

/**
 * Follows the song played by any media app and computes its current and next lyrics lines, shifted
 * by the audio device and song sync offsets.
 */
class NowPlayingLyricsEngine(
    private val repo: SongRepository,
    private val sharedStates: SharedStates,
    private val syncOffsetManager: SyncOffsetManager,
) {
    private val _state = MutableStateFlow(NowPlayingLyrics())
    val state: StateFlow<NowPlayingLyrics> = _state.asStateFlow()

    fun start(scope: CoroutineScope): Job =
        scope.launch {
            MediaListener.songInfoFlow.distinctUntilChanged().collectLatest { meta ->
                try {
                    follow(meta)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    RushLogger.e(TAG, "Could not follow ${meta.title}", e)
                    _state.value = NowPlayingLyrics(NowPlayingLyrics.Status.NO_LYRICS, meta.title)
                }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun follow(meta: SongMeta) {
        val title = meta.title
        val artist = meta.artist.orEmpty()
        _state.value = NowPlayingLyrics(NowPlayingLyrics.Status.LOADING, title, artist)

        val resolved = resolveSong(meta)

        // The lyrics screen may hold a corrected version, or a new sync offset of this song
        val song =
            sharedStates.lyricsState
                .map { state ->
                    (state.lyricsState as? LyricsState.Loaded)?.song?.takeIf { it.matches(meta) }
                        ?: resolved
                }
                .distinctUntilChanged()
                .map { it to it?.timedLines().orEmpty() }

        combine(song, positions(), syncOffsetManager.state) { (song, lines), position, offset ->
                val (positionMs, playing) = position

                when {
                    song == null ->
                        NowPlayingLyrics(
                            NowPlayingLyrics.Status.NO_LYRICS,
                            title,
                            artist,
                            isPlaying = playing,
                        )
                    lines.isEmpty() ->
                        NowPlayingLyrics(
                            status =
                                if (song.lyrics.isNotEmpty()) NowPlayingLyrics.Status.NOT_SYNCED
                                else NowPlayingLyrics.Status.NO_LYRICS,
                            title = song.title,
                            artist = song.artists,
                            isPlaying = playing,
                        )
                    else -> {
                        val (current, next) =
                            linesAt(lines, positionMs - offset.totalOffset(song.syncOffsetMs))
                        NowPlayingLyrics(
                            status = NowPlayingLyrics.Status.SYNCED,
                            title = song.title,
                            artist = song.artists,
                            currentLine = current,
                            nextLine = next,
                            isPlaying = playing,
                        )
                    }
                }
            }
            .collect { _state.value = it }
    }

    /** Lyrics of the song: shown in the app, saved, or fetched (and saved) from the sources. */
    private suspend fun resolveSong(meta: SongMeta): SongUi? {
        (sharedStates.lyricsState.value.lyricsState as? LyricsState.Loaded)
            ?.song
            ?.takeIf { it.matches(meta) }
            ?.let {
                return it
            }

        val query =
            TrackQuery(
                title = meta.title,
                artist = meta.artist.orEmpty(),
                album = meta.album,
                durationMs = meta.duration,
            )

        val saved = repo.getSong(meta.title)
        TrackMatcher.best(query, saved, { it.title }, { it.artists })?.let {
            return it.toSongUi()
        }

        val results =
            (repo.searchGenius("${meta.title} ${meta.artist.orEmpty()}".trim()) as? Result.Success)
                ?.data
                .orEmpty()
        TrackMatcher.best(query, results, { it.title }, { it.artist })?.let { result ->
            (repo.fetchSong(result, meta.duration) as? Result.Success)?.data?.let {
                return it.toSongUi()
            }
        }

        // Song identification failed: lyrics only, not saved
        val candidate = repo.fetchBestLyrics(query) ?: return null
        return SongUi(
            id = -1,
            title = meta.title,
            artists = meta.artist.orEmpty(),
            album = meta.album,
            sourceUrl = "",
            artUrl = null,
            lyrics = breakLyrics(candidate.plain.orEmpty()),
            syncedLyrics = candidate.lrc?.let(::parseLyrics),
            geniusLyrics = null,
            ttmlLyrics = candidate.ttml?.let(TTMLParser::parseTTML),
            lyricsSource = candidate.provider,
        )
    }

    /** Playback position (interpolated between media session updates) and playing state. */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun positions(): Flow<Pair<Long, Boolean>> =
        combine(MediaListener.songPositionFlow, MediaListener.playbackSpeedFlow, ::Pair)
            .transformLatest { (position, speed) ->
                val start = TimeSource.Monotonic.markNow()
                do {
                    val elapsed = (speed * start.elapsedNow().inWholeMilliseconds).toLong()
                    emit((position + elapsed) to (speed > 0f))
                    delay(TICK)
                } while (speed > 0f)
            }

    private fun SongUi.matches(meta: SongMeta): Boolean =
        TrackMatcher.similarity(title, meta.title) >= 0.8 &&
            meta.artist?.let { TrackMatcher.similarity(artists, it) >= 0.3 } != false

    private fun SongUi.timedLines(): List<TimedText> =
        ttmlLyrics
            ?.takeIf { it.isNotEmpty() }
            ?.map { TimedText((it.startTime * 1000).toLong(), it.text) }
            ?: syncedLyrics?.map { TimedText(it.time, it.text) }.orEmpty()

    private companion object {
        const val TAG = "NowPlayingLyrics"
        val TICK = 200.milliseconds
    }
}
