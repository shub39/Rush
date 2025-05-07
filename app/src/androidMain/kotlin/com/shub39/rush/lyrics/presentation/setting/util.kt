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
            true -> MaterialTheme.colorScheme.onSecondary
            else -> MaterialTheme.colorScheme.error
        }, label = "status"
    )

    val cardBackground by animateColorAsState(
        targetValue = when (state) {
            null -> MaterialTheme.colorScheme.surface
            true -> MaterialTheme.colorScheme.onSecondaryContainer
            else -> MaterialTheme.colorScheme.errorContainer
        }, label = "status"
    )

    return ListItemDefaults.colors(
        containerColor = cardBackground,
        headlineColor = cardContent,
        trailingIconColor = cardContent
    )
}

// My Top 10 faves from various distinct artists
fun getRandomLine(): String {
    return when(Random.nextInt(1, 11)) {
        1 -> "You wont get what you want by Daughters"
        2 -> "Cold Visions by Bladee"
        3 -> "Rainbow Bridge 3 by Sematary"
        4 -> "Exmilitary by Death Grips"
        5 -> "Diamond eyes by Deftones"
        6 -> "Lionheart by Vestron Vulture"
        7 -> "Issues by Korn"
        8 -> "God's Country by Chatpile"
        9 -> "The New Sound by Geordie Greep"
        else -> "SICK! by Earl Sweatshirt"
    }
}