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
package com.shub39.rush.shared.ui.saved.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.dataclasses.ExtractedColors
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.ui.component.ArtFromUrl
import com.shub39.rush.shared.ui.saved.SavedPageAction
import com.shub39.rush.shared.ui.saved.SavedPageState
import org.jetbrains.compose.resources.painterResource
import rush.shared.ui.generated.resources.Res
import rush.shared.ui.generated.resources.meteor
import rush.shared.ui.generated.resources.search

@Composable
fun SavedPageToolbar(
    state: SavedPageState,
    notificationAccess: Boolean,
    onAction: (SavedPageAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (state.currentSong != null) {
            Card(
                onClick = { onNavigateToLyrics() },
                shape = CircleShape,
                modifier = Modifier.weight(1f),
                colors =
                    CardDefaults.cardColors(
                        contentColor = Color(state.extractedColors.cardContentMuted),
                        containerColor = Color(state.extractedColors.cardBackgroundMuted),
                    ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(8.dp),
                ) {
                    ArtFromUrl(
                        imageUrl = state.currentSong.artUrl,
                        modifier = Modifier.size(48.dp).clip(CircleShape),
                    )

                    Column {
                        Text(
                            text = state.currentSong.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Text(
                            text = state.currentSong.artists,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        if (notificationAccess) {
            FilledTonalIconToggleButton(
                checked = state.autoChange,
                onCheckedChange = {
                    onAction(SavedPageAction.OnToggleAutoChange)
                    if (!state.autoChange) onNavigateToLyrics()
                },
                modifier = Modifier.size(IconButtonDefaults.mediumContainerSize()),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.meteor),
                    contentDescription = "Rush Mode",
                    modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
                )
            }
        }

        FloatingActionButton(
            shape = CircleShape,
            onClick = { onAction(SavedPageAction.OnToggleSearchSheet) },
        ) {
            Icon(painter = painterResource(Res.drawable.search), contentDescription = "Search")
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SavedPageToolbar(
        state =
            SavedPageState(
                extractedColors = ExtractedColors(cardContentMuted = Color.White.toArgb()),
                autoChange = true,
                currentSong =
                    SongUi(
                        title = "Title",
                        artists = "Artists",
                        artUrl = "",
                        id = 0,
                        album = "",
                        sourceUrl = "TODO()",
                        lyrics = listOf(),
                        syncedLyrics = null,
                        geniusLyrics = null,
                        ttmlLyrics = null,
                    ),
            ),
        notificationAccess = true,
        onAction = {},
        onNavigateToLyrics = {},
    )
}
