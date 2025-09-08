package com.shub39.rush.lyrics.section

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.MeshGradient
import com.shub39.rush.core.domain.enums.LyricsBackground
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.Empty
import com.shub39.rush.core.presentation.KeepScreenOn
import com.shub39.rush.core.presentation.fadeBottomToTop
import com.shub39.rush.core.presentation.fadeTopToBottom
import com.shub39.rush.core.presentation.generateGradientColors
import com.shub39.rush.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.LyricsPageState
import com.shub39.rush.lyrics.component.ActionsRow
import com.shub39.rush.lyrics.component.ErrorCard
import com.shub39.rush.lyrics.component.LoadingCard
import com.shub39.rush.lyrics.component.LrcCorrectDialog
import com.shub39.rush.lyrics.component.PlainLyrics
import com.shub39.rush.lyrics.component.SyncedLyrics
import com.shub39.rush.lyrics.getCardColors
import com.shub39.rush.lyrics.getHypnoticColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Music
import kotlinx.coroutines.delay

@Composable
fun LyricsPage(
    onEdit: () -> Unit,
    onShare: () -> Unit,
    action: (LyricsPageAction) -> Unit,
    state: LyricsPageState,
    notificationAccess: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    // keeping the screen on
    KeepScreenOn()

    val (cardBackground, cardContent) = getCardColors(state)

    val (hypnoticColor1, hypnoticColor2) = getHypnoticColors(state)

    val top by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }

    LaunchedEffect(state.song) {
        delay(100)
        lazyListState.animateScrollToItem(0)
    }

    // Content Start
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .let {
                    when (state.lyricsBackground) {
                        LyricsBackground.HYPNOTIC -> {
                            it.shaderBackground(
                                shader = MeshGradient(
                                    colors = generateGradientColors(
                                        color1 = hypnoticColor1,
                                        color2 = hypnoticColor2,
                                        steps = 6
                                    ).toTypedArray()
                                ),
                                fallback = {
                                    Brush.horizontalGradient(
                                        generateGradientColors(
                                            color1 = hypnoticColor1,
                                            color2 = hypnoticColor2,
                                            steps = 6
                                        )
                                    )
                                }
                            )
                        }
                        LyricsBackground.ALBUM_ART -> it.background(cardBackground)
                        else -> it
                    }
                }
                .fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = if (state.lyricsBackground != LyricsBackground.SOLID_COLOR) Color.Transparent else cardBackground,
                contentColor = cardContent
            ),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.fetching.first || (state.searching.first && state.autoChange)) {

                    LoadingCard(
                        fetching = state.fetching,
                        searching = state.searching,
                        colors = Pair(cardContent, cardBackground)
                    )

                } else if (state.error != null) {

                    ErrorCard(
                        error = state.error,
                        colors = Pair(cardContent, cardBackground)
                    )

                } else if (state.song == null) {

                    Empty(
                        suggestion = false,
                        color = cardContent,
                        imageVector = FontAwesomeIcons.Solid.Music
                    )

                } else {
                    // Updating colors
                    LaunchedEffect(state.song.artUrl) {
                        state.song.artUrl?.let {
                            action(LyricsPageAction.UpdateExtractedColors(it))
                        }
                    }

                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val landscape by remember { mutableStateOf(this.maxHeight < this.maxWidth) }

                        // album art blurred
                        if (state.lyricsBackground == LyricsBackground.ALBUM_ART) {
                            ArtFromUrl(
                                imageUrl = state.song.artUrl,
                                modifier = Modifier
                                    .blur(80.dp)
                                    .fillMaxSize()
                            )

                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        color = cardBackground.copy(alpha = 0.5f)
                                    )
                            )
                        }

                        Column {
                            // Plain lyrics
                            if (!landscape) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        modifier = Modifier.align(Alignment.TopCenter)
                                    ) {
                                        AnimatedVisibility(
                                            visible = (top > 2 || state.sync) && state.lyricsBackground != LyricsBackground.ALBUM_ART
                                        ) {
                                            ArtFromUrl(
                                                imageUrl = state.song.artUrl!!,
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
                                        horizontalAlignment = when (state.textPrefs.textAlign) {
                                            TextAlign.Center -> Alignment.CenterHorizontally
                                            TextAlign.End -> Alignment.End
                                            else -> Alignment.Start
                                        }
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
                                                    imageUrl = state.song.artUrl!!,
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
                                            text = state.song.title,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .basicMarquee()
                                        )

                                        Text(
                                            text = state.song.artists,
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
                                } else if (state.song.syncedLyrics != null) {
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
                                                imageUrl = state.song.artUrl!!,
                                                highlightColor = cardContent,
                                                baseColor = Color.Transparent,
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .clip(MaterialTheme.shapes.small)
                                            )
                                        }

                                        Text(
                                            text = state.song.title,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .basicMarquee()
                                        )

                                        Text(
                                            text = state.song.artists,
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
                                    } else if (state.song.syncedLyrics != null) {
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
        LrcCorrectDialog(
            action = action,
            state = state
        )
    }
}