package com.shub39.rush.presentation.onboarding.route

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.shub39.rush.app.GlobalAction
import com.shub39.rush.presentation.onboarding.Onboarding

@Composable
fun OnboardingRoute(
    notificationAccess: Boolean,
    onDone: () -> Unit,
    onGlobalAction: (GlobalAction) -> Unit,
) {
    Onboarding(
        onDone = onDone,
        notificationAccess = notificationAccess,
        onUpdateNotificationAccess = {
            onGlobalAction(GlobalAction.OnCheckNotificationAccess)
        },
    )
}

