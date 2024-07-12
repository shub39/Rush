package com.shub39.rush.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.shub39.rush.R
import com.shub39.rush.component.ArtFromUrl
import com.shub39.rush.component.Empty
import com.shub39.rush.database.SettingsDataStore
import com.shub39.rush.viewmodel.RushViewModel
import kotlinx.coroutines.launch

@Composable
fun LyricsPage(
    rushViewModel: RushViewModel,
    lazyListState: LazyListState,
    imageLoader: ImageLoader,
    bottomSheet: () -> Unit,
) {
    val song by rushViewModel.currentSong.collectAsState()
    val fetching by rushViewModel.isFetchingLyrics.collectAsState()
    val context = LocalContext.current
    var isSharePageVisible by remember { mutableStateOf(false) }
    var selectedLines by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    val maxLinesFlow by SettingsDataStore.getMaxLinesFlow(context).collectAsState(initial = 6)
    val coroutineScope = rememberCoroutineScope()

    if (isSharePageVisible) {
        SharePage(
            onShare = { isSharePageVisible = false },
            onDismiss = { isSharePageVisible = false },
            song = song!!,
            selectedLines = selectedLines,
            imageLoader = imageLoader
        )
    }

    if (fetching) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeCap = StrokeCap.Round
                )
            }
        }
    } else if (song == null) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Empty()
        }
    } else {
        val nonNullSong = song!!

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
            ) {
                ArtFromUrl(
                    imageUrl = nonNullSong.artUrl,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(MaterialTheme.shapes.small),
                    imageLoader = imageLoader
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = nonNullSong.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = nonNullSong.artists,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    nonNullSong.album?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Row {
                        IconButton(onClick = {
                            openLinkInBrowser(
                                context,
                                nonNullSong.sourceUrl
                            )
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.genius),
                                contentDescription = null
                            )
                        }
                        AnimatedVisibility(visible = selectedLines.isNotEmpty()) {
                            Row {
                                IconButton(onClick = { isSharePageVisible = true }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.round_share_24),
                                        contentDescription = null
                                    )
                                }
                                IconButton(onClick = { selectedLines = emptyMap() }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.round_delete_forever_24),
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            }
            val lyrics = breakLyrics(nonNullSong.lyrics).entries.toList()

            LazyColumn(
                modifier = Modifier.padding(end = 16.dp, start = 16.dp, bottom = 16.dp),
                state = lazyListState
            ) {
                items(lyrics, key = { it.key }) {
                    if (it.value.isNotBlank()) {
                        val isSelected = selectedLines.contains(it.key)
                        val color = if (!isSelected) {
                            CardDefaults.cardColors()
                        } else {
                            CardDefaults.elevatedCardColors()
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Card(
                                modifier = Modifier
                                    .padding(3.dp),
                                onClick = {
                                    selectedLines = updateSelectedLines(
                                        selectedLines,
                                        it.key,
                                        it.value,
                                        maxLinesFlow
                                    )
                                    isSelected != isSelected
                                },
                                shape = MaterialTheme.shapes.small,
                                colors = color
                            ) {
                                Text(
                                    text = it.value,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.padding(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    lazyListState.scrollToItem(0)
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_arrow_upward_24),
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(text = stringResource(id = R.string.back_to_top))
                        }

                        OutlinedButton(onClick = { bottomSheet() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_search_24),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(text = stringResource(id = R.string.search_next))
                        }
                    }
                }
            }
        }

    }
}

private fun breakLyrics(lyrics: String): Map<Int, String> {
    val lines = lyrics.lines()
    val map = mutableMapOf<Int, String>()
    for (i in lines.indices) {
        map[i] = lines[i]
    }
    return map
}

private fun updateSelectedLines(
    selectedLines: Map<Int, String>,
    key: Int,
    value: String,
    maxSelections: Int = 6
): Map<Int, String> {
    return if (!selectedLines.contains(key) && selectedLines.size < maxSelections) {
        selectedLines.plus(key to value)
    } else {
        selectedLines.minus(key)
    }
}