package com.shub39.rush

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
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
import com.shub39.rush.core.presentation.updateSystemBars
import com.shub39.rush.lyrics.data.listener.NotificationListener
import com.shub39.rush.lyrics.presentation.lyrics.LyricsCustomisationsPage
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPage
import com.shub39.rush.lyrics.presentation.saved.SavedPage
import com.shub39.rush.lyrics.presentation.search_sheet.SearchSheet
import com.shub39.rush.lyrics.presentation.setting.About
import com.shub39.rush.lyrics.presentation.setting.AboutLibraries
import com.shub39.rush.lyrics.presentation.setting.Backup
import com.shub39.rush.lyrics.presentation.setting.BatchDownloader
import com.shub39.rush.lyrics.presentation.setting.LookAndFeel
import com.shub39.rush.lyrics.presentation.setting.SettingPage
import com.shub39.rush.lyrics.presentation.share.SharePage
import com.shub39.rush.lyrics.presentation.viewmodels.LyricsVM
import com.shub39.rush.lyrics.presentation.viewmodels.SavedVM
import com.shub39.rush.lyrics.presentation.viewmodels.SearchSheetVM
import com.shub39.rush.lyrics.presentation.viewmodels.SettingsVM
import com.shub39.rush.lyrics.presentation.viewmodels.ShareVM
import com.shub39.rush.onboarding.OnboardingDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun RushApp(
    settingsVM: SettingsVM = koinViewModel(),
    lyricsVM: LyricsVM = koinViewModel(),
    shareVM: ShareVM = koinViewModel(),
    searchSheetVM: SearchSheetVM = koinViewModel(),
    savedVM: SavedVM = koinViewModel()
) {
    val lyricsState by lyricsVM.state.collectAsStateWithLifecycle()
    val settingsState by settingsVM.state.collectAsStateWithLifecycle()
    val shareState by shareVM.state.collectAsStateWithLifecycle()
    val searchState by searchSheetVM.state.collectAsStateWithLifecycle()
    val savedState by savedVM.state.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val context = LocalContext.current

    RushTheme(
        state = settingsState.theme
    ) {
        NavHost(
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            },
            navController = navController,
            startDestination = Route.HomeGraph,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            navigation<Route.HomeGraph>(
                startDestination = Route.SavedPage,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() }
            ) {
                composable<Route.SavedPage> {
                    SideEffect {
                        if (lyricsState.fullscreen) {
                            updateSystemBars(context, true)
                        }
                    }

                    SavedPage(
                        state = savedState,
                        currentSong = lyricsState.song,
                        notificationAccess = NotificationListener.canAccessNotifications(context),
                        action = savedVM::onAction,
                        autoChange = lyricsState.autoChange,
                        navigator = {
                            navController.navigate(it) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            navigation<Route.LyricsGraph>(
                startDestination = Route.LyricsPage,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() }
            ) {
                composable<Route.SharePage> {
                    SideEffect {
                        if (lyricsState.fullscreen) {
                            updateSystemBars(context, true)
                        }
                    }

                    SharePage(
                        onDismiss = { navController.navigateUp() },
                        state = shareState,
                        action = shareVM::onAction
                    )
                }

                composable<Route.LyricsCustomisations> {
                    SideEffect {
                        if (lyricsState.fullscreen) {
                            updateSystemBars(context, true)
                        }
                    }

                    LyricsCustomisationsPage(
                        state = lyricsState,
                        action = lyricsVM::onAction
                    )
                }

                composable<Route.LyricsPage> {
                    SideEffect {
                        if (lyricsState.fullscreen) {
                            updateSystemBars(context, false)
                        }
                    }

                    LyricsPage(
                        state = lyricsState,
                        action = lyricsVM::onAction,
                        onShare = {
                            navController.navigate(Route.SharePage) {
                                launchSingleTop = true
                            }
                        },
                        onEdit = {
                            navController.navigate(Route.LyricsCustomisations) {
                                launchSingleTop = true
                            }
                        },
                        notificationAccess = NotificationListener.canAccessNotifications(context)
                    )
                }
            }

            navigation<Route.SettingsGraph>(
                startDestination = Route.SettingPage
            ) {
                composable<Route.SettingPage> {
                    SettingPage(
                        action = settingsVM::onSettingsPageAction,
                        notificationAccess = NotificationListener.canAccessNotifications(context),
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
                    About(
                        navigator = {
                            navController.navigate(it) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable<Route.LookAndFeelPage> {
                    LookAndFeel(
                        state = settingsState,
                        action = settingsVM::onSettingsPageAction
                    )
                }

                composable<Route.AboutLibrariesPage> {
                    AboutLibraries()
                }
            }
        }

        if (searchState.visible) {
            SearchSheet(
                state = searchState,
                action = searchSheetVM::onAction,
                onClick = {
                    navController.navigate(Route.LyricsPage) {
                        launchSingleTop = true
                    }
                }
            )
        }

        if (!savedState.onboarding) {
            OnboardingDialog()
        }
    }
}