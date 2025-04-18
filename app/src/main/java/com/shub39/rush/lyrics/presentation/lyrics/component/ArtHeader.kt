package com.shub39.rush.lyrics.presentation.lyrics.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.lyrics.presentation.lyrics.SongUi

@Composable
fun ArtHeader(
    top: Int,
    hypnoticCanvas: Boolean,
    song: SongUi,
    cardContent: Color,
    cardBackground: Color
) {
    Column {
        AnimatedVisibility(top > 2 && !hypnoticCanvas) {
            ArtFromUrl(
                imageUrl = song.artUrl!!,
                highlightColor = cardContent,
                baseColor = Color.Transparent,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                cardBackground
                            )
                        )
                    )
                    .height(150.dp)
            )
        }
    }
}