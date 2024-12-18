package com.shub39.rush.lyrics.presentation.setting

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlin.random.Random

@Composable
fun stateListColors(
    state: Boolean?
): ListItemColors {
    val cardContent by animateColorAsState(
        targetValue = when (state) {
            null -> MaterialTheme.colorScheme.primary
            true -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.error
        }, label = "status"
    )

    val cardBackground by animateColorAsState(
        targetValue = when (state) {
            null -> MaterialTheme.colorScheme.primaryContainer
            true -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.errorContainer
        }, label = "status"
    )

    return ListItemDefaults.colors(
        containerColor = cardBackground,
        headlineColor = cardContent,
        trailingIconColor = cardContent
    )
}

fun getRandomLine(): String {
    return when(Random.nextInt(0, 3)) {
        1 -> "Time will lead us to the same Realm"
        2 -> "Foot shook ground when I stepped on it, Didn't look back when I broke soil, cause everytime I did it" +
                " would hurt more"
        else -> "Today's gonna feel like tomorrow someday, Tomorrow's gonna feel like yesterday"
    }
}