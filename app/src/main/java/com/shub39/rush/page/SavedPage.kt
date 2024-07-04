package com.shub39.rush.page

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.shub39.rush.R
import com.shub39.rush.component.Empty
import com.shub39.rush.component.GroupedCard
import com.shub39.rush.component.SongCard
import com.shub39.rush.database.SettingsDataStore
import com.shub39.rush.viewmodel.RushViewModel
import kotlinx.coroutines.launch

@Composable
fun SavedPage(
    rushViewModel: RushViewModel,
    imageLoader: ImageLoader,
    bottomSheet: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val songs = rushViewModel.songs.collectAsState()
    val lazyListState = rememberLazyListState()
    val sortOrder by SettingsDataStore.getSortOrderFlow(context)
        .collectAsState(initial = "title_asc")
    val sortedSongs = when (sortOrder) {
        "title_asc" -> songs.value.sortedBy { it.title }
        else -> songs.value.sortedByDescending { it.title }
    }
    val groupedSongs = when (sortOrder) {
        "artists_asc" -> songs.value.groupBy { it.artists }
        else -> songs.value.groupBy { it.album ?: stringResource(id = R.string.unknown_album) }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (sortedSongs.isEmpty()) {
            Empty()
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyRow(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                ) {
                    items(SortOrder.entries.toTypedArray(), key = { it.textId }) {
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
                            .animateContentSize(),
                        state = lazyListState
                    ) {
                        items(sortedSongs, key = { it.id }) {
                            SongCard(
                                result = it,
                                onDelete = {
                                    rushViewModel.deleteSong(it)
                                },
                                onClick = {
                                    rushViewModel.changeCurrentSong(it.id)
                                    onClick()
                                },
                                imageLoader = imageLoader
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.padding(60.dp))
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .animateContentSize(),
                        state = lazyListState,
                    ) {
                        items(groupedSongs.entries.toList(), key = { it.key }) { map ->
                            GroupedCard(
                                map = map,
                                imageLoader = imageLoader,
                                onClick = {
                                    rushViewModel.changeCurrentSong(it.id)
                                    onClick()
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
            onClick = { bottomSheet() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 32.dp, bottom = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_search_24),
                contentDescription = null
            )
        }
    }
}

enum class SortOrder(
    val sortOrder: String,
    @StringRes val textId: Int
) {
    TITLE_ASC("title_asc", R.string.sort_title_asc),
    TITLE_DESC("title_desc", R.string.sort_title_desc),
    ARTISTS_ASC("artists_asc", R.string.sort_artists_asc),
    ALBUM_ASC("album_asc", R.string.sort_album_asc),
}