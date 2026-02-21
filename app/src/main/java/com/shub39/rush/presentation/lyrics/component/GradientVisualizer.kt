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
package com.shub39.rush.presentation.lyrics.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.shub39.rush.domain.dataclasses.WaveColors
import io.gitlab.bpavuk.viz.VisualizerData
import io.gitlab.bpavuk.viz.bassBucket
import kotlin.math.absoluteValue

/**
 * Gradient visualizer. Intended to be used as a background for currently playing music.
 *
 * @param waveData - A list of Byte values within a -128..127 range. Each value represents a
 *   separate wave point
 * @param modifier - usual modifier
 */
@Composable
fun GradientVisualizer(
    waveData: VisualizerData?,
    modifier: Modifier = Modifier,
    colors: WaveColors,
) {
    if (waveData.isNullOrEmpty()) return

    val bassBucket = waveData.bassBucket().map { it.toInt().absoluteValue }

    val bassMax by
        animateFloatAsState(
            targetValue = bassBucket.max().toFloat(),
            animationSpec = spring(stiffness = 25f),
        )

    Canvas(modifier) {
        val brush =
            Brush.verticalGradient(
                0f to Color(colors.cardBackground),
                1f - (bassMax / 128f) to Color(colors.cardWaveBackground),
                1f to Color(colors.cardWaveBackground),
            )

        drawRect(brush = brush)
    }
}
