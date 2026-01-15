package com.shub39.rush.presentation.saved

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonGroupDefaults
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
import androidx.compose.material3.ToggleButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.domain.dataclasses.Song
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.enums.PaletteStyle
import com.shub39.rush.domain.enums.SortOrder
import com.shub39.rush.presentation.components.ArtFromUrl
import com.shub39.rush.presentation.components.Empty
import com.shub39.rush.presentation.components.PageFill
import com.shub39.rush.presentation.components.RushTheme
import com.shub39.rush.presentation.saved.component.SongCard
import com.shub39.rush.presentation.simpleVerticalScrollbar
import com.shub39.rush.presentation.toStringRes

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

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                MediumFlexibleTopAppBar(
                    title = { Text(stringResource(R.string.rush_branding)) },
                    subtitle = { Text(text = "${state.songsAsc.size} " + stringResource(R.string.saved)) },
                    actions = {
                        IconButton(
                            onClick = onNavigateToSettings,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.settings),
                                contentDescription = "Settings"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(
                                bottomStart = 32.dp,
                                bottomEnd = 32.dp
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                    ) {
                        SortOrder.entries.toList().forEach { order ->
                            ToggleButton(
                                checked = order == state.sortOrder,
                                onCheckedChange = {
                                    onAction(SavedPageAction.UpdateSortOrder(order))
                                },
                                colors = ToggleButtonDefaults.toggleButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                modifier = Modifier.weight(1f),
                                shapes = when (order) {
                                    SortOrder.DATE_ADDED -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    SortOrder.TITLE_ASC -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                    SortOrder.TITLE_DESC -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                }
                            ) {
                                Text(
                                    text = stringResource(order.toStringRes())
                                )
                            }
                        }
                    }
                }
            }
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
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 32.dp,
                                    topEnd = 32.dp
                                )
                            )
                            .clickable { onNavigateToLyrics() },
                        contentColor = Color(state.extractedColors.cardContentMuted),
                        containerColor = Color(state.extractedColors.cardBackgroundMuted)
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
                        contentColor = if (state.autoChange) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.secondary,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.meteor),
                            contentDescription = "Rush Mode",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                MediumFloatingActionButton(
                    onClick = { onAction(SavedPageAction.OnToggleSearchSheet) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.search),
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

                AnimatedContent(
                    targetState = state.sortOrder
                ) { sortOrder ->
                    val songs = when (sortOrder) {
                        SortOrder.DATE_ADDED -> state.songsByTime
                        SortOrder.TITLE_ASC -> state.songsAsc
                        SortOrder.TITLE_DESC -> state.songsDesc
                    }

                    val listState = rememberLazyListState()
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .simpleVerticalScrollbar(listState)
                            .animateContentSize(),
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            bottom = 60.dp
                        )
                    ) {
                        items(
                            items = songs,
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
            style = PaletteStyle.TONALSPOT
        )
    ) {
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
            onNavigateToSettings = {}
        )
    }
}