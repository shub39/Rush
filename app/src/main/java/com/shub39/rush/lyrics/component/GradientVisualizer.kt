package com.shub39.rush.lyrics.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.shub39.rush.core.presentation.WaveColors
import io.gitlab.bpavuk.viz.VisualizerData
import io.gitlab.bpavuk.viz.bassBucket
import kotlin.math.absoluteValue

/**
 * Gradient visualizer. Intended to be used as a background for currently playing music.
 *
 * @param waveData - A list of Byte values within a -128..127 range. Each value represents
 *  a separate wave point
 * @param modifier - usual modifier
 */
@Composable
fun GradientVisualizer(
    waveData: VisualizerData?,
    modifier: Modifier = Modifier,
    colors: WaveColors,
) {
    if (waveData == null || waveData.isEmpty()) return

    val bassBucket = waveData.bassBucket().map { it.toInt().absoluteValue }

    val bassMax by animateFloatAsState(
        targetValue = bassBucket.max().toFloat(),
        animationSpec = spring(stiffness = 25f)
    )

    Canvas(modifier) {
        val brush = Brush.verticalGradient(
            0f to colors.cardBackground,
            1f - (bassMax / 128f) to colors.cardWaveBackground,
            1f to colors.cardWaveBackground
        )

        drawRect(brush = brush)
    }
}
