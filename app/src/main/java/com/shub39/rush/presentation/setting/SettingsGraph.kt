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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.util.pop
import com.shub39.rush.navigation.horizontalTransitionMetadata
import com.shub39.rush.presentation.setting.section.BackupPage
import com.shub39.rush.presentation.setting.section.Changelog
import com.shub39.rush.presentation.setting.section.LookAndFeelPage
import com.shub39.rush.presentation.setting.section.SettingRootPage
import com.shub39.rush.presentation.theme.RushTheme
import kotlinx.serialization.Serializable

@Serializable data object SettingRootPage : NavKey

@Serializable data object BackupPage : NavKey

@Serializable data object LookAndFeelPage : NavKey

@Serializable data object ChangelogPage : NavKey

@Composable
fun SettingsGraph(
    notificationAccess: Boolean,
    fossWarningDaysLeft: Int?,
    state: SettingsPageState,
    isProUser: Boolean,
    action: (SettingsPageAction) -> Unit,
    onNavigateBack: () -> Unit,
    onShowPaywall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(SettingRootPage)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryProvider =
            entryProvider {
                entry<SettingRootPage> {
                    SettingRootPage(
                        notificationAccess = notificationAccess,
                        fossWarningDaysLeft = fossWarningDaysLeft,
                        onAction = action,
                        onNavigateBack = onNavigateBack,
                        onNavigateToLookAndFeel = { backStack.add(LookAndFeelPage) },
                        onNavigateToBackup = { backStack.add(BackupPage) },
                        onNavigateToChangelog = { backStack.add(ChangelogPage) },
                        state = state,
                        onShowPaywall = onShowPaywall,
                    )
                }

                entry<BackupPage>(metadata = horizontalTransitionMetadata()) {
                    BackupPage(
                        state = state,
                        action = action,
                        onNavigateBack = { backStack.pop() },
                    )
                }

                entry<LookAndFeelPage>(metadata = horizontalTransitionMetadata()) {
                    LookAndFeelPage(
                        state = state,
                        onAction = action,
                        onNavigateBack = { backStack.pop() },
                        onShowPaywall = onShowPaywall,
                        isProUser = isProUser,
                    )
                }

                entry<ChangelogPage>(metadata = horizontalTransitionMetadata()) {
                    Changelog(
                        changelog = state.changelog,
                        onNavigateBack = { backStack.pop() },
                    )
                }
            },
    )
}

@Preview
@Composable
private fun Preview() {
    RushTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        SettingsGraph(
            notificationAccess = true,
            fossWarningDaysLeft = 112,
            state = SettingsPageState(),
            action = {},
            onNavigateBack = {},
            isProUser = true,
            onShowPaywall = {},
        )
    }
}
