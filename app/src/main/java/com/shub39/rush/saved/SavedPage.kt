package com.shub39.rush.saved

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.MaterialShapes.Companion.Sunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.domain.enums.SortOrder
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.Empty
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.core.presentation.simpleVerticalScrollbar
import com.shub39.rush.saved.component.SongCard
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Meteor
import compose.icons.fontawesomeicons.solid.Search

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SavedPage(
    state: SavedPageState,
    notificationAccess: Boolean,
    action: (SavedPageAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) = PageFill {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.rush_branding)) },
                subtitle = { Text(text = "${state.songsAsc.size} " + stringResource(R.string.saved)) },
                actions = {
                    FilledTonalIconButton(
                        onClick = onNavigateToSettings,
                        shapes = IconButtonShapes(
                            shape = CircleShape,
                            pressedShape = RoundedCornerShape(10.dp)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
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
                            action(SavedPageAction.OnToggleAutoChange)
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
                    onClick = { action(SavedPageAction.OnToggleSearchSheet) }
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(30.dp)
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
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SortOrder.entries.toList(), key = { it.stringRes }) {
                        val chipRoundness by animateDpAsState(
                            targetValue = if (it == state.sortOrder) 16.dp else 8.dp
                        )

                        FilterChip(
                            selected = it == state.sortOrder,
                            onClick = {
                                action(SavedPageAction.UpdateSortOrder(it))
                            },
                            shape = RoundedCornerShape(chipRoundness),
                            label = {
                                Text(
                                    text = stringResource(it.stringRes)
                                )
                            }
                        )
                    }
                }

                AnimatedContent(
                    targetState = state.sortOrder
                ) { sortOrder ->
                    val listState = rememberLazyListState()

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .simpleVerticalScrollbar(listState)
                            .animateContentSize()
                    ) {
                        when (sortOrder) {
                            SortOrder.DATE_ADDED -> items(state.songsByTime, key = { it.id }) {
                                SongCard(
                                    result = it,
                                    onDelete = {
                                        action(SavedPageAction.OnDeleteSong(it))
                                    },
                                    onClick = {
                                        action(SavedPageAction.ChangeCurrentSong(it.id))
                                        onNavigateToLyrics()
                                    }
                                )
                            }

                            SortOrder.TITLE_ASC -> items(state.songsAsc, key = { it.id }) {
                                SongCard(
                                    result = it,
                                    onDelete = {
                                        action(SavedPageAction.OnDeleteSong(it))
                                    },
                                    onClick = {
                                        action(SavedPageAction.ChangeCurrentSong(it.id))
                                        onNavigateToLyrics()
                                    }
                                )
                            }

                            SortOrder.TITLE_DESC -> items(state.songsDesc, key = { it.id }) {
                                SongCard(
                                    result = it,
                                    onDelete = {
                                        action(SavedPageAction.OnDeleteSong(it))
                                    },
                                    onClick = {
                                        action(SavedPageAction.ChangeCurrentSong(it.id))
                                        onNavigateToLyrics()
                                    }
                                )
                            }
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