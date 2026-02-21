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
package com.shub39.rush.presentation.setting

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.presentation.components.RushTheme
import com.shub39.rush.presentation.setting.section.BackupPage
import com.shub39.rush.presentation.setting.section.LookAndFeelPage
import com.shub39.rush.presentation.setting.section.SettingRootPage
import kotlinx.serialization.Serializable

@Serializable
private sealed interface SettingsRoutes {
    @Serializable data object SettingRootPage : SettingsRoutes

    @Serializable data object BackupPage : SettingsRoutes

    @Serializable data object LookAndFeelPage : SettingsRoutes
}

@Composable
fun SettingsGraph(
    notificationAccess: Boolean,
    state: SettingsPageState,
    isProUser: Boolean,
    action: (SettingsPageAction) -> Unit,
    onNavigateBack: () -> Unit,
    onShowPaywall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SettingsRoutes.SettingRootPage,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() },
        modifier = modifier,
    ) {
        composable<SettingsRoutes.SettingRootPage> {
            SettingRootPage(
                notificationAccess = notificationAccess,
                onAction = action,
                onNavigateBack = onNavigateBack,
                onNavigateToLookAndFeel = {
                    navController.navigate(SettingsRoutes.LookAndFeelPage)
                },
                onNavigateToBackup = { navController.navigate(SettingsRoutes.BackupPage) },
                state = state,
                onShowPaywall = onShowPaywall,
            )
        }

        composable<SettingsRoutes.BackupPage> {
            BackupPage(
                state = state,
                action = action,
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<SettingsRoutes.LookAndFeelPage> {
            LookAndFeelPage(
                state = state,
                onAction = action,
                onNavigateBack = { navController.navigateUp() },
                onShowPaywall = onShowPaywall,
                isProUser = isProUser,
            )
        }
    }
}

@Preview(device = "spec:width=673dp,height=841dp")
@Composable
private fun Preview() {
    RushTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        SettingsGraph(
            notificationAccess = true,
            state = SettingsPageState(),
            action = {},
            onNavigateBack = {},
            isProUser = true,
            onShowPaywall = {},
        )
    }
}
