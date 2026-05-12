package com.shub39.rush.presentation.onboarding.route

import androidx.compose.runtime.Composable
import com.shub39.rush.presentation.onboarding.Onboarding

@Composable
fun OnboardingRoute(
    notificationAccess: Boolean,
    onDone: () -> Unit,
) {
    Onboarding(
        onDone = onDone,
        notificationAccess = notificationAccess,
    )
}

