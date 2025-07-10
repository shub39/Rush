package com.shub39.rush

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object SavedPage: Route

    @Serializable
    data object LyricsGraph: Route

    @Serializable
    data object SettingsGraph: Route

    @Serializable
    data object OnboardingGraph: Route
}

@Composable
expect fun RushApp()