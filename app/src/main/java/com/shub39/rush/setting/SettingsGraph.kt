package com.shub39.rush.setting

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Serializable
private sealed interface SettingsRoutes {
    @Serializable
    data object SettingRootPage : SettingsRoutes

    @Serializable
    data object BackupPage : SettingsRoutes

    @Serializable
    data object LookAndFeelPage : SettingsRoutes

    @Serializable
    data object AboutLibrariesPage : SettingsRoutes
}

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
                onNavigateBack = onNavigateBack,
                onNavigateToLookAndFeel = { navController.navigate(SettingsRoutes.LookAndFeelPage) },
                onNavigateToBackup = { navController.navigate(SettingsRoutes.BackupPage) },
                onNavigateToAboutLibraries = { navController.navigate(SettingsRoutes.AboutLibrariesPage) },
                state = state,
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