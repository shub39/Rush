package com.shub39.rush.lyrics.presentation.saved

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.core.domain.Route
import com.shub39.rush.core.domain.enums.SortOrder
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.Empty
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.core.presentation.simpleVerticalScrollbar
import com.shub39.rush.lyrics.domain.SongUi
import com.shub39.rush.lyrics.presentation.saved.component.GroupedCard
import com.shub39.rush.lyrics.presentation.saved.component.SongCard
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Search
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.rush_transparent
import rush.app.generated.resources.saved

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPage(
    state: SavedPageState,
    currentSong: SongUi?,
    autoChange: Boolean,
    notificationAccess: Boolean,
    action: (SavedPageAction) -> Unit,
    navigator: (Route) -> Unit
) = PageFill {
    val sortOrderChips = remember { SortOrder.entries.toTypedArray() }

    Scaffold(
        modifier = Modifier.widthIn(max = 500.dp),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.saved)) },
                actions = {
                    IconButton(
                        onClick = { navigator(Route.SettingsGraph) }
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
            BottomAppBar(
                modifier = Modifier.clip(
                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = currentSong
                    ) {
                        when (it) {
                            null -> {}
                            else -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(0.75f)
                                        .clickable { navigator(Route.LyricsGraph) }
                                ) {
                                    ArtFromUrl(
                                        imageUrl = it.artUrl,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(MaterialTheme.shapes.extraSmall)
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = it.title,
                                            maxLines = 1,
                                            style = MaterialTheme.typography.titleMedium,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = it.artists,
                                            maxLines = 1,
                                            style = MaterialTheme.typography.bodyMedium,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (notificationAccess) {
                        IconButton(
                            onClick = {
                                action(SavedPageAction.OnToggleAutoChange)
                                if (!autoChange) {
                                    navigator(Route.LyricsGraph)
                                }
                            },
                            colors = if (autoChange) {
                                IconButtonDefaults.filledIconButtonColors()
                            } else {
                                IconButtonDefaults.iconButtonColors()
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.rush_transparent),
                                contentDescription = "App Icon",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { action(SavedPageAction.OnToggleSearchSheet) }
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .widthIn(max = 500.dp)
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
                    items(sortOrderChips, key = { it.stringRes.key }) {
                        FilterChip(
                            selected = it == state.sortOrder,
                            onClick = {
                                action(SavedPageAction.UpdateSortOrder(it))
                            },
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
                    var expandedCardId by rememberSaveable {
                        mutableStateOf<String?>(null)
                    }
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
                                        navigator(Route.LyricsGraph)
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
                                        navigator(Route.LyricsGraph)
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
                                        navigator(Route.LyricsGraph)
                                    }
                                )
                            }

                            SortOrder.ARTISTS_ASC -> items(state.groupedArtist, key = { it.key }) { map ->
                                GroupedCard(
                                    map = map,
                                    isExpanded = expandedCardId == map.key,
                                    onClick = {
                                        action(SavedPageAction.ChangeCurrentSong(it.id))
                                        navigator(Route.LyricsGraph)
                                    },
                                    onCardClick = {
                                        expandedCardId =
                                            if (expandedCardId == map.key) null else map.key
                                    }
                                )
                            }

                            SortOrder.ALBUM_ASC -> items(state.groupedAlbum, key = { it.key }) { map ->
                                GroupedCard(
                                    map = map,
                                    isExpanded = expandedCardId == map.key,
                                    onClick = {
                                        action(SavedPageAction.ChangeCurrentSong(it.id))
                                        navigator(Route.LyricsGraph)
                                    },
                                    onCardClick = {
                                        expandedCardId =
                                            if (expandedCardId == map.key) null else map.key
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

//@Preview
//@Composable
//private fun Preview() {
//    val context = LocalContext.current
//
//    startKoin {
//        modules(
//            module {
//                single { provideImageLoader(context) }
//            }
//        )
//    }
//
//    val songs = (0..100).map {
//        Song(
//            id = it.toLong(),
//            title = "Song $it",
//            artists = "Artist $it",
//            lyrics = "",
//            album = "Album $it",
//            sourceUrl = "",
//            artUrl = "",
//            geniusLyrics = null,
//            syncedLyrics = null,
//            dateAdded = it.toLong()
//        )
//    }
//
//    var state by remember { mutableStateOf(SavedPageState(
//        songsAsc = songs,
//        songsByTime = songs
//    ))}
//
//    RushTheme(
//        state = Theme(
//            appTheme = AppTheme.DARK
//        )
//    ) {
//        SavedPage(
//            state = state,
//            currentSong = SongUi(title = "Satan in the wait", artists = "Daughters"),
//            autoChange = false,
//            notificationAccess = true,
//            action = {},
//            navigator = {}
//        )
//    }
//}