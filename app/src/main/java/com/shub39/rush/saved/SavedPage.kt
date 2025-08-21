package com.shub39.rush.saved

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes.Companion.Sunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.shub39.rush.R
import com.shub39.rush.core.domain.data_classes.Song
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.domain.enums.SortOrder
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.Empty
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.core.presentation.simpleVerticalScrollbar
import com.shub39.rush.saved.component.SongCard
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Meteor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SavedPage(
    state: SavedPageState,
    notificationAccess: Boolean,
    onAction: (SavedPageAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) = PageFill {
    val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(stringResource(R.string.rush_branding)) },
                subtitle = { Text(text = "${state.songsAsc.size} " + stringResource(R.string.saved)) },
                actions = {
                    IconButton(
                        onClick = onNavigateToSettings,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                scrollBehavior = scrollBehaviour,
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = state.currentSong != null,
                enter = slideInVertically { it / 2 },
                exit = slideOutVertically { it / 2 }
            ) {
                if (state.currentSong != null) {
                    BottomAppBar(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp),
                        modifier = Modifier.clickable { onNavigateToLyrics() },
                        contentColor = state.extractedColors.cardContentMuted,
                        containerColor = state.extractedColors.cardBackgroundMuted
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ArtFromUrl(
                                imageUrl = state.currentSong.artUrl,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(MaterialTheme.shapes.small)
                            )

                            Column {
                                Text(
                                    text = state.currentSong.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = state.currentSong.artists,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (notificationAccess) {
                    FloatingActionButton(
                        onClick = {
                            onAction(SavedPageAction.OnToggleAutoChange)
                            if (!state.autoChange) {
                                onNavigateToLyrics()
                            }
                        },
                        shape = Sunny.toShape(),
                        containerColor = if (state.autoChange) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSecondary,
                        contentColor = if (state.autoChange) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Meteor,
                            contentDescription = "Rush Mode",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                MediumFloatingActionButton(
                    onClick = { onAction(SavedPageAction.OnToggleSearchSheet) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (state.songsAsc.isEmpty()) {

                Empty()

            } else {

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SortOrder.entries.toList(), key = { it.stringRes }) { order ->
                        ToggleButton(
                            checked = order == state.sortOrder,
                            onCheckedChange = {
                                onAction(SavedPageAction.UpdateSortOrder(order))
                            }
                        ) {
                            Text(
                                text = stringResource(order.stringRes)
                            )
                        }
                    }
                }

                val listState = rememberLazyListState()
                AnimatedContent(
                    targetState = state.sortOrder
                ) { sortOrder ->
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .simpleVerticalScrollbar(listState)
                            .animateContentSize()
                    ) {
                        items(
                            items = when (sortOrder) {
                                SortOrder.DATE_ADDED -> state.songsByTime
                                SortOrder.TITLE_ASC -> state.songsAsc
                                SortOrder.TITLE_DESC -> state.songsDesc
                            },
                            key = { it.id }) {
                            SongCard(
                                song = it,
                                onDelete = {
                                    onAction(SavedPageAction.OnDeleteSong(it))
                                },
                                onClick = {
                                    onAction(SavedPageAction.ChangeCurrentSong(it.id))
                                    onNavigateToLyrics()
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.padding(60.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val songs = (0..100).map {
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
            dateAdded = it.toLong()
        )
    }

    var state by remember {
        mutableStateOf(
            SavedPageState(
                songsAsc = songs,
                songsDesc = songs.sortedByDescending { it.title },
                songsByTime = songs.sortedBy { it.dateAdded }
            )
        )
    }

    RushTheme(
        theme = Theme(
            appTheme = AppTheme.DARK,
            style = PaletteStyle.FruitSalad
        )
    ) {
        SavedPage(
            state = state,
            notificationAccess = true,
            onNavigateToSettings = {},
            onNavigateToLyrics = {},
            onAction = {
                when (it) {
                    is SavedPageAction.UpdateSortOrder -> state =
                        state.copy(sortOrder = it.sortOrder)

                    else -> {}
                }
            }
        )
    }
}