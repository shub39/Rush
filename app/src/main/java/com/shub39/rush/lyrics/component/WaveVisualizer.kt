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

private const val TAG = "Wave"


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
    val bassMax by animateFloatAsState(
        targetValue = bassBucket.max().toFloat(),
        animationSpec = spring(stiffness = 25f)
    )

    Canvas(modifier) {
        // waveform visualization
        val brush = Brush.verticalGradient(
            0f to colors.cardBackground,
            (1f - (bassMax / 128f) - 0.05f) / 10f to colors.cardBackground,
            1f - (bassMax / 128f) + 0.05f to colors.cardWaveBackground,
            1f to colors.cardWaveBackground,
        )
        drawRect(brush)
    }
}