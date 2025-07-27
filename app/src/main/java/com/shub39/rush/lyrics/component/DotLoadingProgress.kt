package com.shub39.rush.lyrics.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

@Composable
fun DotLoadingProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    val clampedProgress = progress.coerceIn(0f, 1f)

    val dotCount = 3
    val dotSpacing = 16.dp

    val dotProgresses = List(dotCount) { index ->
        ((clampedProgress - (index * 0.15f)) * 1.4f).coerceIn(0f, 1f)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dotSpacing)
    ) {
        dotProgresses.forEach { dotProgress ->
            val animatedScale by animateFloatAsState(
                targetValue = lerp(1f, 1.8f, dotProgress),
                animationSpec = tween(300, easing = LinearEasing),
                label = "dotScale"
            )

            val animatedAlpha by animateFloatAsState(
                targetValue = lerp(0.3f, color.alpha, dotProgress),
                animationSpec = tween(300, easing = LinearEasing),
                label = "dotAlpha"
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                    }
                    .background(color.copy(alpha = animatedAlpha), shape = CircleShape)
            )
        }
    }
}


