package com.shub39.rush.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.presentation.RushTheme
import kotlinx.serialization.Serializable

@Serializable
private sealed interface OnboardingRoutes {
    data object Welcome: OnboardingRoutes
    data object ShareDemo: OnboardingRoutes
    data object Permissions: OnboardingRoutes
}

@Composable
fun OnboardingGraph(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = OnboardingRoutes.Welcome
    ) {

    }
}

@Preview
@Composable
private fun Preview() {
    RushTheme(
        state = Theme()
    ) {
        OnboardingGraph(
            state = OnboardingState(),
            onAction = {}
        )
    }
}