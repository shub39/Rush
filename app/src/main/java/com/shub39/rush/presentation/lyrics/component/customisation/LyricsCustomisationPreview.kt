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
package com.shub39.rush.presentation.lyrics.component.customisation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.domain.dataclasses.Lyric
import com.shub39.rush.domain.dataclasses.WaveColors
import com.shub39.rush.domain.enums.LyricsBackground
import com.shub39.rush.presentation.getRandomLine
import com.shub39.rush.presentation.lyrics.ApplyLyricsBackground
import com.shub39.rush.presentation.lyrics.LyricsPageState
import com.shub39.rush.presentation.lyrics.LyricsState
import com.shub39.rush.presentation.lyrics.component.PlainLyric
import com.shub39.rush.presentation.lyrics.component.SyncedLyric
import io.gitlab.bpavuk.viz.VisualizerData

@Composable
fun LyricsCustomisationPreview(
    state: LyricsPageState,
    isShowingSynced: Boolean,
    cardBackground: Color,
    cardContent: Color,
    waveData: VisualizerData?,
    hypnoticColor1: Color,
    hypnoticColor2: Color,
    waveColors: WaveColors,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(containerColor = cardBackground, contentColor = cardContent),
        shape = RoundedCornerShape(16.dp),
    ) {
        BoxWithConstraints {
            ApplyLyricsBackground(
                background = state.lyricsBackground,
                artUrl = (state.lyricsState as? LyricsState.Loaded)?.song?.artUrl,
                cardBackground = cardBackground,
                waveData = waveData,
                waveColors = waveColors,
                hypnoticColor1 = hypnoticColor1,
                hypnoticColor2 = hypnoticColor2,
            )

            AnimatedContent(targetState = isShowingSynced, modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            with(LocalDensity.current) { state.textPrefs.lineHeight.sp.toDp() / 2 }
                        ),
                ) {
                    if (it) {
                        val lines by remember { mutableStateOf((1..2).map { getRandomLine() }) }

                        SyncedLyric(
                            textPrefs = state.textPrefs,
                            blur = if (state.blurSyncedLyrics) 2.dp else 0.dp,
                            action = {},
                            lyric = Lyric(1L, lines.first()),
                            underTextAlpha = 0.2f,
                            textColor = cardContent,
                            animatedProgress = 1f,
                            glowAlpha = 0f,
                            scale = 0.8f,
                        )
                        SyncedLyric(
                            textPrefs = state.textPrefs,
                            blur = 0.dp,
                            action = {},
                            lyric = Lyric(1L, lines.last()),
                            underTextAlpha = 0.5f,
                            textColor = cardContent,
                            animatedProgress = 0.5f,
                            glowAlpha = if (state.blurSyncedLyrics) 2f else 0f,
                            scale = 1f,
                        )
                        SyncedLyric(
                            textPrefs = state.textPrefs,
                            blur = 0.dp,
                            action = {},
                            lyric = Lyric(1L, ""),
                            underTextAlpha = 0.5f,
                            textColor = cardContent,
                            animatedProgress = 0.5f,
                            glowAlpha = 0f,
                            scale = 0.8f,
                        )
                    } else {
                        PlainLyric(
                            entry =
                                1 to
                                    "This is a very very long text depicting how lyrics should appear based on these settings",
                            textPrefs = state.textPrefs,
                            onClick = {},
                            containerColor =
                                if (state.lyricsBackground != LyricsBackground.SOLID_COLOR)
                                    Color.Transparent
                                else cardBackground,
                            cardContent = cardContent,
                        )
                    }
                }
            }
        }
    }
}
