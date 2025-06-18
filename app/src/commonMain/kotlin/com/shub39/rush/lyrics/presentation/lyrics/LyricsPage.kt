package com.shub39.rush.lyrics.presentation.lyrics

import androidx.compose.runtime.Composable

@Composable
expect fun LyricsPage(
    onEdit: () -> Unit,
    onShare: () -> Unit,
    action: (LyricsPageAction) -> Unit,
    state: LyricsPageState,
    notificationAccess: Boolean
)