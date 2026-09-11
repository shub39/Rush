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
package com.shub39.rush.shared.ui.lyrics.section

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import com.shub39.rush.shared.ui.lyrics.LyricsPageState
import com.shub39.rush.shared.ui.lyrics.PlaybackInfo
import io.gitlab.bpavuk.viz.VisualizerState
import io.gitlab.bpavuk.viz.rememberVisualizerState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun LyricsPage(
    modifier: Modifier,
    onNavigateToCustomisations: () -> Unit,
    onShare: () -> Unit,
    action: (LyricsPageAction) -> Unit,
    state: LyricsPageState,
    playbackInfo: PlaybackInfo,
    notificationAccess: Boolean,
) {
    val microphonePermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val waveData =
        rememberVisualizerState(microphonePermission.status.isGranted).let { state ->
            if (state !is VisualizerState.Ready) return@let null

            state.fft
        }

    LyricsPageContent(
        notificationAccess = notificationAccess,
        playbackInfo = playbackInfo,
        onShare = onShare,
        waveData = waveData,
        modifier = modifier,
        onNavigateToCustomisations = onNavigateToCustomisations,
        action = action,
        state = state,
    )
}
