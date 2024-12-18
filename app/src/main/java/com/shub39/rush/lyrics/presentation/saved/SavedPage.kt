package com.shub39.rush.lyrics.presentation.saved

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.lyrics.presentation.lyrics.component.Empty
import com.shub39.rush.lyrics.presentation.saved.component.GroupedCard
import com.shub39.rush.lyrics.presentation.saved.component.SongCard
import com.shub39.rush.core.data.Settings
import com.shub39.rush.core.domain.Route
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.lyrics.data.listener.NotificationListener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPage(
    state: SavedPageState,
    currentSongImg: String?,
    action: (SavedPageAction) -> Unit,
    onSongClick: () -> Unit,
    settings: Settings,
    navigator: (Route) -> Unit
) {
    val context = LocalContext.current

    val sortOrderChips = remember { SortOrder.entries.toTypedArray() }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.saved)) },
                actions = {
                    IconButton(
                        onClick = { navigator(Route.SettingsGraph) }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.round_settings_24),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (state.songsAsc.isEmpty()) {

                Empty()

            } else {

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyRow(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .animateContentSize()
                    ) {
                        items(sortOrderChips, key = { it.textId }) {
                            FilterChip(
                                selected = it.sortOrder == settings.sortOrder,
                                onClick = {
                                    action(SavedPageAction.UpdateSortOrder(it.sortOrder))
                                },
                                label = { Text(stringResource(id = it.textId)) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }

                    AnimatedContent(
                        targetState = settings.sortOrder
                    ) { sortOrder ->
                        when(sortOrder) {
                            SortOrder.DATE_ADDED.sortOrder -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .animateContentSize()
                                ) {
                                    items(state.songsByTime, key = { it.id }) {
                                        SongCard(
                                            result = it,
                                            onDelete = {
                                                action(SavedPageAction.OnDeleteSong(it))
                                            },
                                            onClick = {
                                                action(SavedPageAction.ChangeCurrentSong(it.id))
                                                onSongClick()
                                            }
                                        )
                                    }

                                    item {
                                        Spacer(modifier = Modifier.padding(60.dp))
                                    }
                                }
                            }

                            SortOrder.TITLE_ASC.sortOrder -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .animateContentSize()
                                ) {
                                    items(state.songsAsc, key = { it.id }) {
                                        SongCard(
                                            result = it,
                                            onDelete = {
                                                action(SavedPageAction.OnDeleteSong(it))
                                            },
                                            onClick = {
                                                action(SavedPageAction.ChangeCurrentSong(it.id))
                                                onSongClick()
                                            }
                                        )
                                    }

                                    item {
                                        Spacer(modifier = Modifier.padding(60.dp))
                                    }
                                }
                            }

                            SortOrder.TITLE_DESC.sortOrder -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .animateContentSize()
                                ) {
                                    items(state.songsDesc, key = { it.id }) {
                                        SongCard(
                                            result = it,
                                            onDelete = {
                                                action(SavedPageAction.OnDeleteSong(it))
                                            },
                                            onClick = {
                                                action(SavedPageAction.ChangeCurrentSong(it.id))
                                                onSongClick()
                                            }
                                        )
                                    }

                                    item {
                                        Spacer(modifier = Modifier.padding(60.dp))
                                    }
                                }
                            }

                            SortOrder.ARTISTS_ASC.sortOrder -> {
                                var expandedCardId by rememberSaveable { mutableStateOf<String?>(null) }

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .animateContentSize()
                                ) {
                                    items(state.groupedArtist, key = { it.key }) { map ->
                                        GroupedCard(
                                            map = map,
                                            isExpanded = expandedCardId == map.key,
                                            onClick = {
                                                action(SavedPageAction.ChangeCurrentSong(it.id))
                                                onSongClick()
                                            },
                                            onCardClick = {
                                                expandedCardId =
                                                    if (expandedCardId == map.key) null else map.key
                                            }
                                        )
                                    }

                                    item {
                                        Spacer(modifier = Modifier.padding(60.dp))
                                    }
                                }
                            }

                            SortOrder.ALBUM_ASC.sortOrder -> {
                                var expandedCardId by rememberSaveable { mutableStateOf<String?>(null) }

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .animateContentSize()
                                ) {
                                    items(state.groupedAlbum, key = { it.key }) { map ->
                                        GroupedCard(
                                            map = map,
                                            isExpanded = expandedCardId == map.key,
                                            onClick = {
                                                action(SavedPageAction.ChangeCurrentSong(it.id))
                                                onSongClick()
                                            },
                                            onCardClick = {
                                                expandedCardId =
                                                    if (expandedCardId == map.key) null else map.key
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

            FloatingActionButton(
                onClick = { action(SavedPageAction.OnToggleSearchSheet) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_search_24),
                    contentDescription = null
                )
            }

            if (NotificationListener.canAccessNotifications(context)) {
                FloatingActionButton(
                    onClick = {
                        action(SavedPageAction.OnToggleAutoChange)
                        if (!state.autoChange) {
                            onSongClick()
                        }
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(top = 16.dp, end = 80.dp, bottom = 16.dp),
                    containerColor = if (state.autoChange) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.rush_transparent),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            AnimatedContent(
                targetState = currentSongImg,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
            ) {
                when (it) {
                    null -> {}
                    else -> {
                        FloatingActionButton(
                            onClick = onSongClick,
                            shape = MaterialTheme.shapes.extraLarge,
                        ) {
                            ArtFromUrl(
                                imageUrl = it,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(MaterialTheme.shapes.extraLarge)
                            )
                        }
                    }
                }
            }
        }
    }
}