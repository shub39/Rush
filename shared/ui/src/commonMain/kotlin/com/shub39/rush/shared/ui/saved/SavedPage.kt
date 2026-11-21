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
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.shub39.rush.shared.core.dataclasses.Song
import com.shub39.rush.shared.core.enums.SortOrder
import com.shub39.rush.shared.ui.LocalWindowSizeClass
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.WindowSize.Companion.getSidebarWidth
import com.shub39.rush.shared.ui.WindowSize.Companion.isWidthCompact
import com.shub39.rush.shared.ui.component.Empty
import com.shub39.rush.shared.ui.component.ExpressiveTopBar
import com.shub39.rush.shared.ui.component.ExpressiveTopBarStyle
import com.shub39.rush.shared.ui.component.LazierBlur
import com.shub39.rush.shared.ui.component.LazierColumn
import com.shub39.rush.shared.ui.component.LazierDefaults
import com.shub39.rush.shared.ui.component.ListSelect
import com.shub39.rush.shared.ui.component.PageFill
import com.shub39.rush.shared.ui.component.PageInsets
import com.shub39.rush.shared.ui.component.RushDialog
import com.shub39.rush.shared.ui.saved.component.RushModeToggle
import com.shub39.rush.shared.ui.saved.component.SavedPageSidebar
import com.shub39.rush.shared.ui.saved.component.SavedPageToolbar
import com.shub39.rush.shared.ui.saved.component.SearchFab
import com.shub39.rush.shared.ui.saved.component.SongCard
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import com.shub39.rush.shared.ui.theme.flexFontRounded
import com.shub39.rush.shared.ui.toStringRes
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.Res
import rush.shared.ui.generated.resources.grant_permission
import rush.shared.ui.generated.resources.no_thanks
import rush.shared.ui.generated.resources.notification_access_needed
import rush.shared.ui.generated.resources.notification_access_needed_info
import rush.shared.ui.generated.resources.notifications
import rush.shared.ui.generated.resources.rush_branding
import rush.shared.ui.generated.resources.saved
import rush.shared.ui.generated.resources.settings

@Composable
fun SavedPage(
    modifier: Modifier = Modifier,
    state: SavedPageState,
    notificationAccess: Boolean,
    onAction: (SavedPageAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onOpenSearchSheet: () -> Unit,
    isSettingsOpen: Boolean,
) =
    PageFill(modifier = modifier) {
        var showNotificationAccessDialog by remember { mutableStateOf(false) }

        val dispatch: (SavedPageAction) -> Unit = { action ->
            if (action is SavedPageAction.OnRequestMediaAccess) {
                showNotificationAccessDialog = true
            } else {
                onAction(action)
            }
        }

        val windowSizeClass = LocalWindowSizeClass.current

        if (windowSizeClass.isWidthCompact()) {
            CompactSavedPage(
                state = state,
                isSettingsOpen = isSettingsOpen,
                notificationAccess = notificationAccess,
                onAction = dispatch,
                onNavigateToLyrics = onNavigateToLyrics,
                onNavigateToSettings = onNavigateToSettings,
                onOpenSearchSheet = onOpenSearchSheet,
            )
        } else {
            WideSavedPage(
                state = state,
                isSettingsOpen = isSettingsOpen,
                notificationAccess = notificationAccess,
                onAction = dispatch,
                onNavigateToLyrics = onNavigateToLyrics,
                onNavigateToSettings = onNavigateToSettings,
                onOpenSearchSheet = onOpenSearchSheet,
            )
        }

        if (showNotificationAccessDialog) {
            RushDialog(
                padding = 32.dp,
                onDismissRequest = { showNotificationAccessDialog = false },
            ) {
                Column(
                    modifier = Modifier.wrapContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.notifications),
                        contentDescription = null,
                    )

                    Text(
                        text = stringResource(Res.string.notification_access_needed),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Text(
                        text = stringResource(Res.string.notification_access_needed_info),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Bottom),
                    ) {
                        Button(
                            onClick = {
                                showNotificationAccessDialog = false
                                onAction(SavedPageAction.OnRequestMediaAccess)
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(text = stringResource(Res.string.grant_permission))
                        }
                        TextButton(onClick = { showNotificationAccessDialog = false }) {
                            Text(text = stringResource(Res.string.no_thanks))
                        }
                    }
                }
            }
        }
    }

/**
 * Two pane layout for wide windows: the branding, categories and now playing card sit in a sidebar
 * on the left while the song list fills the rest of the page.
 */
@Composable
private fun WideSavedPage(
    state: SavedPageState,
    isSettingsOpen: Boolean,
    notificationAccess: Boolean,
    onAction: (SavedPageAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onOpenSearchSheet: () -> Unit,
) {
    val sidebarWidth = LocalWindowSizeClass.current.getSidebarWidth()

    Scaffold { paddingValues ->
        Row(modifier = Modifier.fillMaxSize()) {
            SavedPageSidebar(
                state = state,
                width = sidebarWidth,
                isSettingsOpen = isSettingsOpen,
                onAction = onAction,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToLyrics = onNavigateToLyrics,
                contentPadding = paddingValues,
            )

            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                SavedSongsPane(
                    state = state,
                    contentPadding = paddingValues,
                    extraPadding =
                        LazierDefaults.pagePadding(navBarClearance = PageInsets.NavBarHeight),
                    blur = LazierBlur.None,
                    onAction = onAction,
                    onNavigateToLyrics = onNavigateToLyrics,
                    modifier = Modifier.fillMaxSize(),
                )

                Row(
                    modifier =
                        Modifier.align(Alignment.BottomEnd).padding(paddingValues).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RushModeToggle(
                        state = state,
                        notificationAccess = notificationAccess,
                        onAction = onAction,
                        onNavigateToLyrics = onNavigateToLyrics,
                    )

                    SearchFab(onClick = onOpenSearchSheet)
                }
            }
        }
    }
}

/** Single column layout used on compact (portrait) windows. */
@Composable
private fun CompactSavedPage(
    state: SavedPageState,
    isSettingsOpen: Boolean,
    notificationAccess: Boolean,
    onAction: (SavedPageAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onOpenSearchSheet: () -> Unit,
) {
    var showBottomBar by remember { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                ExpressiveTopBar(
                    style = ExpressiveTopBarStyle.LargeFlexible,
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = stringResource(Res.string.rush_branding),
                            fontFamily = flexFontEmphasis(),
                        )
                    },
                    subtitle = {
                        Text(
                            text = "${state.songsAsc.size} " + stringResource(Res.string.saved),
                            fontFamily = flexFontRounded(),
                        )
                    },
                    actions = {
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
                    },
                    bottomContent = {
                        Row(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement =
                                Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                        ) {
                            ListSelect(
                                title = null,
                                options = SortOrder.entries.toList(),
                                selected = state.sortOrder,
                                onSelectedChange = {
                                    onAction(SavedPageAction.UpdateSortOrder(it))
                                },
                                labelProvider = {
                                    Text(
                                        text = stringResource(it.toStringRes()),
                                        modifier = Modifier.basicMarquee(),
                                        maxLines = 1,
                                    )
                                },
                            )
                        }
                    },
                )
            },
        ) { paddingValues ->
            SavedSongsPane(
                state = state,
                contentPadding = paddingValues,
                extraPadding =
                    LazierDefaults.pagePadding(navBarClearance = PageInsets.NavBarHeight),
                blur = LazierBlur.Top,
                onAction = onAction,
                onNavigateToLyrics = onNavigateToLyrics,
                modifier = Modifier.fillMaxSize(),
                onShowBottomBarChange = { showBottomBar = it },
            )
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
                onOpenSearchSheet = onOpenSearchSheet,
                notificationAccess = notificationAccess,
                onAction = onAction,
                state = state,
                onNavigateToLyrics = onNavigateToLyrics,
                modifier = Modifier.platformNavigationBarsPadding().padding(horizontal = 16.dp),
            )
        }
    }
}

/**
 * The song list, shared by both layouts. The bottom bar of the compact layout is driven by the
 * scroll state, so the current visibility is reported through [onShowBottomBarChange].
 */
@Composable
private fun SavedSongsPane(
    state: SavedPageState,
    contentPadding: PaddingValues,
    onAction: (SavedPageAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    modifier: Modifier = Modifier,
    extraPadding: PaddingValues = LazierDefaults.pagePadding(),
    blur: LazierBlur = LazierBlur.Top,
    onShowBottomBarChange: (Boolean) -> Unit = {},
) {
    if (state.songsAsc.isEmpty()) {
        Empty(modifier = modifier)
    } else {
        AnimatedContent(targetState = state.sortOrder, modifier = modifier) { sortOrder ->
            val songs =
                when (sortOrder) {
                    DATE_ADDED -> state.songsByTime
                    TITLE_ASC -> state.songsAsc
                    TITLE_DESC -> state.songsDesc
                }
            val listState = rememberLazyListState()
            val showBottomBarListener by remember {
                derivedStateOf {
                    listState.firstVisibleItemIndex == 0 || listState.lastScrolledBackward
                }
            }

            LaunchedEffect(showBottomBarListener) { onShowBottomBarChange(showBottomBarListener) }

            LazierColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().animateContentSize(),
                contentPadding = contentPadding,
                extraPadding = extraPadding,
                blur = blur,
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

expect fun Modifier.platformNavigationBarsPadding(): Modifier

@PreviewWrapper(RushPreviewWrapper::class)
@Preview(device = "spec:width=411dp,height=891dp")
@Composable
private fun Preview() {
    SavedPagePreviewContent()
}

@PreviewWrapper(RushPreviewWrapper::class)
@Preview(device = "spec:width=891dp,height=411dp")
@Composable
private fun LandscapePreview() {
    // The wide layout needs a wide window size class, not the compact one previews default to.
    CompositionLocalProvider(
        LocalWindowSizeClass provides WindowSizeClass(minWidthDp = 891, minHeightDp = 411)
    ) {
        SavedPagePreviewContent()
    }
}

@Composable
private fun SavedPagePreviewContent() {
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
        isSettingsOpen = false,
        onOpenSearchSheet = {},
    )
}
