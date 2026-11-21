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

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.dataclasses.ExtractedColors
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.core.enums.SortOrder
import com.shub39.rush.shared.ui.component.LazierDefaults
import com.shub39.rush.shared.ui.component.ListSelect
import com.shub39.rush.shared.ui.rushmode.RushState
import com.shub39.rush.shared.ui.saved.SavedPageAction
import com.shub39.rush.shared.ui.saved.SavedPageState
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import com.shub39.rush.shared.ui.theme.flexFontRounded
import com.shub39.rush.shared.ui.toStringRes
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.Res
import rush.shared.ui.generated.resources.rush_branding
import rush.shared.ui.generated.resources.saved
import rush.shared.ui.generated.resources.settings

/**
 * The left pane of the wide saved page layout. Holds the app branding, the categories (sort order)
 * and the now playing card, while the song list lives in the pane to its right.
 */
@Composable
fun SavedPageSidebar(
    state: SavedPageState,
    width: Dp,
    isSettingsOpen: Boolean,
    onAction: (SavedPageAction) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLyrics: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    Column(
        modifier =
            modifier
                .width(width)
                .fillMaxHeight()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
                )
                .padding(LazierDefaults.contentPaddingWith(contentPadding))
    ) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.rush_branding),
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = flexFontEmphasis(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = "${state.songsAsc.size} " + stringResource(Res.string.saved),
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = flexFontRounded(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                FilledTonalIconToggleButton(
                    checked = isSettingsOpen,
                    onCheckedChange = { onNavigateToSettings() },
                    shapes = IconButtonDefaults.toggleableShapes(),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.settings),
                        contentDescription = "Settings",
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ListSelect(
                title = null,
                options = SortOrder.entries.toList(),
                selected = state.sortOrder,
                onSelectedChange = { onAction(SavedPageAction.UpdateSortOrder(it)) },
                labelProvider = {
                    Text(
                        text = stringResource(it.toStringRes()),
                        modifier = Modifier.basicMarquee(),
                        maxLines = 1,
                    )
                },
            )
        }

        state.nowPlaying?.let { nowPlaying ->
            Spacer(modifier = Modifier.height(12.dp))

            NowPlayingCard(
                nowPlaying = nowPlaying,
                colors = state.extractedColors,
                onClick = onNavigateToLyrics,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(widthDp = 340, heightDp = 411)
@Composable
private fun Preview() {
    SavedPageSidebar(
        state =
            SavedPageState(
                extractedColors = ExtractedColors(),
                rush = RushState(enabled = true, mediaAccess = true),
                nowPlaying =
                    SongUi(
                        title = "Title",
                        artists = "Artists",
                        artUrl = "",
                        id = 0,
                        album = "",
                        sourceUrl = "",
                        lyrics = listOf(),
                        syncedLyrics = null,
                        geniusLyrics = null,
                        ttmlLyrics = null,
                    ),
            ),
        width = 340.dp,
        isSettingsOpen = false,
        onAction = {},
        onNavigateToSettings = {},
        onNavigateToLyrics = {},
    )
}
