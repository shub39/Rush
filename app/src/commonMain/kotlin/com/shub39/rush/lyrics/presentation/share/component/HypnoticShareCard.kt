package com.shub39.rush.lyrics.presentation.share.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.lighten
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.MeshGradient
import com.shub39.rush.core.domain.data_classes.SongDetails
import com.shub39.rush.core.domain.enums.CardFit
import com.shub39.rush.core.presentation.generateGradientColors

@Composable
fun HypnoticShareCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    fit: CardFit
) {
    Box(modifier = modifier.clip(cardCorners)) {
        SpotifyShareCard(
            modifier = Modifier
                .fillMaxWidth()
                .shaderBackground(
                    MeshGradient(
                        colors = generateGradientColors(
                            cardColors.containerColor.lighten(2f),
                            cardColors.containerColor.darken(2f)
                        ).toTypedArray()
                    ),
                    fallback = {
                        Brush.horizontalGradient(
                            generateGradientColors(
                                cardColors.containerColor.lighten(2f),
                                cardColors.containerColor.darken(2f)
                            )
                        )
                    }
                ),
            song = song,
            sortedLines = sortedLines,
            cardColors = cardColors.copy(containerColor = Color.Transparent),
            cardCorners = cardCorners,
            fit = fit
        )
    }
}