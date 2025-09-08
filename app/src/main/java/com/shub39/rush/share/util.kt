package com.shub39.rush.share

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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

@OptIn(ExperimentalTime::class)
fun getFormattedTime(): String {
    val now = Clock.System.now()
    val localTime = now.toLocalDateTime(TimeZone.currentSystemDefault()).time

    val hour = localTime.hour % 12
    val minute = localTime.minute
    val amPm = if (localTime.hour < 12) "AM" else "PM"

    val hourFormatted = if (hour == 0) 12 else hour
    val minuteFormatted = minute.toString().padStart(2, '0')

    return "$hourFormatted:$minuteFormatted $amPm"
}