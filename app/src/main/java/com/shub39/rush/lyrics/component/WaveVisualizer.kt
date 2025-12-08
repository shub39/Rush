package com.shub39.rush.lyrics.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import com.shub39.rush.core.domain.WaveColors
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.presentation.RushTheme
import io.gitlab.bpavuk.viz.VisualizerData
import io.gitlab.bpavuk.viz.VisualizerState
import io.gitlab.bpavuk.viz.bassBucket
import io.gitlab.bpavuk.viz.midBucket
import io.gitlab.bpavuk.viz.rememberVisualizerState
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
        targetValue = bassBucket.maxOrNull()?.toFloat() ?: 0f,
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow, dampingRatio = Spring.DampingRatioNoBouncy)
    )
    val midMax by animateFloatAsState(
        targetValue = midBucket.maxOrNull()?.toFloat() ?: 0f,
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow, dampingRatio = Spring.DampingRatioNoBouncy)
    )
    val trebleMax by animateFloatAsState(
        targetValue = trebleBucket.maxOrNull()?.toFloat() ?: 0f,
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow, dampingRatio = Spring.DampingRatioNoBouncy)
    )

    Canvas(modifier) {
        val width = size.width
        val height = size.height / 2

        val path = Path().apply {
            moveTo(0f, size.height)
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
            lineTo(width, size.height)
            close()
        }

        drawPath(
            path = path,
            color = colors.cardWaveBackground
        )
    }
}

@Preview
@Composable
private fun Preview() {
    val waveData = rememberVisualizerState().let { state ->
        if (state !is VisualizerState.Ready) return@let null

        state.fft
    }

    RushTheme(
        theme = Theme()
    ) {
        WaveVisualizer(
            waveData = waveData,
            colors = WaveColors(
                cardBackground = MaterialTheme.colorScheme.background,
                cardWaveBackground = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxSize(),
        )
    }
}