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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.shub39.rush.shared.core.dataclasses.SongDetails
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.fromPx
import com.shub39.rush.shared.ui.pxToDp
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import com.shub39.rush.shared.ui.theme.flexFontRounded
import org.jetbrains.compose.resources.Font
import rush.shared.ui.generated.resources.Res
import rush.shared.ui.generated.resources.google_sans_flex

@Composable
fun BratShareCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
) {
    val artistFont = FontFamily(Font(Res.font.google_sans_flex))
    val lyricsFont = flexFontEmphasis(slant = 0f, fontWeight = 300, fontWidth = 60f)

    Card(modifier = modifier, colors = cardColors, shape = cardCorners) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = sortedLines.values.firstOrNull() ?: "Woah...",
                style =
                    MaterialTheme.typography.displayMedium
                        .copy(fontFamily = lyricsFont, textAlign = TextAlign.Justify)
                        .fromPx(fontSize = 100, letterSpacing = 0, lineHeight = 120),
                modifier = Modifier.padding(pxToDp(64)),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(Color.Black),
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(pxToDp(16))) {
                Text(
                    text = song.title,
                    style =
                        MaterialTheme.typography.titleMedium
                            .copy(fontFamily = flexFontRounded(), color = Color.White)
                            .fromPx(fontSize = 20, letterSpacing = 0, lineHeight = 22),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = song.artist,
                    style =
                        MaterialTheme.typography.bodySmall
                            .copy(fontFamily = artistFont, color = Color.White)
                            .fromPx(fontSize = 16, letterSpacing = 0, lineHeight = 18),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@PreviewWrapper(RushPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    BratShareCard(
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
    )
}
