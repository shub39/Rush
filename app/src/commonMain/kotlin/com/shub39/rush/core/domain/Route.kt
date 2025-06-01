package com.shub39.rush.core.domain

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object SavedPage: Route

    @Serializable
    data object LyricsGraph: Route

    @Serializable
    data object SettingsGraph: Route

    companion object {
        val allRoutes = listOf(SavedPage, LyricsGraph, SettingsGraph)
    }
}