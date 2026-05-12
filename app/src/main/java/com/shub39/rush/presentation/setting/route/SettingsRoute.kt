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
package com.shub39.rush.presentation.setting.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shub39.rush.presentation.setting.SettingsGraph
import com.shub39.rush.viewmodels.SettingsVM
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoute(
    notificationAccess: Boolean,
    fossWarningDaysLeft: Int?,
    isProUser: Boolean,
    onNavigateBack: () -> Unit,
    onShowPaywall: () -> Unit,
) {
    val settingsVM: SettingsVM = koinViewModel()
    val settingsState by settingsVM.state.collectAsStateWithLifecycle()

    SettingsGraph(
        notificationAccess = notificationAccess,
        fossWarningDaysLeft = fossWarningDaysLeft,
        state = settingsState,
        action = settingsVM::onAction,
        onNavigateBack = onNavigateBack,
        isProUser = isProUser,
        onShowPaywall = onShowPaywall,
    )
}
