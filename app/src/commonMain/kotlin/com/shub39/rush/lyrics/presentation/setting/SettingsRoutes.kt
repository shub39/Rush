package com.shub39.rush.lyrics.presentation.setting

import kotlinx.serialization.Serializable

sealed interface SettingsRoutes {
    @Serializable
    data object SettingRootPage : SettingsRoutes

    @Serializable
    data object BackupPage : SettingsRoutes

    @Serializable
    data object LookAndFeelPage : SettingsRoutes

    @Serializable
    data object AboutLibrariesPage : SettingsRoutes
}