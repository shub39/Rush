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
package com.shub39.rush.shared.ui.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.navigation.horizontalTransitionMetadata
import com.shub39.rush.shared.ui.setting.section.About
import com.shub39.rush.shared.ui.setting.section.BackupPage
import com.shub39.rush.shared.ui.setting.section.Changelog
import com.shub39.rush.shared.ui.setting.section.LookAndFeelPage
import com.shub39.rush.shared.ui.setting.section.SettingRootPage
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
private sealed interface Routes : NavKey {
    @Serializable data object Root : Routes

    @Serializable data object Backup : Routes

    @Serializable data object LookAndFeel : Routes

    @Serializable data object Changelog : Routes

    @Serializable data object About : Routes
}

private val configuration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Routes.Root::class, Routes.Root.serializer())
            subclass(Routes.Backup::class, Routes.Backup.serializer())
            subclass(Routes.LookAndFeel::class, Routes.LookAndFeel.serializer())
            subclass(Routes.Changelog::class, Routes.Changelog.serializer())
            subclass(Routes.About::class, Routes.About.serializer())
        }
    }
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
    val backStack = rememberNavBackStack(configuration, Routes.Root)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryProvider =
            entryProvider {
                entry<Routes.Root> {
                    SettingRootPage(
                        notificationAccess = notificationAccess,
                        onAction = action,
                        onNavigateBack = onNavigateBack,
                        onNavigateToLookAndFeel = { backStack.add(Routes.LookAndFeel) },
                        onNavigateToBackup = { backStack.add(Routes.Backup) },
                        onNavigateToChangelog = { backStack.add(Routes.Changelog) },
                        onNavigateToAppInfo = { backStack.add(Routes.About) },
                        state = state,
                        onShowPaywall = onShowPaywall,
                    )
                }

                entry<Routes.Backup>(metadata = horizontalTransitionMetadata()) {
                    BackupPage(
                        state = state,
                        onAction = action,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                    )
                }

                entry<Routes.LookAndFeel>(metadata = horizontalTransitionMetadata()) {
                    LookAndFeelPage(
                        state = state,
                        onAction = action,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                        onShowPaywall = onShowPaywall,
                        isProUser = isProUser,
                    )
                }

                entry<Routes.Changelog>(metadata = horizontalTransitionMetadata()) {
                    Changelog(
                        changelog = state.changelog,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                    )
                }

                entry<Routes.About>(metadata = horizontalTransitionMetadata()) {
                    About(
                        versionName = state.changelog.firstOrNull()?.version ?: "1.0.00",
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                    )
                }
            },
    )
}

@PreviewWrapper(RushPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    SettingsGraph(
        notificationAccess = true,
        state = SettingsPageState(),
        action = {},
        onNavigateBack = {},
        isProUser = true,
        onShowPaywall = {},
    )
}
