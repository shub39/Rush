package com.shub39.rush.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.shub39.rush.shared.ui.app.GlobalAction
import com.shub39.rush.shared.ui.app.Routes
import com.shub39.rush.shared.ui.component.ChangelogSheet
import com.shub39.rush.shared.ui.lyrics.LyricsGraph
import com.shub39.rush.shared.ui.navigation.horizontalTransitionMetadata
import com.shub39.rush.shared.ui.navigation.verticalTransitionMetadata
import com.shub39.rush.shared.ui.onboarding.Onboarding
import com.shub39.rush.shared.ui.saved.SavedPage
import com.shub39.rush.shared.ui.searchsheet.SearchSheet
import com.shub39.rush.shared.ui.searchsheet.SearchSheetAction
import com.shub39.rush.shared.ui.setting.SettingsGraph
import com.shub39.rush.shared.ui.share.SharePage
import com.shub39.rush.shared.ui.theme.RushTheme
import com.shub39.rush.shared.ui.viewmodels.GlobalVM
import com.shub39.rush.shared.ui.viewmodels.LyricsVM
import com.shub39.rush.shared.ui.viewmodels.SavedVM
import com.shub39.rush.shared.ui.viewmodels.SearchSheetVM
import com.shub39.rush.shared.ui.viewmodels.SettingsVM
import com.shub39.rush.shared.ui.viewmodels.ShareVM
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val globalVM: GlobalVM = koinViewModel()
    val globalState by globalVM.state.collectAsStateWithLifecycle()

    val backStack = rememberNavBackStack(Routes.configuration, Routes.SavedPage)

    LaunchedEffect(Unit) { globalVM.onAction(GlobalAction.OnCheckNotificationAccess) }

    LaunchedEffect(globalState.onBoardingDone) {
        if (!globalState.onBoardingDone) {
            backStack.add(Routes.OnboardingPage)
        }
    }

    RushTheme(theme = globalState.theme) {
        if (globalState.currentChangelog != null) {
            ChangelogSheet(
                currentLog = globalState.currentChangelog!!,
                onDismissRequest = { globalVM.onAction(GlobalAction.DismissChangelog) },
            )
        }

        NavDisplay(
            modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize(),
            backStack = backStack,
            entryProvider =
                entryProvider {
                    entry<Routes.SavedPage> {
                        val savedVM: SavedVM = koinViewModel()
                        val savedState by savedVM.state.collectAsStateWithLifecycle()

                        SavedPage(
                            state = savedState,
                            notificationAccess = globalState.notificationAccess,
                            onAction = savedVM::onAction,
                            onNavigateToLyrics = { backStack.add(Routes.LyricsGraph) },
                            onNavigateToSettings = { backStack.add(Routes.SettingsGraph) },
                        )
                    }

                    entry<Routes.LyricsGraph>(metadata = verticalTransitionMetadata()) {
                        val lyricsVM: LyricsVM = koinViewModel()
                        val lyricsState by lyricsVM.state.collectAsStateWithLifecycle()
                        val playbackInfo by lyricsVM.playbackInfo.collectAsStateWithLifecycle()

                        LyricsGraph(
                            notificationAccess = globalState.notificationAccess,
                            lyricsState = lyricsState,
                            lyricsAction = lyricsVM::onAction,
                            playbackInfo = playbackInfo,
                            onShare = { backStack.add(Routes.SharePage) },
                        )
                    }

                    entry<Routes.SharePage>(metadata = verticalTransitionMetadata()) {
                        val shareVM: ShareVM = koinViewModel()
                        val shareState by shareVM.state.collectAsStateWithLifecycle()

                        SharePage(
                            onDismiss = {
                                if (backStack.size != 1) backStack.removeLastOrNull()
                            },
                            state = shareState,
                            onAction = shareVM::onAction,
                            isProUser = globalState.isProUser,
                            onShowPaywall = {  },
                        )
                    }

                    entry<Routes.SettingsGraph>(metadata = horizontalTransitionMetadata()) {
                        val settingsVM: SettingsVM = koinViewModel()
                        val settingsState by settingsVM.state.collectAsStateWithLifecycle()

                        SettingsGraph(
                            notificationAccess = globalState.notificationAccess,
                            state = settingsState,
                            action = settingsVM::onAction,
                            onNavigateBack = {
                                if (backStack.size != 1) backStack.removeLastOrNull()
                            },
                            isProUser = globalState.isProUser,
                            onShowPaywall = {  },
                            onUpdateNotificationAccess = {
                                globalVM.onAction(GlobalAction.OnCheckNotificationAccess)
                            },
                        )
                    }

                    entry<Routes.OnboardingPage>(metadata = verticalTransitionMetadata()) {
                        Onboarding(
                            onDone = {
                                globalVM.onAction(GlobalAction.OnUpdateOnboardingDone(true))
                                if (backStack.size != 1) backStack.removeLastOrNull()
                            },
                            notificationAccess = globalState.notificationAccess,
                            onUpdateNotificationAccess = {
                                globalVM.onAction(GlobalAction.OnCheckNotificationAccess)
                            },
                        )
                    }
                },
        )

        val searchSheetVM: SearchSheetVM = koinViewModel()
        val searchState by searchSheetVM.state.collectAsStateWithLifecycle()
        if (searchState.visible) {
            SearchSheet(
                state = searchState,
                onAction = searchSheetVM::onAction,
                onNavigateToLyrics = { backStack.add(Routes.LyricsGraph) },
                onDismissRequest = {
                    searchSheetVM.onAction(SearchSheetAction.OnToggleSearchSheet)
                },
            )
        }
    }
}
