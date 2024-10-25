package com.shub39.rush.ui.page.saved

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.ui.page.lyrics.component.Empty
import com.shub39.rush.ui.page.saved.component.GroupedCard
import com.shub39.rush.ui.page.saved.component.SongCard
import com.shub39.rush.database.SettingsDataStore
import com.shub39.rush.listener.NotificationListener
import com.shub39.rush.logic.SortOrder
import kotlinx.coroutines.launch

@Composable
fun SavedPage(
    state: SavedPageState,
    action: (SavedPageAction) -> Unit,
    onSongClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val sortOrder by SettingsDataStore.getSortOrderFlow(context)
        .collectAsState(initial = "title_asc")
    val sortOrderChips = remember { SortOrder.entries.toTypedArray() }

    Box(
        modifier = Modifier.fillMaxSize()
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
                            selected = it.sortOrder == sortOrder,
                            onClick = {
                                coroutineScope.launch {
                                    SettingsDataStore.updateSortOrder(context, it.sortOrder)
                                }
                            },
                            label = { Text(stringResource(id = it.textId)) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                if (sortOrder == "title_asc" || sortOrder == "title_desc") {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .animateContentSize()
                    ) {
                        items(
                            if (sortOrder == "title_asc") state.songsAsc else state.songsDesc,
                            key = { it.id }
                        ) {
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
                } else {
                    var expandedCardId by rememberSaveable { mutableStateOf<String?>(null) }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .animateContentSize()
                    ) {
                        items(
                            if (sortOrder == "artist_asc") state.groupedArtist else state.groupedAlbum,
                            key = { it.key }
                        ) { map ->
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

    }
}