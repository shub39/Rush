package com.shub39.rush.presentation.share

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
fun pxToSp(px: Int): TextUnit {
    val density = LocalDensity.current
    return with(density) { px.toSp() }
}

@Composable
fun pxToDp(px: Int): Dp {
    val density = LocalDensity.current
    return with(density) { px.toDp() }
}

@Composable
fun TextStyle.fromPx(
    fontSize: Int,
    letterSpacing: Int,
    lineHeight: Int,
    fontWeight: FontWeight = FontWeight.Normal
): TextStyle {
    return copy(
        fontSize = pxToSp(fontSize),
        letterSpacing = pxToSp(letterSpacing),
        lineHeight = pxToSp(lineHeight),
        fontWeight = fontWeight
    )
}