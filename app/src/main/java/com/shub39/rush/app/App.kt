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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.shub39.rush.app.state.GlobalState
import com.shub39.rush.billing.PaywallPage
import com.shub39.rush.domain.util.pop
import com.shub39.rush.navigation.horizontalTransitionMetadata
import com.shub39.rush.navigation.verticalTransitionMetadata
import com.shub39.rush.presentation.lyrics.LyricsGraph
import com.shub39.rush.presentation.onboarding.Onboarding
import com.shub39.rush.presentation.saved.SavedPage
import com.shub39.rush.presentation.searchsheet.SearchSheet
import com.shub39.rush.presentation.setting.SettingsGraph
import com.shub39.rush.presentation.share.SharePage
import com.shub39.rush.viewmodels.LyricsVM
import com.shub39.rush.viewmodels.SavedVM
import com.shub39.rush.viewmodels.SearchSheetVM
import com.shub39.rush.viewmodels.SettingsVM
import com.shub39.rush.viewmodels.ShareVM
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
    onGlobalAction: (GlobalAction) -> Unit,
) {
    val backStack = rememberNavBackStack(SavedPage)
    val context = LocalContext.current

    val searchSheetVM: SearchSheetVM = koinViewModel()
    val searchState by searchSheetVM.state.collectAsStateWithLifecycle()
    val searchSheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) { onGlobalAction(GlobalAction.OnCheckNotificationAccess(context)) }

    NavDisplay(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        backStack = backStack,
        entryProvider = entryProvider {
            entry<SavedPage> {
                val savedVM: SavedVM = koinViewModel()
                val savedState by savedVM.state.collectAsStateWithLifecycle()

                SavedPage(
                    state = savedState,
                    notificationAccess = globalState.notificationAccess,
                    onAction = savedVM::onAction,
                    onNavigateToLyrics = { backStack.add(LyricsGraph) },
                    onNavigateToSettings = { backStack.add(SettingsGraph) },
                )
            }

            entry<LyricsGraph>(metadata = verticalTransitionMetadata()) {
                val lyricsVM: LyricsVM = koinViewModel()
                val lyricsState by lyricsVM.state.collectAsStateWithLifecycle()
                val playbackInfo by lyricsVM.playbackInfo.collectAsStateWithLifecycle()

                LyricsGraph(
                    notificationAccess = globalState.notificationAccess,
                    lyricsState = lyricsState,
                    lyricsAction = lyricsVM::onAction,
                    playbackInfo = playbackInfo,
                    onShare = { backStack.add(SharePage) },
                )
            }

            entry<SharePage>(metadata = verticalTransitionMetadata()) {
                val shareVM: ShareVM = koinViewModel()
                val shareState by shareVM.state.collectAsStateWithLifecycle()

                SharePage(
                    onDismiss = {
                        if (backStack.size != 1) backStack.removeLastOrNull()
                    },
                    state = shareState,
                    onAction = shareVM::onAction,
                    isProUser = globalState.isProUser,
                    onShowPaywall = { backStack.add(PaywallPage) },
                )
            }

            entry<SettingsGraph>(metadata = horizontalTransitionMetadata()) {
                val settingsVM: SettingsVM = koinViewModel()
                val settingsState by settingsVM.state.collectAsStateWithLifecycle()

                SettingsGraph(
                    notificationAccess = globalState.notificationAccess,
                    state = settingsState,
                    action = settingsVM::onAction,
                    onNavigateBack = { backStack.pop() },
                    isProUser = globalState.isProUser,
                    onShowPaywall = { backStack.add(PaywallPage) },
                )
            }

            entry<OnboardingPage>(metadata = verticalTransitionMetadata()) {
                Onboarding(
                    onDone = {
                        onGlobalAction(GlobalAction.OnUpdateOnboardingDone(true))
                        backStack.pop()
                    },
                    notificationAccess = globalState.notificationAccess,
                    onUpdateNotificationAccess = {
                        onGlobalAction(
                            GlobalAction.OnCheckNotificationAccess(context)
                        )
                    },
                )
            }

            entry<PaywallPage>(metadata = verticalTransitionMetadata()) {
                PaywallPage(
                    isProUser = globalState.isProUser,
                    onDismissRequest = {
                        if (backStack.size != 1) backStack.removeLastOrNull()
                    },
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
