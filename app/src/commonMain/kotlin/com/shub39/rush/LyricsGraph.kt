package com.shub39.rush

import kotlinx.serialization.Serializable

sealed interface LyricsRoutes {
    @Serializable
    data object LyricsPage : LyricsRoutes

    @Serializable
    data object LyricsCustomisations : LyricsRoutes

    @Serializable
    data object SharePage : LyricsRoutes
}

// TODO: After moving share page to common
//expect fun LyricsGraph(
//    notificationAccess: Boolean,
//    lyricsState: LyricsPageState,
//    shareState: SharePageState,
//    lyricsAction: (LyricsPageAction) -> Unit,
//    shareAction: (SharePageAction) -> Unit
//)