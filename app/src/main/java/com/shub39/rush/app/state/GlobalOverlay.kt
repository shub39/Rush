package com.shub39.rush.app.state

sealed interface GlobalOverlay {
    data object None : GlobalOverlay

    data class FossWarning(
        val daysLeft: Int
    ): GlobalOverlay

    data class Changelog(
        val changelog: VersionEntry
    ) : GlobalOverlay
}