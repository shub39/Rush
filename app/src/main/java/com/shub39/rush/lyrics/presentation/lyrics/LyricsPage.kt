package com.shub39.rush.lyrics.presentation.lyrics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.MeshGradient
import com.shub39.rush.R
import com.shub39.rush.core.data.Settings
import com.shub39.rush.core.data.SongDetails
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.domain.Sources
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.generateGradientColors
import com.shub39.rush.core.presentation.getMainTitle
import com.shub39.rush.core.presentation.openLinkInBrowser
import com.shub39.rush.lyrics.data.listener.MediaListener
import com.shub39.rush.lyrics.presentation.lyrics.component.Empty
import com.shub39.rush.lyrics.presentation.lyrics.component.ErrorCard
import com.shub39.rush.lyrics.presentation.lyrics.component.LoadingCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsPage(
    onShare: () -> Unit,
    action: (LyricsPageAction) -> Unit,
    state: LyricsPageState,
    notificationAccess: Boolean,
    settings: Settings
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    // States which change frequently
    var syncedAvailable by remember { mutableStateOf(false) }
    var sync by remember { mutableStateOf(false) }
    var lyricsCorrect by remember { mutableStateOf(false) }
    var source by remember { mutableStateOf(Sources.LrcLib) }
    var selectedLines by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }

    val cardBackground by animateColorAsState(
        targetValue = when (settings.lyricsColor) {
            CardColors.MUTED -> state.extractedColors.cardBackgroundMuted
            else -> state.extractedColors.cardBackgroundDominant
        },
        label = "cardBackground"
    )
    val cardContent by animateColorAsState(
        targetValue = when (settings.lyricsColor) {
            CardColors.MUTED -> state.extractedColors.cardContentMuted
            else -> state.extractedColors.cardContentDominant
        },
        label = "cardContent"
    )

    val hypnoticColor1 by animateColorAsState(
        targetValue = state.extractedColors.cardBackgroundDominant,
        label = "hypnotic color 1"
    )

    val hypnoticColor2 by animateColorAsState(
        targetValue = state.extractedColors.cardBackgroundMuted,
        label = "hypnotic color 2"
    )

    val modifier = if (settings.hypnoticCanvas) {
        Modifier.shaderBackground(
            MeshGradient(
                colors = generateGradientColors(
                    color1 = hypnoticColor1,
                    color2 = hypnoticColor2,
                    steps = 6
                ).toTypedArray()
            )
        )
    } else {
        Modifier
    }

    LaunchedEffect(state.song) {
        delay(100)
        lazyListState.animateScrollToItem(0)
    }

    // Content Start
    Card(
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (settings.hypnoticCanvas) Color.Transparent else cardBackground,
            contentColor = cardContent
        ),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.fetching.first || (state.searching.first && state.autoChange)) {

                LoadingCard(
                    state.fetching,
                    state.searching,
                    Pair(cardContent, cardBackground)
                )

            } else if (state.error != null) {

                ErrorCard(state.error, Pair(cardContent, cardBackground))

            } else if (state.song == null) {

                Empty()

            } else {

                // setting the temporary states
                LaunchedEffect(state.song.lyrics) {
                    if (state.song.lyrics.isNotEmpty()) {
                        source = Sources.LrcLib
                    } else if (state.song.geniusLyrics != null) {
                        source = Sources.Genius
                    }

                    if (state.song.syncedLyrics != null) {
                        syncedAvailable = true

                        sync = getMainTitle(state.playingSong.title).trim()
                            .lowercase() == state.song.title.trim()
                            .lowercase()

                    }

                    action(
                        LyricsPageAction.UpdateExtractedColors(context)
                    )
                }

                LaunchedEffect(state.playingSong.title) {
                    syncedAvailable = (state.song.syncedLyrics != null)

                    sync = getMainTitle(state.playingSong.title).trim()
                        .lowercase() == state.song.title.trim()
                        .lowercase() && syncedAvailable
                }

                LaunchedEffect(source) {
                    selectedLines = emptyMap()
                }

                Box {
                    val top by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }

                    Column {
                        AnimatedVisibility(top > 2 && !settings.hypnoticCanvas) {
                            ArtFromUrl(
                                imageUrl = state.song.artUrl,
                                highlightColor = cardContent,
                                baseColor = Color.Transparent,
                                modifier = Modifier
                                    .height(150.dp)
                                    .fillMaxWidth(),
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                cardBackground
                                            )
                                        )
                                    )
                                    .height(150.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(top = 64.dp)
                    ) {
                        AnimatedVisibility(
                            visible = top < 3
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                ArtFromUrl(
                                    imageUrl = state.song.artUrl,
                                    highlightColor = cardContent,
                                    baseColor = Color.Transparent,
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.small)
                                        .size(150.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = state.song.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 2,
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                )

                                Text(
                                    text = state.song.artists,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }

                        // Actions Row
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        if (selectedLines.isEmpty()) {
                                            copyToClipBoard(
                                                context,
                                                if (source == Sources.LrcLib) {
                                                    state.song.lyrics.joinToString("\n") { it.value }
                                                } else {
                                                    state.song.geniusLyrics?.joinToString("\n") { it.value }
                                                        ?: ""
                                                },
                                                "Complete Lyrics"
                                            )
                                        } else {
                                            copyToClipBoard(
                                                context,
                                                selectedLines.toSortedMap().values.joinToString("\n"),
                                                "Selected Lyrics"
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.round_content_copy_24),
                                        contentDescription = null
                                    )
                                }

                                AnimatedVisibility(visible = selectedLines.isEmpty()) {
                                    IconButton(onClick = {
                                        source = if (source == Sources.LrcLib) Sources.Genius else Sources.LrcLib
                                        sync = false
                                    }) {
                                        if (source == Sources.Genius) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.round_lyrics_24),
                                                contentDescription = null
                                            )
                                        } else {
                                            Icon(
                                                painter = painterResource(id = R.drawable.genius),
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                                AnimatedVisibility(
                                    visible = source == Sources.LrcLib && selectedLines.isEmpty()
                                ) {
                                    IconButton(
                                        onClick = {
                                            lyricsCorrect = true
                                            sync = false
                                            if (state.autoChange) action(
                                                LyricsPageAction.OnToggleAutoChange
                                            )
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.round_edit_note_24),
                                            contentDescription = null
                                        )
                                    }
                                }

                                AnimatedVisibility(
                                    visible = syncedAvailable && selectedLines.isEmpty() && source == Sources.LrcLib && notificationAccess
                                ) {
                                    Row {
                                        IconButton(
                                            onClick = { sync = !sync },
                                            colors = if (sync) {
                                                IconButtonDefaults.iconButtonColors(
                                                    contentColor = cardBackground,
                                                    containerColor = cardContent
                                                )
                                            } else {
                                                IconButtonDefaults.iconButtonColors()
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.round_sync_24),
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                                AnimatedVisibility(visible = notificationAccess) {
                                    IconButton(
                                        onClick = { action(LyricsPageAction.OnToggleAutoChange) },
                                        colors = if (state.autoChange) {
                                            IconButtonDefaults.iconButtonColors(
                                                contentColor = cardBackground,
                                                containerColor = cardContent
                                            )
                                        } else {
                                            IconButtonDefaults.iconButtonColors()
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.rush_transparent),
                                            contentDescription = null,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }

                                AnimatedVisibility(visible = selectedLines.isNotEmpty()) {
                                    Row {
                                        IconButton(onClick = {
                                            action(
                                                LyricsPageAction.OnUpdateShareLines(
                                                    songDetails = SongDetails(
                                                        title = state.song.title,
                                                        artist = state.song.artists,
                                                        album = state.song.album,
                                                        artUrl = state.song.artUrl ?: ""
                                                    ),
                                                    shareLines = selectedLines
                                                )
                                            )
                                            onShare()
                                        }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.round_share_24),
                                                contentDescription = null
                                            )
                                        }

                                        IconButton(onClick = { selectedLines = emptyMap() }) {
                                            Icon(
                                                imageVector = Icons.Rounded.Clear,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Plain lyrics
                if (!sync) {
                    LazyColumn(
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                            .padding(
                                end = 16.dp,
                                start = 16.dp,
                                top = 16.dp,
                                bottom = 32.dp
                            ),
                        state = lazyListState
                    ) {
                        items(
                            items = if (source == Sources.LrcLib) state.song.lyrics else state.song.geniusLyrics
                                ?: emptyList(),
                            key = { it.key }
                        ) {
                            if (it.value.isNotBlank()) {
                                val isSelected = selectedLines.contains(it.key)
                                val containerColor by animateColorAsState(
                                    targetValue = when (!isSelected) {
                                        true -> Color.Transparent
                                        else -> cardContent.copy(alpha = 0.3f)
                                    },
                                    label = "container"
                                )

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
                                                settings.maxLines
                                            )
                                            isSelected != isSelected
                                        },
                                        shape = MaterialTheme.shapes.small,
                                        colors = CardDefaults.cardColors(
                                            containerColor = containerColor,
                                            contentColor = cardContent
                                        )
                                    ) {
                                        Text(
                                            text = it.value,
                                            style = TextStyle(
                                                fontSize = 19.sp,
                                                fontFamily = FontFamily(Font(R.font.poppins_regular))
                                            ),
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Lyrics not found from LRCLIB
                        if (state.song.lyrics.isEmpty() && source != Sources.Genius) {
                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Spacer(modifier = Modifier.padding(10.dp))

                                    Icon(
                                        painter = painterResource(id = R.drawable.round_warning_24),
                                        contentDescription = null,
                                        modifier = Modifier.size(100.dp)
                                    )

                                    Spacer(modifier = Modifier.padding(10.dp))

                                    Text(text = stringResource(id = R.string.no_lyrics))
                                }
                            }
                        }

                        // Bottom Actions Row
                        item {
                            Spacer(modifier = Modifier.padding(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            lazyListState.scrollToItem(0)
                                        }
                                    },
                                    enabled = if (source == Sources.LrcLib) state.song.lyrics.isNotEmpty() else !state.song.geniusLyrics.isNullOrEmpty()
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.round_arrow_upward_24),
                                        contentDescription = null
                                    )
                                }

                                Button(
                                    onClick = {
                                        openLinkInBrowser(
                                            context,
                                            state.song.sourceUrl
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = cardContent.copy(alpha = 0.3f),
                                        contentColor = cardContent
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.source)
                                    )
                                }

                                IconButton(
                                    onClick = { action(LyricsPageAction.OnToggleSearchSheet) },
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.round_search_24),
                                        contentDescription = null
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.padding(10.dp))
                        }
                    }
                } else if (state.song.syncedLyrics != null) {

                    // updater for synced lyrics
                    LaunchedEffect(state.playingSong.position) {
                        coroutineScope.launch {
                            var currentIndex =
                                getCurrentLyricIndex(
                                    state.playingSong.position,
                                    state.song.syncedLyrics
                                )
                            currentIndex -= 3
                            lazyListState.animateScrollToItem(currentIndex.coerceAtLeast(0))
                        }
                    }

                    // Synced Lyrics
                    LazyColumn(
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                            .padding(
                                end = 16.dp,
                                start = 16.dp,
                                top = 16.dp,
                                bottom = 32.dp
                            ),
                        state = lazyListState
                    ) {
                        items(state.song.syncedLyrics, key = { it.time }) { lyric ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val textColor by animateColorAsState(
                                    targetValue = if (lyric.time <= state.playingSong.position + 1000) {
                                        cardContent
                                    } else {
                                        cardContent.copy(0.3f)
                                    },
                                    label = "textColor"
                                )

                                Card(
                                    modifier = Modifier.padding(6.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.Transparent,
                                        contentColor = cardContent
                                    ),
                                    shape = MaterialTheme.shapes.small,
                                    onClick = {
                                        MediaListener.seek(lyric.time)
                                    }
                                ) {
                                    if (lyric.text.isNotEmpty()) {
                                        Text(
                                            text = lyric.text,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor,
                                            fontSize = 19.sp,
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.round_music_note_24),
                                            contentDescription = null,
                                            tint = textColor
                                        )
                                    }
                                }
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

    // Lyrics Correction from LRCLIB
    if (lyricsCorrect) {
        var track by remember { mutableStateOf("") }
        var artist by remember { mutableStateOf("") }

        BasicAlertDialog(
            onDismissRequest = { lyricsCorrect = false }
        ) {
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.padding(top = 32.dp, bottom = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.correct_lyrics),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        OutlinedTextField(
                            value = track,
                            onValueChange = { track = it },
                            singleLine = true,
                            shape = MaterialTheme.shapes.extraLarge,
                            label = { Text(text = stringResource(R.string.track)) }
                        )

                        OutlinedTextField(
                            value = artist,
                            onValueChange = { artist = it },
                            singleLine = true,
                            shape = MaterialTheme.shapes.extraLarge,
                            label = { Text(text = stringResource(R.string.artist)) }
                        )
                    }

                    Button(
                        onClick = {
                            action(LyricsPageAction.OnLrcSearch(track, artist))
                        },
                        enabled = track.isNotBlank() && !state.lrcCorrect.searching,
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (!state.lrcCorrect.searching) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_search_24),
                                contentDescription = null
                            )
                        } else {
                            CircularProgressIndicator(
                                strokeCap = StrokeCap.Round,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(state.lrcCorrect.searchResults, key = { it.id }) {
                            Card(
                                onClick = {
                                    action(
                                        LyricsPageAction.OnUpdateSongLyrics(
                                            state.song?.id!!,
                                            it.plainLyrics!!,
                                            it.syncedLyrics
                                        )
                                    )
                                    lyricsCorrect = false
                                },
                                colors = when (it.syncedLyrics) {
                                    null -> CardDefaults.elevatedCardColors()
                                    else -> CardDefaults.elevatedCardColors(
                                        contentColor = MaterialTheme.colorScheme.primary,
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                },
                                shape = MaterialTheme.shapes.large,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(0.7f)
                                    ) {
                                        Text(
                                            text = it.name,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = it.artistName,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    if (it.syncedLyrics != null) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.round_sync_24),
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}