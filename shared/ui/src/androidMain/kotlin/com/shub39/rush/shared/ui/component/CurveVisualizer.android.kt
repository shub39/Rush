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

import android.graphics.RuntimeShader
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import com.shub39.rush.shared.core.dataclasses.WaveColors
import com.shub39.rush.shared.ui.hypnoticAvailable
import io.gitlab.bpavuk.viz.bassBucket
import io.gitlab.bpavuk.viz.midBucket
import io.gitlab.bpavuk.viz.trebleBucket
import kotlin.math.absoluteValue
import org.intellij.lang.annotations.Language

@Composable
actual fun CurveVisualizer(modifier: Modifier, waveData: List<Byte>?, colors: WaveColors) {
    if (!hypnoticAvailable()) {
        Box(modifier = modifier.background(Color(colors.cardWaveBackground)))
    } else {
        if (waveData.isNullOrEmpty()) return

        val bassBucket = waveData.bassBucket().map { it.toInt().absoluteValue }
        val midBucket = waveData.midBucket().map { it.toInt().absoluteValue }
        val trebleBucket = waveData.trebleBucket().map { it.toInt().absoluteValue }

        val bassMax by
            animateFloatAsState(
                targetValue = bassBucket.maxOrNull()?.toFloat() ?: 0f,
                animationSpec =
                    spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioNoBouncy,
                    ),
            )
        val midMax by
            animateFloatAsState(
                targetValue = midBucket.maxOrNull()?.toFloat() ?: 0f,
                animationSpec =
                    spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioNoBouncy,
                    ),
            )
        val trebleMax by
            animateFloatAsState(
                targetValue = trebleBucket.maxOrNull()?.toFloat() ?: 0f,
                animationSpec =
                    spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioNoBouncy,
                    ),
            )

        var startMillis = remember(colors) { -1L }
        val time by
            produceState(0f) {
                while (true) {
                    withInfiniteAnimationFrameMillis {
                        if (startMillis < 0) startMillis = it
                        value = ((it - startMillis) / 16.6f) / 10f
                    }
                }
            }
        val colorShader = RuntimeShader(sksl)
        val shaderBrush = ShaderBrush(colorShader)

        Canvas(modifier = modifier) {
            colorShader.setFloatUniform(
                "uResolution",
                size.width,
                size.height,
                size.width / size.height,
            )
            colorShader.setFloatUniform("uTime", time)
            colorShader.setFloatUniform(
                "cardWaveBackground",
                Color(colors.cardWaveBackground).red,
                Color(colors.cardWaveBackground).green,
                Color(colors.cardWaveBackground).blue,
            )
            colorShader.setFloatUniform(
                "cardBackground",
                Color(colors.cardBackground).red,
                Color(colors.cardBackground).green,
                Color(colors.cardBackground).blue,
            )
            colorShader.setFloatUniform("uBass", bassMax)
            colorShader.setFloatUniform("uMid", midMax)
            colorShader.setFloatUniform("uTreble", trebleMax)

            drawRect(brush = shaderBrush)
        }
    }
}

@Language("AGSL")
private val sksl: String =
    """
uniform float uTime;
uniform vec3 uResolution;

uniform vec3 cardBackground;
uniform vec3 cardWaveBackground;

uniform float uBass;
uniform float uMid;
uniform float uTreble;

vec4 main(vec2 fragCoord) {
    vec2 uv = fragCoord / uResolution.xy;

    float lineCount = 3.0;

    // Base line width + dynamic width from bass
    float baseWidth = 0.1;
    float lineWidth = baseWidth + uBass * 0.005; // bass expands line thickness

    // Distortion amplitude — more with mid/treble
    float distortAmp = (uMid * 0.001) + (uTreble * 0.001);

    // Create smooth pseudo-random distortion across x
    float distortion = sin(uv.x * 10.0 + uTime * 0.2) * distortAmp;
    distortion += sin(uv.x * 25.0 + uTime * 0.5) * distortAmp * 0.5;

    // Add distortion to the Y coordinate before line patterning
    float pattern = fract((uv.y + distortion) * lineCount);

    // Draw line (hard edge)
    float line = step(1.0 - lineWidth, pattern);

    // Mix colors
    vec3 color = mix(cardBackground, cardWaveBackground, line);

    return vec4(color, 1.0);
}
"""
