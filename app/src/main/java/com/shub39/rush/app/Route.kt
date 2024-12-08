package com.shub39.rush.app

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object RushGraph: Route

    @Serializable
    data object LyricsGraph: Route

    @Serializable
    data object LyricsPage: Route

    @Serializable
    data object SavedPage: Route

    @Serializable
    data object SharePage: Route

    @Serializable
    data object SettingPage: Route
}