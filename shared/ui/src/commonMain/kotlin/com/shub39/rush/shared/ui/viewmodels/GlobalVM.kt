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
package com.shub39.rush.shared.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.shared.core.interfaces.AnalyticsWrapper
import com.shub39.rush.shared.core.interfaces.AnalyticsWrapper.Companion.AnalyticsEvent
import com.shub39.rush.shared.core.interfaces.BillingHandler
import com.shub39.rush.shared.core.interfaces.ChangelogManager
import com.shub39.rush.shared.core.interfaces.MediaAccessChecker
import com.shub39.rush.shared.core.interfaces.OtherPreferences
import com.shub39.rush.shared.core.interfaces.SubscriptionResult
import com.shub39.rush.shared.ui.app.GlobalAction
import com.shub39.rush.shared.ui.app.GlobalState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class GlobalVM(
    @Provided private val billingHandler: BillingHandler,
    @Provided private val otherPreferences: OtherPreferences,
    @Provided private val changelogManager: ChangelogManager,
    @Provided private val mediaAccessChecker: MediaAccessChecker,
    @Provided private val analytics: AnalyticsWrapper,
) : ViewModel() {
    private var syncJob: Job? = null

    private val _state = MutableStateFlow(GlobalState())
    val state =
        _state
            .asStateFlow()
            .onStart {
                analytics.trackEvent(AnalyticsEvent.APP_OPENED.name, emptyMap())
                checkSubscription()
                checkChangelog()
                startSync()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = _state.value,
            )

    fun onAction(action: GlobalAction) {
        when (action) {
            GlobalAction.ChangelogOpened -> {
                analytics.trackEvent(AnalyticsEvent.CHANGELOG_OPENED.name, emptyMap())
            }

            GlobalAction.AboutOpened -> {
                analytics.trackEvent(AnalyticsEvent.ABOUT_OPENED.name, emptyMap())
            }

            GlobalAction.OnRefreshSub ->
                viewModelScope.launch {
                    if (checkSubscription()) {
                        analytics.trackEvent(AnalyticsEvent.PAYWALL_PURCHASED.name, emptyMap())
                    }
                }

            is GlobalAction.OnPaywallOpened -> {
                analytics.trackEvent(
                    AnalyticsEvent.PAYWALL_OPENED.name,
                    mapOf("source" to action.source),
                )
            }

            is GlobalAction.OnUpdateOnboardingDone ->
                viewModelScope.launch {
                    otherPreferences.updateOnboardingDone(action.status)
                    if (action.status) {
                        analytics.trackEvent(AnalyticsEvent.ONBOARDING_COMPLETED.name, emptyMap())
                    }
                }

            is GlobalAction.OnCheckNotificationAccess -> {
                val hasAccess = mediaAccessChecker.canAccessMediaInfo()
                if (hasAccess && !_state.value.notificationAccess) {
                    analytics.trackEvent(
                        AnalyticsEvent.NOTIFICATION_ACCESS_GRANTED.name,
                        emptyMap(),
                    )
                }
                _state.update { it.copy(notificationAccess = hasAccess) }
            }

            GlobalAction.DismissChangelog -> {
                _state.value.currentChangelog?.version?.let {
                    viewModelScope.launch { otherPreferences.updateLastChangelogShown(it) }
                }

                _state.update { it.copy(currentChangelog = null) }
            }
        }
    }

    private suspend fun checkSubscription(): Boolean {
        val isSubscribed = billingHandler.userResult()

        return when (isSubscribed) {
            SubscriptionResult.Subscribed -> {
                _state.update { it.copy(isProUser = true) }
                true
            }

            else -> false
        }
    }

    private fun checkChangelog() {
        viewModelScope.launch {
            val lastShownChangelog = otherPreferences.getLastChangelogShown().first()
            val changeLogs = changelogManager.changelogs.first()

            if (lastShownChangelog.isBlank()) {
                changeLogs.firstOrNull()?.version?.let {
                    otherPreferences.updateLastChangelogShown(it)
                }
                return@launch // don't show changelog on first install
            }

            if (lastShownChangelog != changeLogs.firstOrNull()?.version) {
                _state.update { it.copy(currentChangelog = changeLogs.firstOrNull()) }
            }
        }
    }

    private fun startSync() {
        syncJob?.cancel()
        syncJob =
            viewModelScope.launch {
                combine(
                        otherPreferences.getFontFlow(),
                        otherPreferences.getPaletteStyle(),
                        otherPreferences.getSeedColorFlow(),
                        otherPreferences.getAmoledPrefFlow(),
                        otherPreferences.getAppThemePrefFlow(),
                    ) { font, style, seedColor, withAmoled, theme ->
                        _state.update {
                            it.copy(
                                theme =
                                    it.theme.copy(
                                        appTheme = theme,
                                        font = font,
                                        style = style,
                                        seedColor = seedColor,
                                        withAmoled = withAmoled,
                                    )
                            )
                        }
                    }
                    .launchIn(this)

                otherPreferences
                    .getMaterialYouFlow()
                    .onEach { pref ->
                        _state.update { it.copy(theme = it.theme.copy(materialTheme = pref)) }
                    }
                    .launchIn(this)

                otherPreferences
                    .getSeedColorFlow()
                    .onEach { pref ->
                        _state.update { it.copy(theme = it.theme.copy(seedColor = pref)) }
                    }
                    .launchIn(this)

                otherPreferences
                    .getOnboardingDoneFlow()
                    .onEach { pref -> _state.update { it.copy(onBoardingDone = pref) } }
                    .launchIn(this)
            }
    }
}
