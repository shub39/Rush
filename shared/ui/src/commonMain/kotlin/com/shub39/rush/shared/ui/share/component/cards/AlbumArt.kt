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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialShapes.Companion.VerySunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.shub39.rush.shared.core.dataclasses.SongDetails
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.component.ArtFromUrl
import com.shub39.rush.shared.ui.fromPx
import com.shub39.rush.shared.ui.pxToDp
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import io.github.vinceglb.filekit.PlatformFile

@Composable
fun AlbumArt(
    song: SongDetails,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    modifier: Modifier = Modifier,
    albumArtShape: Shape = CircleShape,
    selectedImage: PlatformFile? = null,
) {
    Card(modifier = modifier, shape = cardCorners, colors = cardColors) {
        Column(modifier = Modifier.fillMaxWidth().padding(pxToDp(30))) {
            Text(
                text = song.artist,
                style =
                    MaterialTheme.typography.headlineLarge
                        .copy(fontFamily = flexFontEmphasis())
                        .fromPx(fontSize = 80, letterSpacing = 0, lineHeight = 82),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(pxToDp(28)))

            ArtFromUrl(
                imageUrl = selectedImage?.toString() ?: song.artUrl,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(albumArtShape),
            )
        }
    }
}

@PreviewWrapper(RushPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    AlbumArt(
        modifier = Modifier.width(pxToDp(720)).heightIn(max = pxToDp(1280)),
        song = SongDetails(title = "Test Song", artist = "Eminem", null, "as"),
        cardColors =
            CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
            ),
        cardCorners = RoundedCornerShape(pxToDp(32)),
        albumArtShape = VerySunny.toShape(),
    )
}
