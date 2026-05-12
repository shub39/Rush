package com.shub39.rush.app.state

import androidx.compose.runtime.Stable

@Stable
data class OverlayState(
    val changelog: VersionEntry? = null,
    val showWarningDialog: Boolean = false,
)