package com.shub39.rush.lyrics.presentation.share

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.layer.GraphicsLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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

@Composable
expect fun ShareButton(
    coroutineScope: CoroutineScope,
    cardGraphicsLayer: GraphicsLayer
)