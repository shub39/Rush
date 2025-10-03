package com.shub39.rush.share.component.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.lighten
import com.shub39.rush.core.domain.data_classes.SongDetails
import com.shub39.rush.core.domain.enums.CardFit
import com.shub39.rush.core.presentation.HypnoticVisualizer
import com.shub39.rush.core.presentation.generateGradientColors

@Composable
fun HypnoticShareCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    fit: CardFit,
    albumArtShape: Shape = CircleShape
) {
    Box(modifier = modifier.clip(cardCorners)) {
        HypnoticVisualizer(
            waveData = null,
            modifier = Modifier.matchParentSize(),
            colors = generateGradientColors(cardColors.containerColor.lighten(2f), cardColors.containerColor.darken(1f)),
        )

        SpotifyShareCard(
            modifier = Modifier.fillMaxWidth(),
            song = song,
            sortedLines = sortedLines,
            cardColors = cardColors.copy(containerColor = Color.Transparent),
            cardCorners = cardCorners,
            albumArtShape = albumArtShape,
            fit = fit
        )
    }
}