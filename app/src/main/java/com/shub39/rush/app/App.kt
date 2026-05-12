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
package com.shub39.rush.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.shub39.rush.app.state.GlobalState
import com.shub39.rush.domain.util.pop
import com.shub39.rush.navigation.horizontalTransitionMetadata
import com.shub39.rush.navigation.verticalTransitionMetadata
import com.shub39.rush.presentation.lyrics.route.LyricsRoute
import com.shub39.rush.presentation.onboarding.route.OnboardingRoute
import com.shub39.rush.presentation.paywall.route.PaywallRoute
import com.shub39.rush.presentation.saved.route.SavedRoute
import com.shub39.rush.presentation.searchsheet.SearchSheet
import com.shub39.rush.presentation.setting.route.SettingsRoute
import com.shub39.rush.presentation.share.route.ShareRoute
import com.shub39.rush.viewmodels.SearchSheetVM
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SavedPage : NavKey

@Serializable
data object LyricsGraph : NavKey

@Serializable
data object SettingsGraph : NavKey

@Serializable
data object SharePage : NavKey

@Serializable
data object OnboardingPage : NavKey

@Serializable
data object PaywallPage : NavKey

@Composable
fun App(
    globalState: GlobalState,
    globalEvents: Flow<GlobalEvent>,
    onGlobalAction: (GlobalAction) -> Unit,
) {
    val backStack = rememberNavBackStack(SavedPage)

    val searchSheetVM: SearchSheetVM = koinViewModel()
    val searchState by searchSheetVM.state.collectAsStateWithLifecycle()
    val searchSheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        globalEvents.collect { event ->
            when (event) {
                GlobalEvent.GoToOnboarding -> {
                    if (backStack.lastOrNull() != OnboardingPage) {
                        backStack.clear()
                        backStack.add(OnboardingPage)
                    }
                }
            }
        }
    }

    NavDisplay(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        backStack = backStack,
        entryProvider = entryProvider {
            entry<SavedPage> {
                SavedRoute(
                    notificationAccess = globalState.notificationAccess,
                    onNavigateToLyrics = { backStack.add(LyricsGraph) },
                    onNavigateToSettings = { backStack.add(SettingsGraph) },
                )
            }

            entry<LyricsGraph>(metadata = verticalTransitionMetadata()) {
                LyricsRoute(
                    notificationAccess = globalState.notificationAccess,
                    onShare = { backStack.add(SharePage) },
                )
            }

            entry<SharePage>(metadata = verticalTransitionMetadata()) {
                ShareRoute(
                    onDismiss = { backStack.pop() },
                    isProUser = globalState.isProUser,
                    onShowPaywall = { backStack.add(PaywallPage) },
                )
            }

            entry<SettingsGraph>(metadata = horizontalTransitionMetadata()) {
                SettingsRoute(
                    notificationAccess = globalState.notificationAccess,
                    fossWarningDaysLeft = globalState.fossWarningDaysLeft,
                    onNavigateBack = { backStack.pop() },
                    isProUser = globalState.isProUser,
                    onShowPaywall = { backStack.add(PaywallPage) },
                )
            }

            entry<OnboardingPage>(metadata = verticalTransitionMetadata()) {
                OnboardingRoute(
                    onDone = {
                        onGlobalAction(GlobalAction.OnUpdateOnboardingDone(true))
                        backStack.clear()
                        backStack.add(SavedPage)
                    },
                    notificationAccess = globalState.notificationAccess,
                )
            }

            entry<PaywallPage>(metadata = verticalTransitionMetadata()) {
                PaywallRoute(
                    isProUser = globalState.isProUser,
                    onDismissRequest = { backStack.pop() },
                )
            }
        },
    )


    SearchSheet(
        state = searchState,
        onAction = searchSheetVM::onAction,
        onNavigateToLyrics = { backStack.add(LyricsGraph) },
        sheetState = searchSheetState,
    )
}
