package com.shub39.rush.core.presentation

import android.graphics.RuntimeShader
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush

@Composable
fun HypnoticVisualizer(
    modifier: Modifier = Modifier,
    colors: List<Color>
) {
    if (!hypnoticAvailable()) {
        Box(
            modifier = modifier.background(
                brush = Brush.verticalGradient(colors)
            )
        )
    } else {
        var startMillis = remember(colors) { -1L }
        val time by produceState(0f) {
            while (true) {
                withInfiniteAnimationFrameMillis {
                    if (startMillis < 0) startMillis = it
                    value = ((it - startMillis) / 16.6f) / 10f
                }
            }
        }
        val colorShader = RuntimeShader(
            getSksl(colorCount = colors.size)
        )
        val shaderBrush = ShaderBrush(colorShader)
        val colorUniforms = colors.flatMap {
            listOf(it.red, it.green, it.blue)
        }.toTypedArray().toFloatArray()

        Canvas(
            modifier = modifier
        ) {
            colorShader.setFloatUniform(
                "uResolution",
                size.width,
                size.height,
                size.width / size.height
            )
            colorShader.setFloatUniform("uTime", time)
            colorShader.setFloatUniform("uColor", colorUniforms)

            drawRect(brush = shaderBrush)
        }
    }
}

private fun getSksl(
    colorCount: Int
): String = """
    uniform float uTime;
    uniform vec3 uResolution;

    vec3 vColor;
    const int MAX_COLORS = ${colorCount};
    uniform vec3 uColor[MAX_COLORS];

    vec4 main(vec2 fragCoord) {
        vec2 uv = fragCoord.xy / uResolution.xy;
        float t = uTime * 0.1;
        
        vec3 finalColor = vec3(0.0);
        float totalWeight = 0.0;

        for (int i = 0; i < MAX_COLORS; i++) {
            float fi = float(i);
            float wave = sin(uv.y * 3.0 + t + fi * 2.0 * 3.14159 / float(MAX_COLORS));
            wave = (wave + 1.0) * 0.5;
            float weight = pow(wave, 4.0);
            finalColor += uColor[i] * weight;
            totalWeight += weight;
        }
        
        return vec4(finalColor / totalWeight, 1.0);
    }
"""