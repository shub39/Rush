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
package com.shub39.rush.shared.logic.network.lyrics

import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.core.dataclasses.LyricsCandidate
import com.shub39.rush.shared.core.dataclasses.TrackQuery
import com.shub39.rush.shared.core.enums.LyricsProviderId
import com.shub39.rush.shared.core.util.LrcUtils
import com.shub39.rush.shared.core.util.TrackMatcher
import com.shub39.rush.shared.logic.network.LrcLibApi
import org.koin.core.annotation.Single

@Single
class LrcLibProvider(private val api: LrcLibApi) : LyricsProvider {
    override val id = LyricsProviderId.LRCLIB

    override suspend fun search(query: TrackQuery, limit: Int): List<LyricsCandidate> {
        val results =
            when (val result = api.searchLrcLyrics(query.title, query.artist, query.album)) {
                is Result.Success -> result.data.filter { it.instrumental != true }
                is Result.Error -> return emptyList()
            }

        return TrackMatcher.ranked(
                query = query,
                items = results,
                title = { it.trackName },
                artist = { it.artistName ?: "" },
                durationMs = { dto -> dto.duration?.let { (it * 1000).toLong() } },
            )
            .map { dto ->
                val lrc = LrcUtils.normalize(dto.syncedLyrics)
                LyricsCandidate(
                    provider = id,
                    title = dto.trackName,
                    artist = dto.artistName ?: "",
                    album = dto.albumName,
                    durationMs = dto.duration?.let { (it * 1000).toLong() },
                    plain =
                        dto.plainLyrics?.takeIf { it.isNotBlank() } ?: lrc?.let(LrcUtils::toPlain),
                    lrc = lrc,
                )
            }
            .filter { it.plain != null || it.lrc != null }
            .distinctBy { it.lrc ?: it.plain }
            .take(limit)
    }
}
