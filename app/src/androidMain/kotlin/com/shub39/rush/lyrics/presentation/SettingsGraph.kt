package com.shub39.rush.lyrics.presentation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.lyrics.presentation.setting.AboutAppPage
import com.shub39.rush.lyrics.presentation.setting.AboutLibrariesPage
import com.shub39.rush.lyrics.presentation.setting.BackupPage
import com.shub39.rush.lyrics.presentation.setting.BatchDownloader
import com.shub39.rush.lyrics.presentation.setting.LookAndFeelPage
import com.shub39.rush.lyrics.presentation.setting.SettingRootPage
import com.shub39.rush.lyrics.presentation.setting.SettingsPageAction
import com.shub39.rush.lyrics.presentation.setting.SettingsPageState
import kotlinx.serialization.Serializable

sealed interface SettingsRoutes {
    @Serializable
    data object SettingRootPage : SettingsRoutes

    @Serializable
    data object BatchDownloaderPage : SettingsRoutes

    @Serializable
    data object BackupPage : SettingsRoutes

    @Serializable
    data object AboutPage : SettingsRoutes

    @Serializable
    data object LookAndFeelPage : SettingsRoutes

    @Serializable
    data object AboutLibrariesPage : SettingsRoutes
}

@Composable
fun SettingsGraph(
    notificationAccess: Boolean,
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SettingsRoutes.SettingRootPage,
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
        }
    ) {
        composable<SettingsRoutes.SettingRootPage> {
            SettingRootPage(
                notificationAccess = notificationAccess,
                action = action,
                navigator = { navController.navigate(it) { launchSingleTop = true } }
            )
        }

        composable<SettingsRoutes.BatchDownloaderPage> {
            BatchDownloader(
                state = state,
                action = action
            )
        }

        composable<SettingsRoutes.BackupPage> {
            BackupPage(
                state = state,
                action = action
            )
        }

        composable<SettingsRoutes.AboutPage> {
            AboutAppPage(
                onNavigateToLibraries = {
                    navController.navigate(SettingsRoutes.AboutLibrariesPage) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<SettingsRoutes.LookAndFeelPage> {
            LookAndFeelPage(
                state = state,
                action = action
            )
        }

        composable<SettingsRoutes.AboutLibrariesPage> {
            AboutLibrariesPage()
        }
    }
}