package com.shub39.rush.presentation.lyrics.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shub39.rush.presentation.lyrics.LyricsGraph as LyricsGraphScreen
import com.shub39.rush.viewmodels.LyricsVM
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LyricsRoute(
    notificationAccess: Boolean,
    onShare: () -> Unit,
) {
    val lyricsVM: LyricsVM = koinViewModel()
    val lyricsState by lyricsVM.state.collectAsStateWithLifecycle()
    val playbackInfo by lyricsVM.playbackInfo.collectAsStateWithLifecycle()

    LyricsGraphScreen(
        notificationAccess = notificationAccess,
        lyricsState = lyricsState,
        lyricsAction = lyricsVM::onAction,
        playbackInfo = playbackInfo,
        onShare = onShare,
    )
}

