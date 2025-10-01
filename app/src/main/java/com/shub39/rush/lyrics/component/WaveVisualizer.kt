package com.shub39.rush.lyrics.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import com.shub39.rush.core.presentation.WaveColors
import io.gitlab.bpavuk.viz.VisualizerData
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
 * @param waveData - A list of Int values within a 0..128 range. Each value represents
 *  a separate wave point
 */
@Composable
fun WaveVisualizer(
    waveData: VisualizerData?,
    colors: WaveColors,
    modifier: Modifier = Modifier
) {
    if (waveData == null || waveData.isEmpty()) return

    val bassBucket = waveData.bassBucket().map { it.toInt().absoluteValue }
    val midBucket = waveData.midBucket().map { it.toInt().absoluteValue }
    val trebleBucket = waveData.trebleBucket().map { it.toInt().absoluteValue }
    val bassMax by animateFloatAsState(
        targetValue = bassBucket.max().toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow, dampingRatio = Spring.DampingRatioLowBouncy)
    )
    val midMax by animateFloatAsState(
        targetValue = midBucket.max().toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow, dampingRatio = Spring.DampingRatioLowBouncy)
    )
    val trebleMax by animateFloatAsState(
        targetValue = trebleBucket.max().toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow, dampingRatio = Spring.DampingRatioLowBouncy)
    )

    Canvas(modifier) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(0f, height)
            lineTo(0f, height * (1 - midMax / 128f))
            cubicTo(
                width / 4, height * (1 - midMax / 128f),
                width / 4, height * (1 - bassMax / 128f),
                width / 2, height * (1 - bassMax / 128f)
            )
            cubicTo(
                width * 3 / 4, height * (1 - bassMax / 128f),
                width * 3 / 4, height * (1 - trebleMax / 128f),
                width, height * (1 - trebleMax / 128f)
            )
            lineTo(width, height)
            close()
        }

        val brush = Brush.verticalGradient(
            0f to colors.cardBackground,
            0.5f to colors.cardWaveBackground.copy(alpha = 0.5f),
            1f to colors.cardWaveBackground,
        )
        drawPath(path = path, brush = brush)
    }
}