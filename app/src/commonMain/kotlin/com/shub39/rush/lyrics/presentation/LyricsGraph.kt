package com.shub39.rush.lyrics.presentation

import androidx.compose.runtime.Composable
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState
import com.shub39.rush.lyrics.presentation.share.SharePageAction
import com.shub39.rush.lyrics.presentation.share.SharePageState
import kotlinx.serialization.Serializable

@Serializable
sealed interface LyricsRoutes {
    @Serializable
    data object LyricsPage : LyricsRoutes

    @Serializable
    data object LyricsCustomisations : LyricsRoutes

    @Serializable
    data object SharePage : LyricsRoutes
}

@Composable
expect fun LyricsGraph(
    notificationAccess: Boolean,
    lyricsState: LyricsPageState,
    shareState: SharePageState,
    lyricsAction: (LyricsPageAction) -> Unit,
    shareAction: (SharePageAction) -> Unit
)