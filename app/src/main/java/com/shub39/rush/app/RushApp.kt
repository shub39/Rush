package com.shub39.rush.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.core.domain.Route
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.core.presentation.findActivity
import com.shub39.rush.lyrics.data.listener.NotificationListener
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
            navController = navController,
            startDestination = Route.HomeGraph,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            navigation<Route.HomeGraph>(
                startDestination = Route.SavedPage
            ) {
                composable<Route.SavedPage> {
                    LaunchedEffect(Unit) {
                        val window = context.findActivity()?.window ?: return@LaunchedEffect
                        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

                        insetsController.apply {
                            show(WindowInsetsCompat.Type.systemBars())
                            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
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

                    if (lyricsState.searchSheet) {
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
                }
            }

            navigation<Route.LyricsGraph>(
                startDestination = Route.LyricsPage
            ) {
                composable<Route.SharePage> {
                    LaunchedEffect(Unit) {
                        val window = context.findActivity()?.window ?: return@LaunchedEffect
                        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

                        insetsController.apply {
                            show(WindowInsetsCompat.Type.systemBars())
                            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                        }
                    }

                    SharePage(
                        onDismiss = { navController.navigateUp() },
                        state = shareState,
                        action = shareVM::onAction
                    )
                }

                composable<Route.LyricsPage> {
                    LaunchedEffect(Unit) {
                        val window = context.findActivity()?.window ?: return@LaunchedEffect
                        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

                        insetsController.apply {
                            hide(WindowInsetsCompat.Type.systemBars())
                            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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
                        notificationAccess = NotificationListener.canAccessNotifications(context)
                    )

                    if (lyricsState.searchSheet) {
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
                }
            }

            navigation<Route.SettingsGraph>(
                startDestination = Route.SettingPage
            ) {
                composable<Route.SettingPage> {
                    SettingPage(
                        state = settingsState,
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

        if (!savedState.onboarding) {
            OnboardingDialog()
        }
    }
}