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
package com.shub39.rush.shared.ui.saved

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.dataclasses.Song
import com.shub39.rush.shared.core.enums.SortOrder
import com.shub39.rush.shared.ui.LocalWindowSizeClass
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.component.Empty
import com.shub39.rush.shared.ui.component.PageFill
import com.shub39.rush.shared.ui.isExpanded
import com.shub39.rush.shared.ui.saved.component.SavedPageToolbar
import com.shub39.rush.shared.ui.saved.component.SongCard
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import com.shub39.rush.shared.ui.theme.flexFontRounded
import com.shub39.rush.shared.ui.toStringRes
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

@Composable
fun SavedPage(
    state: SavedPageState,
    notificationAccess: Boolean,
    onAction: (SavedPageAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) =
    PageFill(modifier = modifier) {
        val listState = rememberLazyListState()
        val windowSizeClass = LocalWindowSizeClass.current
        val showBottomBar by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex == 0 || listState.lastScrolledBackward
            }
        }

        Scaffold(
            modifier = Modifier.widthIn(max = 700.dp),
            topBar = {
                Column {
                    if (!windowSizeClass.isExpanded()) {
                        LargeFlexibleTopAppBar(
                            title = {
                                Text(
                                    text = stringResource(Res.string.rush_branding),
                                    fontFamily = flexFontEmphasis(),
                                )
                            },
                            subtitle = {
                                Text(
                                    text =
                                        "${state.songsAsc.size} " +
                                            stringResource(Res.string.saved),
                                    fontFamily = flexFontRounded(),
                                )
                            },
                            actions = {
                                IconButton(onClick = onNavigateToSettings) {
                                    Icon(
                                        painter = painterResource(Res.drawable.settings),
                                        contentDescription = "Settings",
                                    )
                                }
                            },
                            colors =
                                TopAppBarDefaults.topAppBarColors(
                                    scrolledContainerColor =
                                        MaterialTheme.colorScheme.surfaceContainer,
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                ),
                        )
                    } else {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(Res.string.rush_branding),
                                    fontFamily = flexFontEmphasis(),
                                )
                            },
                            subtitle = {
                                Text(
                                    text =
                                        "${state.songsAsc.size} " +
                                            stringResource(Res.string.saved),
                                    fontFamily = flexFontRounded(),
                                )
                            },
                            actions = {
                                IconButton(onClick = onNavigateToSettings) {
                                    Icon(
                                        painter = painterResource(Res.drawable.settings),
                                        contentDescription = "Settings",
                                    )
                                }
                            },
                            colors =
                                TopAppBarDefaults.topAppBarColors(
                                    scrolledContainerColor =
                                        MaterialTheme.colorScheme.surfaceContainer,
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                ),
                        )
                    }

                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    shape =
                                        RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                                )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement =
                                Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                        ) {
                            SortOrder.entries.toList().forEach { order ->
                                ToggleButton(
                                    checked = order == state.sortOrder,
                                    onCheckedChange = {
                                        onAction(SavedPageAction.UpdateSortOrder(order))
                                    },
                                    colors =
                                        ToggleButtonDefaults.toggleButtonColors(
                                            containerColor =
                                                MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor =
                                                MaterialTheme.colorScheme.onSecondaryContainer,
                                        ),
                                    modifier = Modifier.weight(1f),
                                    shapes =
                                        when (order) {
                                            SortOrder.DATE_ADDED ->
                                                ButtonGroupDefaults.connectedLeadingButtonShapes()

                                            SortOrder.TITLE_ASC ->
                                                ButtonGroupDefaults.connectedMiddleButtonShapes()

                                            SortOrder.TITLE_DESC ->
                                                ButtonGroupDefaults.connectedTrailingButtonShapes()
                                        },
                                ) {
                                    Text(
                                        text = stringResource(order.toStringRes()),
                                        modifier = Modifier.basicMarquee(),
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }
                }
            },
        ) { paddingValues ->
            Column(
                modifier =
                    Modifier.padding(
                            start =
                                paddingValues.calculateLeftPadding(LocalLayoutDirection.current),
                            end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                        )
                        .fillMaxSize()
            ) {
                if (state.songsAsc.isEmpty()) {
                    Empty()
                } else {

                    AnimatedContent(targetState = state.sortOrder) { sortOrder ->
                        val songs =
                            when (sortOrder) {
                                DATE_ADDED -> state.songsByTime
                                TITLE_ASC -> state.songsAsc
                                TITLE_DESC -> state.songsDesc
                            }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize().animateContentSize(),
                            contentPadding =
                                PaddingValues(
                                    top = paddingValues.calculateTopPadding() + 16.dp,
                                    bottom = paddingValues.calculateBottomPadding() + 60.dp,
                                ),
                        ) {
                            items(items = songs, key = { it.id }) { song ->
                                SongCard(
                                    song = song,
                                    onDelete = { onAction(SavedPageAction.OnDeleteSong(song)) },
                                    onClick = {
                                        onAction(SavedPageAction.ChangeCurrentSong(song.id))
                                        onNavigateToLyrics()
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showBottomBar,
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            enter =
                slideInVertically(
                    animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                    initialOffsetY = { it },
                ),
            exit =
                slideOutVertically(
                    animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                    targetOffsetY = { it },
                ),
        ) {
            SavedPageToolbar(
                notificationAccess = notificationAccess,
                onAction = onAction,
                state = state,
                onNavigateToLyrics = onNavigateToLyrics,
                modifier = Modifier.navigationBarsPadding().padding(horizontal = 16.dp),
            )
        }
    }

@PreviewWrapper(RushPreviewWrapper::class)
@Preview(device = "spec:width=411dp,height=891dp")
@Composable
private fun Preview() {
    val songs =
        (0..100).map {
            Song(
                id = it.toLong(),
                title = "Song $it",
                artists = it.toString(),
                lyrics = "",
                album = "",
                sourceUrl = "",
                artUrl = "",
                geniusLyrics = "",
                syncedLyrics = "",
                dateAdded = it.toLong(),
                ttmlLyrics = null,
            )
        }

    var state by remember {
        mutableStateOf(
            SavedPageState(
                songsAsc = songs,
                songsDesc = songs.sortedByDescending { it.title },
                songsByTime = songs.sortedBy { it.dateAdded },
            )
        )
    }

    SavedPage(
        state = state,
        notificationAccess = true,
        onAction = {
            when (it) {
                is SavedPageAction.UpdateSortOrder -> state = state.copy(sortOrder = it.sortOrder)

                else -> {}
            }
        },
        onNavigateToLyrics = {},
        onNavigateToSettings = {},
    )
}
