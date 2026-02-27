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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.shub39.rush.domain.dataclasses.SongDetails
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.enums.CardFit
import com.shub39.rush.presentation.components.ArtFromUrl
import com.shub39.rush.presentation.components.RushBranding
import com.shub39.rush.presentation.components.RushTheme
import com.shub39.rush.presentation.flexFontEmphasis
import com.shub39.rush.presentation.flexFontRounded
import com.shub39.rush.presentation.share.fromPx
import com.shub39.rush.presentation.share.pxToDp
import kotlin.random.Random
import kotlin.random.nextInt

private data class Word(
    val text: String,
    val fontWeight: FontWeight,
    val fontSize: Int,
    val rotate: Int,
    val fontDecoration: TextDecoration,
)

@Composable
fun MessyCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    fit: CardFit,
    albumArtShape: Shape = CircleShape,
    rushBranding: Boolean,
) {
    val firstLine = sortedLines.values.firstOrNull() ?: "Woah..."
    val words = remember {
        firstLine.split(" ").map {
            Word(
                text = if (Random.nextBoolean()) it.uppercase() else it.lowercase(),
                fontWeight = if (Random.nextBoolean()) FontWeight.Normal else FontWeight.ExtraBold,
                fontSize = Random.nextInt(50, 100),
                rotate = Random.nextInt(-10..10),
                fontDecoration = listOf(TextDecoration.Underline, TextDecoration.None).random(),
            )
        }
    }

    Card(modifier = modifier, colors = cardColors, shape = cardCorners) {
        Column(
            modifier =
                Modifier.padding(pxToDp(48)).let {
                    if (fit == CardFit.STANDARD) {
                        it.weight(1f)
                    } else it
                },
            verticalArrangement = Arrangement.Center,
        ) {
            AnimatedVisibility(visible = rushBranding) {
                RushBranding(
                    color = cardColors.contentColor,
                    modifier = Modifier.padding(bottom = pxToDp(32)),
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(pxToDp(16)),
                verticalArrangement = Arrangement.spacedBy(pxToDp(16)),
            ) {
                words.forEach { word ->
                    Text(
                        modifier = Modifier.rotate(word.rotate.toFloat()),
                        text = word.text,
                        style =
                            MaterialTheme.typography.titleMedium
                                .copy(textDecoration = word.fontDecoration)
                                .fromPx(
                                    fontSize = word.fontSize,
                                    letterSpacing = 0,
                                    lineHeight = word.fontSize,
                                    fontWeight = word.fontWeight,
                                ),
                    )
                }
            }

            Spacer(modifier = Modifier.padding(pxToDp(64)))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier.weight(1f).padding(horizontal = pxToDp(32)),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = song.title,
                        style =
                            MaterialTheme.typography.titleMedium
                                .copy(fontFamily = flexFontEmphasis())
                                .fromPx(fontSize = 32, letterSpacing = 0, lineHeight = 32),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = song.artist,
                        style =
                            MaterialTheme.typography.bodySmall
                                .copy(fontFamily = flexFontRounded())
                                .fromPx(fontSize = 26, letterSpacing = 0, lineHeight = 26),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                ArtFromUrl(
                    imageUrl = song.artUrl,
                    modifier = Modifier.size(pxToDp(100)).clip(albumArtShape),
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RushTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        MessyCard(
            modifier = Modifier.width(pxToDp(720)).heightIn(max = pxToDp(1280)),
            song = SongDetails(title = "Test Song", artist = "Eminem", null, ""),
            sortedLines =
                mapOf(0 to "This is a simple line")
                    .plus(
                        0 to
                            "Hello this is a very very very very very the quick browm fox jumps over the lazy dog"
                    ),
            cardColors =
                CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            cardCorners = RoundedCornerShape(pxToDp(48)),
            fit = CardFit.FIT,
            rushBranding = true,
        )
    }
}
