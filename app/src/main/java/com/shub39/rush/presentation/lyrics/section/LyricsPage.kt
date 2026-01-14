package com.shub39.rush.presentation.lyrics.section

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.domain.enums.LyricsBackground
import com.shub39.rush.presentation.KeepScreenOn
import com.shub39.rush.presentation.audioDependentBackgrounds
import com.shub39.rush.presentation.components.ArtFromUrl
import com.shub39.rush.presentation.components.Empty
import com.shub39.rush.presentation.fadeBottomToTop
import com.shub39.rush.presentation.fadeTopToBottom
import com.shub39.rush.presentation.glowBackground
import com.shub39.rush.presentation.lyrics.ApplyLyricsBackground
import com.shub39.rush.presentation.lyrics.LyricsPageAction
import com.shub39.rush.presentation.lyrics.LyricsPageState
import com.shub39.rush.presentation.lyrics.LyricsState
import com.shub39.rush.presentation.lyrics.SearchState
import com.shub39.rush.presentation.lyrics.component.ActionsRow
import com.shub39.rush.presentation.lyrics.component.ErrorCard
import com.shub39.rush.presentation.lyrics.component.FetchingCard
import com.shub39.rush.presentation.lyrics.component.LrcCorrectSheet
import com.shub39.rush.presentation.lyrics.component.PlainLyrics
import com.shub39.rush.presentation.lyrics.component.SyncedLyrics
import com.shub39.rush.presentation.lyrics.getCardColors
import com.shub39.rush.presentation.lyrics.getHypnoticColors
import com.shub39.rush.presentation.lyrics.getWaveColors
import com.shub39.rush.presentation.toAlignment
import io.gitlab.bpavuk.viz.VisualizerData
import io.gitlab.bpavuk.viz.midBucket
import io.gitlab.bpavuk.viz.trebleBucket
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LyricsPage(
    onEdit: () -> Unit,
    onShare: () -> Unit,
    action: (LyricsPageAction) -> Unit,
    state: LyricsPageState,
    waveData: VisualizerData?,
    notificationAccess: Boolean,
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    // keeping the screen on
    KeepScreenOn()

    val (cardBackground, cardContent) = getCardColors(state)
    val (hypnoticColor1, hypnoticColor2) = getHypnoticColors(state)
    val waveColors = getWaveColors(state)

    val glowMultiplier by animateFloatAsState(calculateGlowMultiplier(waveData))

    val top by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }

    LaunchedEffect(state.lyricsState) {
        delay(100)
        lazyListState.animateScrollToItem(0)
    }

    // Content Start
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = cardBackground,
                contentColor = cardContent
            ),
            shape = RoundedCornerShape(0.dp)
        ) {
            AnimatedContent(
                targetState = state.lyricsState,
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { lyricsState ->
                when (lyricsState) {
                    LyricsState.Idle -> {
                        Empty(
                            suggestion = false,
                            color = cardContent,
                            imageVector = FontAwesomeIcons.Solid.Music
                        )
                    }

                    is LyricsState.Fetching -> {
                        FetchingCard(
                            fetching = lyricsState.name,
                            colors = Pair(cardContent, cardBackground)
                        )
                    }

                    is LyricsState.LyricsError -> {
                        ErrorCard(
                            error = lyricsState.errorCode,
                            debugMessage = lyricsState.debugMessage,
                            colors = Pair(cardContent, cardBackground)
                        )
                    }

                    is LyricsState.Loaded -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Updating colors
                            LaunchedEffect(lyricsState.song.artUrl) {
                                lyricsState.song.artUrl?.let {
                                    action(LyricsPageAction.UpdateExtractedColors(it))
                                }
                            }

                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                val landscape by remember { mutableStateOf(this.maxHeight < this.maxWidth) }

                                ApplyLyricsBackground(
                                    background = state.lyricsBackground,
                                    artUrl = lyricsState.song.artUrl,
                                    cardBackground = cardBackground,
                                    waveData = waveData,
                                    waveColors = waveColors,
                                    hypnoticColor1 = hypnoticColor1,
                                    hypnoticColor2 = hypnoticColor2
                                )

                                Column {
                                    if (!landscape) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                modifier = Modifier.align(Alignment.TopCenter)
                                            ) {
                                                AnimatedVisibility(
                                                    visible = !state.sync && top > 2 && state.lyricsBackground != LyricsBackground.ALBUM_ART
                                                ) {
                                                    ArtFromUrl(
                                                        imageUrl = lyricsState.song.artUrl!!,
                                                        highlightColor = cardContent,
                                                        baseColor = Color.Transparent,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(100.dp)
                                                            .fadeBottomToTop()
                                                    )
                                                }
                                            }

                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalAlignment = state.textPrefs.lyricsAlignment.toAlignment()
                                            ) {
                                                Spacer(modifier = Modifier.height(40.dp))

                                                AnimatedVisibility(
                                                    visible = top <= 2 && !state.sync
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(
                                                            horizontal = 16.dp,
                                                            vertical = 8.dp
                                                        )
                                                    ) {
                                                        ArtFromUrl(
                                                            imageUrl = lyricsState.song.artUrl!!,
                                                            highlightColor = cardContent,
                                                            baseColor = Color.Transparent,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier
                                                                .size(80.dp)
                                                                .aspectRatio(1f)
                                                                .clip(MaterialTheme.shapes.small)
                                                        )
                                                    }
                                                }

                                                Text(
                                                    text = lyricsState.song.title,
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier
                                                        .padding(horizontal = 16.dp)
                                                        .basicMarquee()
                                                )

                                                Text(
                                                    text = lyricsState.song.artists,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier
                                                        .padding(horizontal = 16.dp)
                                                        .basicMarquee()
                                                )

                                                // Actions Row
                                                ActionsRow(
                                                    state = state,
                                                    action = action,
                                                    notificationAccess = notificationAccess,
                                                    cardBackground = cardBackground,
                                                    cardContent = cardContent,
                                                    onShare = onShare,
                                                    onEdit = onEdit,
                                                    glowMultiplier = glowMultiplier,
                                                    modifier = Modifier.padding(16.dp)
                                                )
                                            }
                                        }

                                        if (!state.sync) {
                                            PlainLyrics(
                                                state = state,
                                                lazyListState = lazyListState,
                                                cardContent = cardContent,
                                                action = action,
                                                coroutineScope = coroutineScope,
                                                modifier = Modifier
                                                    .widthIn(max = 500.dp)
                                                    .fillMaxWidth()
                                                    .fadeTopToBottom()
                                            )
                                        } else if (lyricsState.song.syncedLyrics != null) {
                                            SyncedLyrics(
                                                state = state,
                                                coroutineScope = coroutineScope,
                                                lazyListState = lazyListState,
                                                cardContent = cardContent,
                                                action = action,
                                                modifier = Modifier
                                                    .widthIn(max = 500.dp)
                                                    .fillMaxWidth()
                                                    .fadeTopToBottom()
                                            )
                                        }
                                    } else {
                                        // landscape UI

                                        Row(
                                            modifier = Modifier
                                                .padding(start = 40.dp)
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(0.3f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(
                                                        horizontal = 16.dp,
                                                        vertical = 8.dp
                                                    )
                                                ) {
                                                    ArtFromUrl(
                                                        imageUrl = lyricsState.song.artUrl!!,
                                                        highlightColor = cardContent,
                                                        baseColor = Color.Transparent,
                                                        contentScale = ContentScale.Fit,
                                                        modifier = Modifier
                                                            .size(100.dp)
                                                            .clip(MaterialTheme.shapes.small)
                                                    )
                                                }

                                                Text(
                                                    text = lyricsState.song.title,
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier
                                                        .padding(horizontal = 16.dp)
                                                        .basicMarquee()
                                                )

                                                Text(
                                                    text = lyricsState.song.artists,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier
                                                        .padding(horizontal = 16.dp)
                                                        .basicMarquee()
                                                )
                                            }

                                            if (!state.sync) {
                                                PlainLyrics(
                                                    state = state,
                                                    lazyListState = lazyListState,
                                                    cardContent = cardContent,
                                                    action = action,
                                                    coroutineScope = coroutineScope,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fadeTopToBottom()
                                                )
                                            } else if (lyricsState.song.syncedLyrics != null) {
                                                SyncedLyrics(
                                                    state = state,
                                                    coroutineScope = coroutineScope,
                                                    lazyListState = lazyListState,
                                                    cardContent = cardContent,
                                                    action = action,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fadeTopToBottom()
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

        AnimatedVisibility(
            visible = state.autoChange,
            modifier = Modifier
                .widthIn(max = 150.dp)
                .padding(vertical = 32.dp)
                .align(Alignment.BottomStart),
            enter = slideInHorizontally(),
            exit = fadeOut()
        ) {
            AnimatedContent(
                targetState = state.searchState
            ) {
                when (it) {
                    SearchState.Idle -> {}
                    is SearchState.Searching -> {
                        Card(
                            shape = RoundedCornerShape(
                                topEnd = 100.dp,
                                bottomEnd = 100.dp
                            ),
                            colors = CardDefaults.cardColors(
                                contentColor = cardBackground,
                                containerColor = cardContent
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LoadingIndicator(
                                    color = cardBackground,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = it.query,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    SearchState.UserPrompt -> {
                        Card(
                            shape = RoundedCornerShape(
                                topEnd = 100.dp,
                                bottomEnd = 100.dp
                            ),
                            colors = CardDefaults.cardColors(
                                contentColor = cardBackground,
                                containerColor = cardContent
                            ),
                            onClick = { action(LyricsPageAction.OnToggleSearchSheet) }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.SearchOff,
                                    contentDescription = "No exact match found"
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.not_found),
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = state.sync,
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.BottomEnd)
        ) {
            FloatingActionButton(
                contentColor = cardBackground,
                containerColor = cardContent,
                elevation = FloatingActionButtonDefaults.loweredElevation(0.dp),
                shape = CircleShape,
                onClick = { action(LyricsPageAction.OnPauseOrResume) },
                modifier = Modifier.run {
                    if (state.lyricsBackground in audioDependentBackgrounds) {
                        glowBackground((24 * glowMultiplier).dp, CircleShape, cardContent)
                    } else {
                        this
                    }
                }
            ) {
                Icon(
                    imageVector = if (state.playingSong.speed == 0f) {
                        Icons.Rounded.PlayArrow
                    } else {
                        Icons.Rounded.Pause
                    },
                    contentDescription = "Pause or Resume"
                )
            }
        }
    }

    // Lyrics Correction from LRCLIB
    if (state.lyricsCorrect) {
        LrcCorrectSheet(
            action = action,
            state = state,
            track = (state.lyricsState as? LyricsState.Loaded)?.song?.title ?: "",
            artist = (state.lyricsState as? LyricsState.Loaded)?.song?.artists ?: "",
        )
    }
}

fun calculateGlowMultiplier(waveData: VisualizerData?): Float {
    if (waveData == null) return 0f

    val mid = waveData.midBucket().max()
    val treble = waveData.trebleBucket().max()
    return (mid + treble).toFloat().absoluteValue / 128f
}
