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
package com.shub39.rush.shared.ui.lyrics

import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.lyrics.section.LyricsCustomisationsPage
import com.shub39.rush.shared.ui.lyrics.section.LyricsPage
import com.shub39.rush.shared.ui.navigation.horizontalTransitionMetadata
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed interface LyricsRoutes : NavKey {
    @Serializable data object LyricsPage : LyricsRoutes

    @Serializable data object LyricsCustomisations : LyricsRoutes

    companion object {
        val config = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(LyricsPage::class, LyricsPage.serializer())
                    subclass(LyricsCustomisations::class, LyricsCustomisations.serializer())
                }
            }
        }
    }
}

@Composable expect fun ManageSystemBars(fullscreen: Boolean)

@Composable
expect fun LyricsGraph(
    notificationAccess: Boolean,
    lyricsState: LyricsPageState,
    playbackInfo: PlaybackInfo,
    lyricsAction: (LyricsPageAction) -> Unit,
    onShare: () -> Unit,
)

@Composable
fun LyricsGraphContent(
    notificationAccess: Boolean,
    lyricsState: LyricsPageState,
    playbackInfo: PlaybackInfo,
    lyricsAction: (LyricsPageAction) -> Unit,
    onShare: () -> Unit,
    waveData: List<Byte>?,
    micPermission: Boolean,
    onMicPermissionGranted: () -> Unit,
) {
    val backStack = rememberNavBackStack(LyricsRoutes.config, LyricsRoutes.LyricsPage)

    NavDisplay(
        backStack = backStack,
        entryProvider =
            entryProvider {
                entry<LyricsRoutes.LyricsPage> {
                    ManageSystemBars(lyricsState.fullscreen)

                    LyricsPage(
                        onNavigateToCustomisations = {
                            backStack.add(LyricsRoutes.LyricsCustomisations)
                        },
                        onShare = onShare,
                        action = lyricsAction,
                        state = lyricsState,
                        playbackInfo = playbackInfo,
                        notificationAccess = notificationAccess,
                        waveData = waveData,
                    )
                }

                entry<LyricsRoutes.LyricsCustomisations>(
                    metadata = horizontalTransitionMetadata()
                ) {
                    LyricsCustomisationsPage(
                        state = lyricsState,
                        onNavigateBack = { if (backStack.size != 1) backStack.removeLastOrNull() },
                        onAction = lyricsAction,
                        notificationAccess = notificationAccess,
                        microphonePermission = micPermission,
                        requestMicrophonePermission = onMicPermissionGranted,
                        waveData = waveData,
                    )
                }
            },
    )
}

@PreviewWrapper(RushPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    var state by remember { mutableStateOf(LyricsPageState()) }

    LyricsGraph(
        notificationAccess = true,
        lyricsState = state,
        lyricsAction = {},
        onShare = {},
        playbackInfo = PlaybackInfo(),
    )
}
