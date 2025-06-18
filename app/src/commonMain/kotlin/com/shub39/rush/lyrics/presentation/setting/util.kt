package com.shub39.rush.lyrics.presentation.setting

import androidx.compose.foundation.lazy.LazyListScope

expect fun LazyListScope.materialYouToggle(
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit
)

expect fun LazyListScope.paletteStyles(
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit
)

expect fun LazyListScope.notificationAccessReminder(notificationAccess: Boolean)