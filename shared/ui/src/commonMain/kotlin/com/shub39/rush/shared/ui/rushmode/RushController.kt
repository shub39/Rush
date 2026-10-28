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
package com.shub39.rush.shared.ui.rushmode

import com.shub39.rush.shared.core.AnalyticsEvent
import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.core.dataclasses.SearchResult
import com.shub39.rush.shared.core.dataclasses.SongMeta
import com.shub39.rush.shared.core.interfaces.AnalyticsWrapper
import com.shub39.rush.shared.core.interfaces.MediaAccessChecker
import com.shub39.rush.shared.core.interfaces.OtherPreferences
import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.core.listener.MediaListener
import com.shub39.rush.shared.core.matching.SongMatcher
import com.shub39.rush.shared.ui.errorStringRes
import com.shub39.rush.shared.ui.searchsheet.toSearchResult
import com.shub39.rush.shared.ui.session.SessionController
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

/**
 * Owns rush mode end to end.
 *
 * Lives on the application scope and is started once from the app entry point, so the pipeline
 * keeps running while the user navigates (it used to live inside the search sheet view model, which
 * is why rush mode never worked at startup). It is also the only consumer of [MediaAccessChecker],
 * making it the single source of truth for "do we have media access".
 */
@Single
class RushController(
    private val repo: SongRepository,
    private val prefs: OtherPreferences,
    private val session: SessionController,
    private val mediaAccessChecker: MediaAccessChecker,
    private val analytics: AnalyticsWrapper,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(RushState())
    val state: StateFlow<RushState> = _state.asStateFlow()

    private val _navEvents =
        MutableSharedFlow<RushNavEvent>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    val navEvents: SharedFlow<RushNavEvent> = _navEvents.asSharedFlow()

    private val mediaAccess = MutableStateFlow(false)

    private var started = false
    private var resolvedKey: String? = null
    private var accessPollJob: Job? = null

    /** Idempotent. Must be called from the app entry point, not from a screen. */
    fun start() {
        if (started) return
        started = true

        scope.launch {
            prefs
                .getRushModeFlow()
                .onEach { enabled -> _state.update { it.copy(enabled = enabled) } }
                .launchIn(this)

            mediaAccess
                .onEach { granted -> _state.update { it.copy(mediaAccess = granted) } }
                .launchIn(this)

            MediaListener.songInfoFlow
                .onEach { meta -> _state.update { it.copy(nowPlaying = meta) } }
                .launchIn(this)

            observeNowPlaying()
        }

        refreshMediaAccess()
    }

    fun onAction(action: RushAction) {
        when (action) {
            RushAction.Toggle -> scope.launch { setEnabled(!_state.value.enabled) }
            RushAction.Enable -> scope.launch { setEnabled(true) }
            RushAction.Disable -> scope.launch { setEnabled(false) }
            RushAction.Retry -> retry()
            RushAction.RefreshMediaAccess -> refreshMediaAccess()
            RushAction.OnRequestMediaAccess -> requestMediaAccess()
        }
    }

    /** Re-reads media access from the platform. Also re-arms the platform listener once granted. */
    fun refreshMediaAccess() {
        mediaAccess.value = mediaAccessChecker.canAccessMediaInfo()
    }

    private suspend fun setEnabled(enabled: Boolean) {
        analytics.trackEvent(AnalyticsEvent.RUSH_MODE_TOGGLED, mapOf("enabled" to enabled))

        prefs.updateRushMode(enabled)

        if (enabled) {
            resolvedKey = null
            MediaListener.onSeekEagerly()
        }
    }

    private fun retry() {
        val nowPlaying = _state.value.nowPlaying ?: return

        resolvedKey = null
        scope.launch { resolve(nowPlaying) }
    }

    private fun requestMediaAccess() {
        mediaAccessChecker.launchPermissionSettings()

        accessPollJob?.cancel()
        accessPollJob =
            scope.launch {
                while (true) {
                    delay(500.milliseconds)

                    val granted = mediaAccessChecker.canAccessMediaInfo()

                    if (granted) {
                        mediaAccess.value = true
                        analytics.trackEvent(AnalyticsEvent.NOTIFICATION_ACCESS_GRANTED, emptyMap())
                        mediaAccessChecker.redirectToApp()
                        break
                    }
                }
            }
    }

    @OptIn(FlowPreview::class)
    private suspend fun observeNowPlaying() {
        combine(prefs.getRushModeFlow(), mediaAccess, MediaListener.songInfoFlow) {
                enabled,
                access,
                meta ->
                Triple(enabled, access, meta)
            }
            .distinctUntilChanged()
            .debounce(REFRESH_DEBOUNCE)
            .collect { (enabled, access, meta) -> if (enabled && access) resolve(meta) }
    }

    private suspend fun resolve(meta: SongMeta) {
        if (meta.title.isBlank()) return

        val key = "${meta.title}|${meta.artist.orEmpty()}|${meta.album.orEmpty()}"
        if (key == resolvedKey) return
        resolvedKey = key

        val query = listOfNotNull(meta.title, meta.artist).joinToString(" ").trim()

        _state.update { it.copy(phase = RushPhase.Searching(query)) }

        val saved = localCandidate(meta)
        if (saved != null) {
            load(saved, fromLocal = true)
            return
        }

        when (val remote = repo.searchGenius(query)) {
            is Result.Error ->
                _state.update {
                    it.copy(phase = RushPhase.Error(errorStringRes(remote.error), remote.message))
                }

            is Result.Success -> {
                val match = SongMatcher.best(meta, remote.data)

                if (match == null) {
                    _state.update { it.copy(phase = RushPhase.NotFound) }
                } else {
                    load(match, fromLocal = false)
                }
            }
        }
    }

    private suspend fun load(match: SearchResult, fromLocal: Boolean) {
        _state.update { it.copy(phase = RushPhase.Fetching("${match.title} - ${match.artist}")) }

        val loaded =
            if (fromLocal) {
                session.loadSaved(match.id, origin = "rush_saved", autoSync = true)
            } else {
                session.loadRemote(match, origin = "rush_online", autoSync = true)
            }

        if (loaded) {
            _state.update { it.copy(phase = RushPhase.Loaded(match.id)) }
            _navEvents.emit(RushNavEvent.OpenLyrics)
        } else {
            val error = session.state.value.error

            _state.update {
                it.copy(
                    phase =
                        if (error != null) RushPhase.Error(error.code, error.debug)
                        else RushPhase.NotFound
                )
            }
        }
    }

    private suspend fun localCandidate(meta: SongMeta): SearchResult? =
        runCatching { SongMatcher.best(meta, repo.getSong(meta.title).map { it.toSearchResult() }) }
            .getOrNull()

    private companion object {
        val REFRESH_DEBOUNCE = 300.milliseconds
    }
}
