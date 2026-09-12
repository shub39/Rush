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
package com.shub39.rush.shared.ui.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.shub39.rush.shared.ui.LocalWindowSizeClass
import com.shub39.rush.shared.ui.app.GlobalAction
import com.shub39.rush.shared.ui.component.ChangelogSheet
import com.shub39.rush.shared.ui.component.PageFill
import com.shub39.rush.shared.ui.lyrics.ManageSystemBars
import com.shub39.rush.shared.ui.lyrics.section.LyricsCustomisationsPage
import com.shub39.rush.shared.ui.lyrics.section.LyricsPage
import com.shub39.rush.shared.ui.onboarding.Onboarding
import com.shub39.rush.shared.ui.saved.SavedPage
import com.shub39.rush.shared.ui.searchsheet.SearchSheet
import com.shub39.rush.shared.ui.setting.section.About
import com.shub39.rush.shared.ui.setting.section.BackupPage
import com.shub39.rush.shared.ui.setting.section.Changelog
import com.shub39.rush.shared.ui.setting.section.LookAndFeelPage
import com.shub39.rush.shared.ui.setting.section.SettingRootPage
import com.shub39.rush.shared.ui.share.SharePage
import com.shub39.rush.shared.ui.share.component.SharePageEdit
import com.shub39.rush.shared.ui.theme.RushTheme
import com.shub39.rush.shared.ui.viewmodels.GlobalVM
import com.shub39.rush.shared.ui.viewmodels.LyricsVM
import com.shub39.rush.shared.ui.viewmodels.SavedVM
import com.shub39.rush.shared.ui.viewmodels.SearchSheetVM
import com.shub39.rush.shared.ui.viewmodels.SettingsVM
import com.shub39.rush.shared.ui.viewmodels.ShareVM
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RushNavDisplay(
    modifier: Modifier = Modifier,
    paywall: @Composable (Boolean, () -> Unit) -> Unit,
) {
    val globalVM: GlobalVM = koinViewModel()
    val globalState by globalVM.state.collectAsStateWithLifecycle()

    val windowSizeClass = LocalWindowSizeClass.current

    val topLevelBackStack = retain { TopLevelBackStack(Saved) }

    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }
    val twoPaneSceneStrategy =
        remember(windowSizeClass) { TwoPaneSceneStrategy<NavKey>(windowSizeClass) }
    val listDetailStrategy =
        remember(windowSizeClass) { ListDetailSceneStrategy<NavKey>(windowSizeClass) }
    val editSheetStrategy =
        remember(windowSizeClass) { EditScreenSceneStrategy<NavKey>(windowSizeClass) }

    LaunchedEffect(Unit) { globalVM.onAction(GlobalAction.OnCheckNotificationAccess) }

    LaunchedEffect(globalState.onBoardingDone) {
        if (!globalState.onBoardingDone) topLevelBackStack.addTopLevel(Routes.Onboarding)
    }

    LaunchedEffect(globalState.currentChangelog) {
        if (globalState.currentChangelog != null) topLevelBackStack.addTopLevel(Routes.Changelog)
    }

    RushTheme(theme = globalState.theme) {
        SharedTransitionLayout(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
            NavDisplay(
                backStack = topLevelBackStack.backStack,
                sharedTransitionScope = this,
                onBack = { topLevelBackStack.removeLast() },
                sceneStrategies =
                    listOf(
                        bottomSheetStrategy,
                        twoPaneSceneStrategy,
                        listDetailStrategy,
                        editSheetStrategy,
                    ),
                entryProvider =
                    entryProvider {
                        entry<Routes.Onboarding> {
                            Onboarding(
                                notificationAccess = globalState.notificationAccess,
                                onDone = { topLevelBackStack.removeLast() },
                                onUpdateNotificationAccess = {
                                    globalVM.onAction(GlobalAction.OnCheckNotificationAccess)
                                },
                            )
                        }

                        entry<Routes.Search>(metadata = BottomSheetSceneStrategy.bottomSheet()) {
                            val viewModel = koinViewModel<SearchSheetVM>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            SearchSheet(
                                state = state,
                                onAction = viewModel::onAction,
                                onNavigateToLyrics = {
                                    topLevelBackStack.addTopLevel(Routes.Lyrics.LyricsRoot)
                                },
                                modifier = Modifier.fillMaxWidth().heightIn(max = 700.dp),
                            )
                        }

                        entry<Routes.Lyrics.LyricsRoot>(
                            metadata = ListDetailScene.detailPane() + verticalTransitionMetadata()
                        ) {
                            val viewModel = koinViewModel<LyricsVM>()
                            val state by viewModel.state.collectAsStateWithLifecycle()
                            val playbackInfo by viewModel.playbackInfo.collectAsStateWithLifecycle()

                            ManageSystemBars(state.fullscreen)

                            LyricsPage(
                                onNavigateToCustomisations = {
                                    topLevelBackStack.add(Routes.Lyrics.LyricsCustomisations)
                                },
                                onShare = { topLevelBackStack.add(Routes.Share.ShareRoot) },
                                action = viewModel::onAction,
                                state = state,
                                playbackInfo = playbackInfo,
                                notificationAccess = globalState.notificationAccess,
                                isCustomisationsOpened =
                                    topLevelBackStack.isRouteOnTop(
                                        Routes.Lyrics.LyricsCustomisations
                                    ),
                            )
                        }

                        entry<Routes.Share.ShareRoot>(
                            metadata = EditScreenScene.mainPane() + verticalTransitionMetadata()
                        ) {
                            val viewModel = koinViewModel<ShareVM>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            SharePage(
                                onDismiss = { topLevelBackStack.removeLast() },
                                state = state,
                                onAction = viewModel::onAction,
                                onOpenEdit = { topLevelBackStack.add(Routes.Share.ShareEdit) },
                                isEditing = topLevelBackStack.isRouteOnTop(Routes.Share.ShareEdit),
                            )
                        }

                        entry<Routes.Share.ShareEdit>(
                            metadata = EditScreenScene.editPane() + horizontalTransitionMetadata()
                        ) {
                            val viewModel = koinViewModel<ShareVM>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            SharePageEdit(state = state, onAction = viewModel::onAction)
                        }

                        entry<Routes.Lyrics.LyricsCustomisations>(
                            metadata = ListDetailScene.listPane() + horizontalTransitionMetadata()
                        ) {
                            val viewModel = koinViewModel<LyricsVM>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LyricsCustomisationsPage(
                                onNavigateBack = { topLevelBackStack.removeLast() },
                                state = state,
                                onAction = viewModel::onAction,
                                notificationAccess = globalState.notificationAccess,
                            )
                        }

                        entry<Routes.Saved>(
                            metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()
                        ) {
                            val viewModel = koinViewModel<SavedVM>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            val isLastRouteSettings =
                                topLevelBackStack.backStack.last() is Routes.Settings

                            PageFill {
                                SavedPage(
                                    state = state,
                                    onAction = viewModel::onAction,
                                    onNavigateToLyrics = {
                                        topLevelBackStack.addTopLevel(Routes.Lyrics.LyricsRoot)
                                    },
                                    onNavigateToSettings = {
                                        topLevelBackStack.apply {
                                            if (!isRouteOnTop(Routes.Settings.SettingsRoot)) {
                                                addTopLevel(Routes.Settings.SettingsRoot)
                                            } else removeLast()
                                        }
                                    },
                                    onOpenSearchSheet = { topLevelBackStack.addTopLevel(Search) },
                                    notificationAccess = globalState.notificationAccess,
                                    isSettingsOpen = isLastRouteSettings,
                                    modifier = Modifier.widthIn(max = 600.dp),
                                )
                            }
                        }

                        entry<Routes.Paywall>(metadata = verticalTransitionMetadata()) {
                            paywall(globalState.isProUser, { topLevelBackStack.removeLast() })
                        }

                        entry<Routes.Changelog>(metadata = BottomSheetSceneStrategy.bottomSheet()) {
                            ChangelogSheet(
                                currentLog = globalState.currentChangelog!!,
                                onDismissRequest = { topLevelBackStack.removeLast() },
                                showSupportButton = !globalState.isProUser,
                                onNavigateToPaywall = {
                                    topLevelBackStack.addTopLevel(Routes.Paywall)
                                },
                            )
                        }

                        entry<Routes.Settings.SettingsRoot>(
                            metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()
                        ) {
                            val viewModel = koinViewModel<SettingsVM>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            SettingRootPage(
                                notificationAccess = globalState.notificationAccess,
                                state = state,
                                onShowPaywall = { topLevelBackStack.addTopLevel(Routes.Paywall) },
                                onAction = viewModel::onAction,
                                onNavigateBack = { topLevelBackStack.removeLast() },
                                onNavigateToLookAndFeel = {
                                    topLevelBackStack.addTopLevel(
                                        Routes.Settings.SettingsLookAndFeel
                                    )
                                },
                                onNavigateToBackup = {
                                    topLevelBackStack.addTopLevel(Routes.Settings.SettingsBackup)
                                },
                                onNavigateToChangelog = {
                                    topLevelBackStack.addTopLevel(Routes.Settings.SettingsChangelog)
                                },
                                onNavigateToAppInfo = {
                                    topLevelBackStack.addTopLevel(Routes.Settings.SettingsAppInfo)
                                },
                                onUpdateNotificationAccess = {
                                    globalVM.onAction(GlobalAction.OnCheckNotificationAccess)
                                },
                                lastRoute = topLevelBackStack.backStack.last(),
                            )
                        }

                        entry<Routes.Settings.SettingsLookAndFeel>(
                            metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()
                        ) {
                            val viewModel = koinViewModel<SettingsVM>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LookAndFeelPage(
                                state = state,
                                onAction = viewModel::onAction,
                                onNavigateBack = { topLevelBackStack.removeLast() },
                            )
                        }

                        entry<Routes.Settings.SettingsBackup>(
                            metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()
                        ) {
                            val viewModel = koinViewModel<SettingsVM>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            BackupPage(
                                state = state,
                                onAction = viewModel::onAction,
                                onNavigateBack = { topLevelBackStack.removeLast() },
                            )
                        }

                        entry<Routes.Settings.SettingsChangelog>(
                            metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()
                        ) {
                            val viewModel = koinViewModel<SettingsVM>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            Changelog(
                                changelog = state.changelog,
                                onNavigateBack = { topLevelBackStack.removeLast() },
                            )
                        }

                        entry<Routes.Settings.SettingsAppInfo>(
                            metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()
                        ) {
                            val viewModel = koinViewModel<SettingsVM>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            About(
                                versionName = state.changelog.firstOrNull()?.version ?: "6.7.0",
                                onNavigateBack = { topLevelBackStack.removeLast() },
                            )
                        }
                    },
            )
        }
    }
}
