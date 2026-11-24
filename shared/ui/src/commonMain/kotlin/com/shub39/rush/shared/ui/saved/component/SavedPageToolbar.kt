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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.dataclasses.ExtractedColors
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.ui.rushmode.RushState
import com.shub39.rush.shared.ui.saved.SavedPageAction
import com.shub39.rush.shared.ui.saved.SavedPageState
import org.jetbrains.compose.resources.painterResource
import rush.shared.ui.generated.resources.Res
import rush.shared.ui.generated.resources.meteor
import rush.shared.ui.generated.resources.search

/** Floating bottom bar of the saved page, used on compact (portrait) windows. */
@Composable
fun SavedPageToolbar(
    state: SavedPageState,
    notificationAccess: Boolean,
    onAction: (SavedPageAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    onOpenSearchSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (state.nowPlaying != null) {
            NowPlayingCard(
                nowPlaying = state.nowPlaying,
                colors = state.extractedColors,
                onClick = onNavigateToLyrics,
                modifier = Modifier.weight(1f),
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        RushModeToggle(
            state = state,
            notificationAccess = notificationAccess,
            onAction = onAction,
            onNavigateToLyrics = onNavigateToLyrics,
        )

        SearchFab(onClick = onOpenSearchSheet)
    }
}

/** Toggles the rush mode, asking for notification access when it is not granted yet. */
@Composable
fun RushModeToggle(
    state: SavedPageState,
    notificationAccess: Boolean,
    onAction: (SavedPageAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconToggleButton(
        checked = state.rushEnabled,
        onCheckedChange = {
            if (notificationAccess) {
                onAction(SavedPageAction.OnToggleRushMode)
                if (!state.rushEnabled) onNavigateToLyrics()
            } else {
                onAction(SavedPageAction.OnRequestMediaAccess)
            }
        },
        modifier = modifier.size(IconButtonDefaults.mediumContainerSize()),
    ) {
        Icon(
            painter = painterResource(Res.drawable.meteor),
            contentDescription = "Rush Mode",
            modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
        )
    }
}

/** Opens the search sheet. */
@Composable
fun SearchFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(shape = CircleShape, onClick = onClick, modifier = modifier) {
        Icon(painter = painterResource(Res.drawable.search), contentDescription = "Search")
    }
}

@Preview
@Composable
private fun Preview() {
    SavedPageToolbar(
        onOpenSearchSheet = {},
        state =
            SavedPageState(
                extractedColors = ExtractedColors(cardContentMuted = Color.White.toArgb()),
                rush = RushState(enabled = true, mediaAccess = true),
                nowPlaying =
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
