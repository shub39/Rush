/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.presentation.lyrics.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shub39.rush.presentation.lyrics.LyricsGraph as LyricsGraphScreen
import com.shub39.rush.viewmodels.LyricsVM
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LyricsRoute(notificationAccess: Boolean, onShare: () -> Unit) {
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
