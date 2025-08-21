package com.shub39.rush.core.presentation

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

fun Modifier.zigZagBackground(
    zigZagHeight: Float = 20f,
    steps: Int = 20,
    lineColor: Color = Color.Gray.copy(alpha = 0.3f),
    strokeWidth: Float = 4f
): Modifier = this.then(
    Modifier.drawBehind {
        if (steps <= 0) return@drawBehind
        val stepWidth = size.width / steps.toFloat()
        val midY = size.height / 2f

        val path = Path().apply {
            moveTo(0f, midY)
            var x = 0f
            var up = true
            while (x < size.width) {
                val nextX = x + stepWidth
                val nextY = if (up) midY - zigZagHeight else midY + zigZagHeight
                lineTo(nextX, nextY)
                x = nextX
                up = !up
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = strokeWidth)
        )
    }
)