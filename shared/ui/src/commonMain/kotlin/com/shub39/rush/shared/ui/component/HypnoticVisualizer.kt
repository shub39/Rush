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

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.MeshGradientPainter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.generateGradientColors
import com.shub39.rush.shared.ui.lerp
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/** Number of mesh patches along the vertical axis. */
private const val ROWS = 3

/** Number of mesh patches along the horizontal axis. */
private const val COLUMNS = 3

/**
 * How far a vertex may drift, as a fraction of the drawing bounds. Kept below half of the grid
 * spacing (`1 / COLUMNS / 2`) so neighbouring vertices can never cross and invert a patch.
 */
private const val WOBBLE = 0.12f

/** Duration of one seamless animation loop. */
private const val LOOP_MILLIS = 18_000

/** Phase used when the animation is disabled, so the frozen frame still looks composed. */
private const val STATIC_PHASE = 0.17f

/**
 * Resolution divisor for the mesh.
 *
 * `MeshGradientPainter` subdivides per patch by pixel size (roughly one vertex every 8px, capped at
 * 64 per axis per patch) and computes every vertex on the UI thread. A full screen 3x3 mesh is ~26k
 * vertices per frame, which drops a mid-range device to ~12fps. A soft gradient survives upscaling,
 * so the mesh is rendered into a smaller layer and scaled back up.
 */
private const val DOWNSCALE = 4

private const val TAU = 6.2831855f

/**
 * A Composable that displays an animated mesh gradient, with vertices drifting and colours flowing
 * across the mesh.
 *
 * This is backed by [MeshGradientPainter], which is hardware accelerated from API 29 and is
 * available on every Compose Multiplatform target.
 *
 * The animation lives entirely in the draw phase: the [MeshGradientPainter] configuration block
 * reads the animated phase, so each frame only invalidates drawing and never triggers
 * recomposition.
 *
 * @param modifier The [Modifier] to be applied to this Composable.
 * @param colors The list of [Color]s sampled across the mesh. Any number of colours is supported.
 * @param animated Whether the mesh should animate. Pass `false` for deterministic output, such as a
 *   card that is recorded into a `GraphicsLayer` and exported as an image.
 */
@Composable
fun HypnoticVisualizer(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    animated: Boolean = true,
) {
    if (colors.isEmpty()) return

    val phase: State<Float>? = rememberAnimatedPhase(animated)

    val painter =
        remember(colors, phase) {
            MeshGradientPainter(rows = ROWS, columns = COLUMNS, hasBicubicColor = true) {
                // Read here (during draw) rather than in composition, so animating costs no
                // recomposition.
                val time = phase?.value ?: STATIC_PHASE

                for (row in 0..ROWS) {
                    for (column in 0..COLUMNS) {
                        val fx = column / COLUMNS.toFloat()
                        val fy = row / ROWS.toFloat()

                        // Frequencies are integer harmonics of the loop, so the sinusoids line up
                        // exactly at the wrap and the motion never visibly jumps. One cycle per
                        // loop keeps each frame's delta small enough to read as smooth even when
                        // the device only manages ~15fps.
                        val driftX = sin((time + fy * 1.5f + fx * 0.5f) * TAU) * WOBBLE
                        val driftY = sin((time + fx * 1.7f - fy * 0.4f) * TAU) * WOBBLE

                        setVertex(
                            row = row,
                            column = column,
                            // Vertices on an edge slide along it and corners stay pinned, so the
                            // mesh always covers the full bounds.
                            position =
                                Offset(
                                    x = if (column == 0 || column == COLUMNS) fx else fx + driftX,
                                    y = if (row == 0 || row == ROWS) fy else fy + driftY,
                                ),
                            // `wave` instead of `% 1f`: the modulo wrapped the last colour back
                            // to the first, snapping every vertex to a different colour twice per
                            // loop. A cosine is also C1 continuous, so no vertex's colour velocity
                            // reverses abruptly, which is what reads as a blob popping mid-mesh.
                            color = colors.sampleColor(wave(fy * 0.55f + fx * 0.45f + time)),
                        )
                    }
                }
            }
        }

    BoxWithConstraints(modifier = modifier) {
        if (constraints.hasBoundedWidth && constraints.hasBoundedHeight) {
            Box(
                modifier =
                    Modifier.requiredSize(maxWidth / DOWNSCALE, maxHeight / DOWNSCALE)
                        .graphicsLayer {
                            scaleX = DOWNSCALE.toFloat()
                            scaleY = DOWNSCALE.toFloat()
                            transformOrigin = TransformOrigin(0f, 0f)
                        }
                        .paint(painter)
            )
        } else {
            Box(modifier = Modifier.paint(painter))
        }
    }
}

/**
 * Drives the mesh phase from `0f` to `1f` forever, or returns `null` when [animated] is `false`.
 *
 * Returning `null` means no infinite transition is created at all, so a frozen visualizer does not
 * keep scheduling frames.
 */
@Composable
private fun rememberAnimatedPhase(animated: Boolean): State<Float>? {
    if (!animated) return null

    return rememberInfiniteTransition(label = "hypnotic")
        .animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = LOOP_MILLIS, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "phase",
        )
}

/**
 * Maps [t] onto `0f..1f` as a smooth cosine wave with a continuous first derivative.
 *
 * A triangle wave is continuous in value but not in velocity: each vertex's colour reverses
 * direction instantly at the apex, producing staggered pops across the mesh. A cosine is C1
 * continuous, so the colour flow stays liquid.
 */
private fun wave(t: Float): Float = (1f - cos(t * TAU)) / 2f

/** Samples this list of colours at [t] in `0f..1f`, interpolating between adjacent entries. */
private fun List<Color>.sampleColor(t: Float): Color {
    if (size == 1) return first()

    val scaled = t.coerceIn(0f, 1f) * (size - 1)
    val index = floor(scaled).toInt().coerceAtMost(size - 2)

    return lerp(this[index], this[index + 1], scaled - index)
}

@PreviewWrapper(RushPreviewWrapper::class)
@Preview(widthDp = 320, heightDp = 320)
@Composable
private fun HypnoticVisualizerPreview() {
    HypnoticVisualizer(
        modifier = Modifier.size(320.dp),
        colors = generateGradientColors(color1 = Color(0xFF0068B4), color2 = Color(0xFFB1D1F3)),
    )
}

@PreviewWrapper(RushPreviewWrapper::class)
@Preview(widthDp = 320, heightDp = 320)
@Composable
private fun HypnoticVisualizerFrozenPreview() {
    HypnoticVisualizer(
        modifier = Modifier.size(320.dp),
        colors = generateGradientColors(color1 = Color(0xFF0068B4), color2 = Color(0xFFB1D1F3)),
        animated = false,
    )
}
