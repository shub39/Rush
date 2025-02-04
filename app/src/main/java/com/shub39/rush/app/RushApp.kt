package com.shub39.rush.app

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.core.domain.Route
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.lyrics.data.listener.NotificationListener
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPage
import com.shub39.rush.lyrics.presentation.saved.SavedPage
import com.shub39.rush.lyrics.presentation.search_sheet.SearchSheet
import com.shub39.rush.lyrics.presentation.setting.About
import com.shub39.rush.lyrics.presentation.setting.Backup
import com.shub39.rush.lyrics.presentation.setting.BatchDownloader
import com.shub39.rush.lyrics.presentation.setting.LookAndFeel
import com.shub39.rush.lyrics.presentation.setting.SettingPage
import com.shub39.rush.lyrics.presentation.setting.SettingsVM
import com.shub39.rush.share.SharePage
import org.koin.androidx.compose.koinViewModel

@Composable
fun RushApp(
    rushViewModel: RushViewModel = koinViewModel(),
    settingsVM: SettingsVM = koinViewModel()
) {
    val lyricsState by rushViewModel.lyricsState.collectAsStateWithLifecycle()
    val savedState by rushViewModel.savedState.collectAsStateWithLifecycle()
    val shareState by rushViewModel.shareState.collectAsStateWithLifecycle()
    val settingsState by settingsVM.state.collectAsStateWithLifecycle()
    val searchSheetState by rushViewModel.searchState.collectAsStateWithLifecycle()
    val datastoreSettings by rushViewModel.datastoreSettings.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val notificationAccess = NotificationListener.canAccessNotifications(LocalContext.current)

    RushTheme(
        state = settingsState.theme
    ) {
        NavHost(
            navController = navController,
            startDestination = Route.HomeGraph,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            navigation<Route.HomeGraph>(
                startDestination = Route.SavedPage
            ) {
                composable<Route.SavedPage> {
                    SavedPage(
                        state = savedState,
                        currentSong = lyricsState.song,
                        notificationAccess = notificationAccess,
                        action = rushViewModel::onSavedPageAction,
                        settings = datastoreSettings,
                        navigator = {
                            navController.navigate(it) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            navigation<Route.LyricsGraph>(
                startDestination = Route.LyricsPage
            ) {
                composable<Route.SharePage> {
                    Scaffold { paddingValues ->
                        SharePage(
                            onDismiss = {
                                navController.navigateUp()
                            },
                            state = shareState,
                            paddingValues = paddingValues,
                            settings = datastoreSettings,
                            action = rushViewModel::onSharePageAction
                        )
                    }
                }

                composable<Route.LyricsPage> {
                    LyricsPage(
                        state = lyricsState,
                        settings = datastoreSettings,
                        action = rushViewModel::onLyricsPageAction,
                        onShare = {
                            navController.navigate(Route.SharePage) {
                                launchSingleTop = true
                            }
                        },
                        notificationAccess = notificationAccess
                    )
                }
            }

            navigation<Route.SettingsGraph>(
                startDestination = Route.SettingPage
            ) {
                composable<Route.SettingPage> {
                    SettingPage(
                        state = settingsState,
                        action = settingsVM::onSettingsPageAction,
                        notificationAccess = notificationAccess,
                        navigator = {
                            navController.navigate(it) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable<Route.BatchDownloaderPage> {
                    BatchDownloader(
                        state = settingsState,
                        action = settingsVM::onSettingsPageAction
                    )
                }

                composable<Route.BackupPage> {
                    Backup(
                        state = settingsState,
                        action = settingsVM::onSettingsPageAction
                    )
                }

                composable<Route.AboutPage> {
                    About()
                }

                composable<Route.LookAndFeelPage> {
                    LookAndFeel(
                        state = settingsState,
                        action = settingsVM::onSettingsPageAction
                    )
                }
            }
        }

        if (searchSheetState.visible) {
            SearchSheet(
                state = searchSheetState,
                action = rushViewModel::onSearchSheetAction,
                onClick = {
                    navController.navigate(Route.LyricsPage) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}