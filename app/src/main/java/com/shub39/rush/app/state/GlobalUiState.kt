package com.shub39.rush.app.state

data class GlobalUiState(
    val appState: GlobalState = GlobalState(),
    val overlayState: OverlayState = OverlayState()
)