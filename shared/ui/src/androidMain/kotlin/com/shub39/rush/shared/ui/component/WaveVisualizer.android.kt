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
package com.shub39.rush.shared.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.shub39.rush.shared.core.dataclasses.WaveColors
import io.gitlab.bpavuk.viz.bassBucket
import io.gitlab.bpavuk.viz.midBucket
import io.gitlab.bpavuk.viz.trebleBucket
import kotlin.math.absoluteValue

// TODO: handle errors more gracefully. among solutions:
//  - make waveData a part of LyricsState or, even better, make it globally accessible
//  - provide fallback animation

/**
 * Wave visualizer. Intended to be used as a background for currently playing music.
 *
 * @param waveData - A list of Int values within a 0..128 range. Each value represents a separate
 *   wave point
 */
@Composable
actual fun WaveVisualizer(waveData: List<Byte>?, colors: WaveColors, modifier: Modifier) {
    if (waveData.isNullOrEmpty()) return

    val bassBucket = waveData.bassBucket().map { it.toInt().absoluteValue }
    val midBucket = waveData.midBucket().map { it.toInt().absoluteValue }
    val trebleBucket = waveData.trebleBucket().map { it.toInt().absoluteValue }

    val bassMax by
        animateFloatAsState(
            targetValue = bassBucket.maxOrNull()?.toFloat() ?: 0f,
            animationSpec =
                spring(
                    stiffness = Spring.StiffnessVeryLow,
                    dampingRatio = Spring.DampingRatioNoBouncy,
                ),
        )
    val midMax by
        animateFloatAsState(
            targetValue = midBucket.maxOrNull()?.toFloat() ?: 0f,
            animationSpec =
                spring(
                    stiffness = Spring.StiffnessVeryLow,
                    dampingRatio = Spring.DampingRatioNoBouncy,
                ),
        )
    val trebleMax by
        animateFloatAsState(
            targetValue = trebleBucket.maxOrNull()?.toFloat() ?: 0f,
            animationSpec =
                spring(
                    stiffness = Spring.StiffnessVeryLow,
                    dampingRatio = Spring.DampingRatioNoBouncy,
                ),
        )

    Canvas(modifier) {
        val width = size.width
        val height = size.height / 2

        val path =
            Path().apply {
                moveTo(0f, size.height)
                lineTo(0f, height * (1 - midMax / 128f))
                cubicTo(
                    width / 4,
                    height * (1 - midMax / 128f),
                    width / 4,
                    height * (1 - bassMax / 128f),
                    width / 2,
                    height * (1 - bassMax / 128f),
                )
                cubicTo(
                    width * 3 / 4,
                    height * (1 - bassMax / 128f),
                    width * 3 / 4,
                    height * (1 - trebleMax / 128f),
                    width,
                    height * (1 - trebleMax / 128f),
                )
                lineTo(width, size.height)
                close()
            }

        drawPath(path = path, color = Color(colors.cardWaveBackground))
    }
}
