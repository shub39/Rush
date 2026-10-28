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
import com.shub39.rush.shared.core.AnalyticsEvent
import com.shub39.rush.shared.core.interfaces.AnalyticsWrapper
import com.shub39.rush.shared.core.interfaces.BillingHandler
import com.shub39.rush.shared.core.interfaces.OtherPreferences
import com.shub39.rush.shared.core.interfaces.SubscriptionResult
import com.shub39.rush.shared.ui.app.GlobalAction
import com.shub39.rush.shared.ui.app.GlobalState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    @Provided private val analytics: AnalyticsWrapper,
) : ViewModel() {
    private var syncJob: Job? = null

    private val _state = MutableStateFlow(GlobalState())
    val state =
        _state
            .asStateFlow()
            .onStart {
                checkSubscription(firstLaunch = true)
                startSync()
                analytics.trackEvent(AnalyticsEvent.APP_OPENED, emptyMap())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = _state.value,
            )

    fun onAction(action: GlobalAction) {
        when (action) {
            GlobalAction.ChangelogOpened -> {
                analytics.trackEvent(AnalyticsEvent.CHANGELOG_OPENED, emptyMap())
            }

            GlobalAction.AboutOpened -> {
                analytics.trackEvent(AnalyticsEvent.ABOUT_OPENED, emptyMap())
            }

            GlobalAction.OnRefreshSub -> viewModelScope.launch { checkSubscription() }

            is GlobalAction.OnPaywallOpened -> {
                _state.update { it.copy(paywallSource = action.source) }
                analytics.trackEvent(
                    AnalyticsEvent.PAYWALL_OPENED,
                    mapOf("source" to action.source),
                )
            }

            is GlobalAction.OnPaywallDismissed -> {
                // Skip when the dismissal was actually caused by a successful purchase
                if (!billingHandler.isPro.value) {
                    analytics.trackEvent(
                        AnalyticsEvent.PAYWALL_DISMISSED,
                        mapOf<String, Any>(
                            "source" to (_state.value.paywallSource ?: "unknown"),
                            "dwell_ms" to action.dwellMs,
                        ),
                    )
                }
            }

            is GlobalAction.OnProFeatureShown -> {
                analytics.trackEvent(
                    AnalyticsEvent.PRO_FEATURE_SHOWN,
                    mapOf("feature" to action.feature),
                )
            }

            is GlobalAction.OnProFeatureUpsellTapped -> {
                analytics.trackEvent(
                    AnalyticsEvent.PRO_FEATURE_UPSELL_TAPPED,
                    mapOf("feature" to action.feature),
                )
            }
        }
    }

    private suspend fun checkSubscription(firstLaunch: Boolean = false) {
        val isSubscribed = billingHandler.userResult()

        when (isSubscribed) {
            SubscriptionResult.Subscribed -> {
                if (!_state.value.isProUser && !firstLaunch) {
                    analytics.trackEvent(AnalyticsEvent.PAYWALL_PURCHASED, emptyMap())
                }

                _state.update { it.copy(isProUser = true) }
            }

            else -> {}
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
            }
    }
}
