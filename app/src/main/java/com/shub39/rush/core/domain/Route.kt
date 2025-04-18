package com.shub39.rush.core.domain

import kotlinx.serialization.Serializable

sealed interface Route {
    // Home Routes
    @Serializable
    data object HomeGraph: Route
    @Serializable
    data object SavedPage: Route

    // Lyrics Routes
    @Serializable
    data object LyricsGraph: Route
    @Serializable
    data object LyricsPage: Route
    @Serializable
    data object LyricsCustomisations: Route
    @Serializable
    data object SharePage: Route

    // Settings and utility Routes
    @Serializable
    data object SettingsGraph: Route
    @Serializable
    data object SettingPage: Route
    @Serializable
    data object BatchDownloaderPage: Route
    @Serializable
    data object BackupPage: Route
    @Serializable
    data object AboutPage: Route
    @Serializable
    data object LookAndFeelPage: Route
    @Serializable
    data object AboutLibrariesPage: Route
}