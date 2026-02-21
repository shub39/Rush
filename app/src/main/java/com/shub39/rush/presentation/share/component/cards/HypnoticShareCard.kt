/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.presentation.share.component.cards

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
import com.shub39.rush.domain.dataclasses.SongDetails
import com.shub39.rush.domain.enums.CardFit
import com.shub39.rush.presentation.components.HypnoticVisualizer
import com.shub39.rush.presentation.generateGradientColors

@Composable
fun HypnoticShareCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    fit: CardFit,
    albumArtShape: Shape = CircleShape,
    rushBranding: Boolean,
) {
    Box(modifier = modifier.clip(cardCorners)) {
        HypnoticVisualizer(
            modifier = Modifier.matchParentSize(),
            colors =
                generateGradientColors(
                    cardColors.containerColor.lighten(2f),
                    cardColors.containerColor.darken(1f),
                ),
        )

        SpotifyShareCard(
            modifier = Modifier.fillMaxWidth(),
            song = song,
            sortedLines = sortedLines,
            cardColors = cardColors.copy(containerColor = Color.Transparent),
            cardCorners = cardCorners,
            albumArtShape = albumArtShape,
            fit = fit,
            rushBranding = rushBranding,
        )
    }
}
