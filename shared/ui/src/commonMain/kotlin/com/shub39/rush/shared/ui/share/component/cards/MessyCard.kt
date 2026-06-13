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
package com.shub39.rush.shared.ui.share.component.cards

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.shub39.rush.shared.core.dataclasses.SongDetails
import com.shub39.rush.shared.core.enums.CardFit
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.component.ArtFromUrl
import com.shub39.rush.shared.ui.component.RushBranding
import com.shub39.rush.shared.ui.fromPx
import com.shub39.rush.shared.ui.pxToDp
import com.shub39.rush.shared.ui.pxToSp
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import com.shub39.rush.shared.ui.theme.flexFontRounded
import kotlin.math.roundToInt
import kotlin.random.Random

private data class Word(
    val text: String,
    val fontWeight: Int,
    val fontSize: Int,
    val fontWidth: Float,
    val angle: Int,
)

private val fontCache = mutableMapOf<Pair<Int, Int>, FontFamily>()

@Composable
private fun flexFontEmphasisCached(weight: Int, width: Int): FontFamily {
    return fontCache.getOrPut(weight to width) {
        flexFontEmphasis(fontWeight = weight, fontWidth = width.toFloat())
    }
}

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
    seed: Long,
) {
    val firstLine = sortedLines.values.firstOrNull() ?: "Woah..."
    val words =
        remember(seed, firstLine) {
            firstLine
                .split(Regex("\\s+"))
                .filter { it.isNotBlank() }
                .map {
                    Word(
                        text = if (Random.nextBoolean()) it.uppercase() else it.lowercase(),
                        fontWeight = Random.nextInt(3, 10) * 100,
                        fontSize = Random.nextInt(3, 10) * 10,
                        fontWidth = (Random.nextInt(5, 12) * 10).toFloat(),
                        angle = Random.nextInt(-10, 10),
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
                        text = word.text,
                        style =
                            TextStyle(
                                fontFamily =
                                    flexFontEmphasisCached(
                                        weight = word.fontWeight,
                                        width =
                                            (if (word.text.length > 10) 100f else word.fontWidth)
                                                .roundToInt(),
                                    ),
                                fontSize = pxToSp(word.fontSize),
                            ),
                        modifier = Modifier.rotate(word.angle.toFloat()),
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
                                .copy(fontFamily = flexFontRounded())
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

@PreviewWrapper(RushPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
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
        seed = 0,
    )
}
