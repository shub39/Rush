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

