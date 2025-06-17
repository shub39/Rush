package com.shub39.rush.lyrics

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.lyrics.presentation.setting.AboutLibrariesPage
import com.shub39.rush.lyrics.presentation.setting.BackupPage
import com.shub39.rush.lyrics.presentation.setting.LookAndFeelPage
import com.shub39.rush.lyrics.presentation.setting.SettingRootPage
import com.shub39.rush.lyrics.presentation.setting.SettingsPageAction
import com.shub39.rush.lyrics.presentation.setting.SettingsPageState
import com.shub39.rush.lyrics.presentation.setting.SettingsRoutes

@Composable
fun SettingsGraph(
    notificationAccess: Boolean,
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit,
    onNavigateBack: () -> Unit
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
                navigator = { navController.navigate(it) { launchSingleTop = true } },
                onNavigateBack = onNavigateBack
            )
        }

        composable<SettingsRoutes.BackupPage> {
            BackupPage(
                state = state,
                action = action,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<SettingsRoutes.LookAndFeelPage> {
            LookAndFeelPage(
                state = state,
                action = action,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<SettingsRoutes.AboutLibrariesPage> {
            AboutLibrariesPage(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}