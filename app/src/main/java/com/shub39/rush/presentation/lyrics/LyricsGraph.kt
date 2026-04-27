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
package com.shub39.rush.presentation.lyrics

import android.Manifest
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.navigation.horizontalTransitionMetadata
import com.shub39.rush.presentation.theme.RushTheme
import com.shub39.rush.presentation.lyrics.section.LyricsCustomisationsPage
import com.shub39.rush.presentation.lyrics.section.LyricsPage
import com.shub39.rush.presentation.updateSystemBars
import io.gitlab.bpavuk.viz.VisualizerState
import io.gitlab.bpavuk.viz.rememberVisualizerState
import kotlinx.serialization.Serializable

@Serializable data object LyricsPage : NavKey

@Serializable data object LyricsCustomisations : NavKey

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LyricsGraph(
    notificationAccess: Boolean,
    lyricsState: LyricsPageState,
    playbackInfo: PlaybackInfo,
    lyricsAction: (LyricsPageAction) -> Unit,
    onShare: () -> Unit,
) {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(LyricsPage)

    val microphonePermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val waveData =
        rememberVisualizerState(microphonePermission.status.isGranted).let { state ->
            if (state !is VisualizerState.Ready) return@let null

            state.fft
        }

    NavDisplay(
        backStack = backStack,
        entryProvider =
            entryProvider {
                entry<LyricsPage> {
                    DisposableEffect(Unit) {
                        updateSystemBars(context, show = !lyricsState.fullscreen)
                        onDispose { updateSystemBars(context, show = true) }
                    }

                    LyricsPage(
                        onNavigateToCustomisations = { backStack.add(LyricsCustomisations) },
                        onShare = onShare,
                        action = lyricsAction,
                        state = lyricsState,
                        playbackInfo = playbackInfo,
                        notificationAccess = notificationAccess,
                        waveData = waveData,
                    )
                }

                entry<LyricsCustomisations>(metadata = horizontalTransitionMetadata()) {
                    LyricsCustomisationsPage(
                        state = lyricsState,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                        onAction = lyricsAction,
                        modifier = Modifier.widthIn(max = 700.dp),
                        notificationAccess = notificationAccess,
                        microphonePermission = microphonePermission.status.isGranted,
                        requestMicrophonePermission = {
                            microphonePermission.launchPermissionRequest()
                        },
                        waveData = waveData,
                    )
                }
            },
    )
}

@Preview
@Composable
private fun Preview() {
    var state by remember { mutableStateOf(LyricsPageState()) }

    RushTheme(theme = Theme()) {
        LyricsGraph(
            notificationAccess = true,
            lyricsState = state,
            lyricsAction = {},
            onShare = {},
            playbackInfo = PlaybackInfo(),
        )
    }
}
