/*
 * Copyright (C) 2026  Shubham Gorai
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
package com.shub39.rush.shared.logic.repository

import com.shub39.rush.logic.IS_PRIVATE_TOKEN_AVAILABLE
import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.core.RushLogger
import com.shub39.rush.shared.core.SourceError
import com.shub39.rush.shared.core.dataclasses.LyricsCandidate
import com.shub39.rush.shared.core.dataclasses.SearchResult
import com.shub39.rush.shared.core.dataclasses.Song
import com.shub39.rush.shared.core.dataclasses.TrackQuery
import com.shub39.rush.shared.core.enums.LyricsProviderId
import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.core.util.LrcUtils
import com.shub39.rush.shared.core.util.TTMLParser
import com.shub39.rush.shared.logic.database.SongDao
import com.shub39.rush.shared.logic.mappers.toSong
import com.shub39.rush.shared.logic.mappers.toSongEntity
import com.shub39.rush.shared.logic.network.GeniusApi
import com.shub39.rush.shared.logic.network.lyrics.LyricsCascade
import kotlin.time.Clock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single(binds = [SongRepository::class])
class RushRepository(
    private val localDao: SongDao,
    private val geniusApi: GeniusApi,
    private val lyricsCascade: LyricsCascade,
) : SongRepository {
    companion object {
        private const val TAG = "RushRepository"
    }

    override suspend fun fetchSong(
        result: SearchResult,
        durationMs: Long?,
    ): Result<Song, SourceError> {
        try {
            val candidate =
                withContext(Dispatchers.IO) {
                    lyricsCascade.findBest(
                        TrackQuery(
                            title = result.title,
                            artist = result.artist,
                            album = result.album,
                            durationMs = durationMs,
                        )
                    )
                }
            val geniusLyrics =
                if (IS_PRIVATE_TOKEN_AVAILABLE) {
                    withContext(Dispatchers.IO) {
                        when (val result = geniusApi.getGeniusLyrics(result.id)) {
                            is Result.Success -> result.data
                            is Result.Error -> null
                        }
                    }
                } else null

            val song =
                Song(
                    id = result.id,
                    title = result.title,
                    artists = result.artist,
                    lyrics = candidate?.plainText() ?: "",
                    album = result.album,
                    sourceUrl = result.url,
                    artUrl = result.artUrl,
                    geniusLyrics = geniusLyrics,
                    syncedLyrics = candidate?.lineSynced(),
                    ttmlLyrics = candidate?.ttml,
                    dateAdded = Clock.System.now().epochSeconds,
                    lyricsSource = candidate?.provider?.name,
                )

            localDao.insertSong(song.toSongEntity())
            return Result.Success(song)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            RushLogger.e(TAG, "Unexpected exception", e)
            return Result.Error(SourceError.Data.UNKNOWN, "Unexpected exception: $e")
        }
    }

    override suspend fun searchGenius(query: String): Result<List<SearchResult>, SourceError> {
        val result = withContext(Dispatchers.IO) { geniusApi.geniusSearch(query) }

        when (result) {
            is Result.Success -> {
                val results = result.data.response.hits.filter { it.type == "song" }
                val searchResults =
                    results
                        .filter { it.type == "song" }
                        .map { hit ->
                            SearchResult(
                                title = hit.result.title,
                                artist = hit.result.artistNames,
                                album = null,
                                artUrl = hit.result.songArtImageURL,
                                url = hit.result.url,
                                id = hit.result.id,
                            )
                        }

                return Result.Success(searchResults)
            }

            is Result.Error -> {
                return Result.Error(error = result.error, message = result.message)
            }
        }
    }

    override suspend fun updateGenius(id: Long): Result<String, SourceError> {
        if (!IS_PRIVATE_TOKEN_AVAILABLE) return Result.Error(SourceError.Network.REQUEST_FAILED)

        val geniusLyrics = withContext(Dispatchers.IO) { geniusApi.getGeniusLyrics(id) }
        return when (geniusLyrics) {
            is Result.Success -> {
                localDao.updateGeniusLyrics(id, geniusLyrics.data)
                Result.Success(geniusLyrics.data)
            }
            is Result.Error -> Result.Error(SourceError.Data.UNKNOWN)
        }
    }

    override suspend fun searchLyrics(
        query: TrackQuery
    ): Result<List<LyricsCandidate>, SourceError> =
        withContext(Dispatchers.IO) { lyricsCascade.searchAll(query) }

    override suspend fun fetchLyricsFrom(
        provider: LyricsProviderId,
        query: TrackQuery,
    ): Result<LyricsCandidate, SourceError> =
        withContext(Dispatchers.IO) { lyricsCascade.fetchFrom(provider, query) }

    override suspend fun insertSong(song: Song) {
        localDao.insertSong(song.toSongEntity())
    }

    override suspend fun getSongs(): Flow<List<Song>> {
        return localDao.getAllSongs().map { entities -> entities.map { it.toSong() } }
    }

    override suspend fun getAllSongs(): List<Song> {
        return localDao.getAllSongs().first().map { it.toSong() }
    }

    override suspend fun getSong(id: Long): Song {
        return localDao.getSongById(id).toSong()
    }

    override suspend fun getSong(query: String): List<Song> {
        return localDao.searchSong(query).map { it.toSong() }
    }

    override suspend fun deleteSong(id: Long) {
        localDao.deleteSong(id)
    }

    override suspend fun applyLyrics(id: Long, candidate: LyricsCandidate) {
        localDao.updateLyrics(
            id = id,
            plain = candidate.plainText(),
            synced = candidate.lineSynced(),
            ttml = candidate.ttml,
            source = candidate.provider.name,
        )
    }

    override suspend fun setSongOffset(id: Long, offsetMs: Long) {
        localDao.updateSyncOffset(id, offsetMs)
    }

    override suspend fun deleteAllSongs() {
        localDao.deleteAllSongs()
    }
}

private fun LyricsCandidate.lineSynced(): String? =
    lrc
        ?: ttml?.let { ttml ->
            // background vocals are tagged "{bg}" by toLRC, keep the main lines only
            LrcUtils.normalize(
                TTMLParser.toLRC(ttml).lines().filterNot { "]{bg}" in it }.joinToString("\n")
            )
        }

private fun LyricsCandidate.plainText(): String =
    plain?.takeIf { it.isNotBlank() } ?: lineSynced()?.let(LrcUtils::toPlain) ?: ""
