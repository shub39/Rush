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
import com.shub39.rush.shared.core.SourceError
import com.shub39.rush.shared.core.dataclasses.LyricsCandidate
import com.shub39.rush.shared.core.dataclasses.TrackQuery
import com.shub39.rush.shared.core.enums.LyricsProviderId
import com.shub39.rush.shared.core.enums.LyricsQuality
import com.shub39.rush.shared.core.interfaces.LyricsSourcePreferences
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

/**
 * Queries the enabled lyrics sources in the user defined order and keeps the best lyrics: syllable
 * synced > line synced > plain, ties going to the source ranked first.
 *
 * Sources run in parallel, a failing or slow source never blocks the others.
 */
@Single
class LyricsCascade(
    lrcLib: LrcLibProvider,
    netease: NeteaseProvider,
    musixmatch: MusixmatchProvider,
    qqMusic: QQMusicProvider,
    private val preferences: LyricsSourcePreferences,
) {
    private val providers: Map<LyricsProviderId, LyricsProvider> =
        listOf(lrcLib, netease, musixmatch, qqMusic).associateBy { it.id }

    private suspend fun enabledProviders(): List<LyricsProvider> =
        preferences
            .getSourcesFlow()
            .first()
            .filter { it.enabled }
            .mapNotNull { providers[it.provider] }

    suspend fun findBest(query: TrackQuery): LyricsCandidate? = coroutineScope {
        val enabled = enabledProviders()
        val completions = Channel<Pair<Int, LyricsCandidate?>>(capacity = enabled.size)
        val jobs =
            enabled.mapIndexed { index, provider ->
                launch {
                    val result = provider.safeSearch(query, limit = 1)
                    completions.send(index to (result as? Result.Success)?.data?.firstOrNull())
                }
            }

        try {
            pickBest(enabled.map { it.id }, completions)
        } finally {
            jobs.forEach { it.cancel() }
        }
    }

    suspend fun fetchFrom(
        provider: LyricsProviderId,
        query: TrackQuery,
    ): Result<LyricsCandidate, SourceError> =
        when (val result = providers.getValue(provider).safeSearch(query, limit = 1)) {
            is Result.Success -> Result.Success(result.data.first())
            is Result.Error -> Result.Error(result.error, result.message)
        }

    /** Candidates from every enabled source, grouped by source in cascade order. */
    suspend fun searchAll(
        query: TrackQuery,
        perProvider: Int = 3,
    ): Result<List<LyricsCandidate>, SourceError> = coroutineScope {
        val results =
            enabledProviders()
                .map { provider -> async { provider.safeSearch(query, perProvider) } }
                .awaitAll()

        val candidates = results.flatMap { (it as? Result.Success)?.data.orEmpty() }
        if (candidates.isNotEmpty()) return@coroutineScope Result.Success(candidates)

        // Report a real failure (network...) rather than "no results" when there is one
        for (result in results) {
            if (result is Result.Error && result.error != SourceError.Data.NO_RESULTS) {
                return@coroutineScope Result.Error(result.error, result.message)
            }
        }
        Result.Error(SourceError.Data.NO_RESULTS)
    }

    companion object {
        /**
         * Consumes source results as they complete (index in [order], candidate) and returns as
         * soon as no pending source can beat the best candidate: a better quality, or the same
         * quality from a source ranked higher.
         */
        suspend fun pickBest(
            order: List<LyricsProviderId>,
            completions: ReceiveChannel<Pair<Int, LyricsCandidate?>>,
        ): LyricsCandidate? {
            val done = BooleanArray(order.size)
            var best: LyricsCandidate? = null
            var bestIndex = -1

            repeat(order.size) {
                val (index, candidate) = completions.receive()
                done[index] = true

                if (candidate != null && candidate.quality != LyricsQuality.NONE) {
                    val current = best
                    if (
                        current == null ||
                            candidate.quality > current.quality ||
                            (candidate.quality == current.quality && index < bestIndex)
                    ) {
                        best = candidate
                        bestIndex = index
                    }
                }

                val current = best ?: return@repeat
                val canBeBeaten =
                    order.indices.any { i ->
                        !done[i] &&
                            (order[i].maxQuality > current.quality ||
                                (order[i].maxQuality == current.quality && i < bestIndex))
                    }
                if (!canBeBeaten) return current
            }

            return best
        }
    }
}
