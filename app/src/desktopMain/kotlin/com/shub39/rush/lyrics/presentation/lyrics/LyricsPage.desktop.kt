package com.shub39.rush.lyrics.presentation.lyrics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.MeshGradient
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.Empty
import com.shub39.rush.core.presentation.fadeBottomToTop
import com.shub39.rush.core.presentation.fadeTopToBottom
import com.shub39.rush.core.presentation.generateGradientColors
import com.shub39.rush.lyrics.presentation.lyrics.component.ActionsRow
import com.shub39.rush.lyrics.presentation.lyrics.component.ErrorCard
import com.shub39.rush.lyrics.presentation.lyrics.component.LoadingCard
import com.shub39.rush.lyrics.presentation.lyrics.component.LrcCorrectDialog
import com.shub39.rush.lyrics.presentation.lyrics.component.PlainLyrics
import com.shub39.rush.lyrics.presentation.lyrics.component.SyncedLyrics
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Pause
import compose.icons.fontawesomeicons.solid.Play
import kotlinx.coroutines.delay

@Composable
actual fun LyricsPage(
    onEdit: () -> Unit,
    onShare: () -> Unit,
    action: (LyricsPageAction) -> Unit,
    state: LyricsPageState,
    notificationAccess: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val (cardBackground, cardContent) = getCardColors(state)
    val (hypnoticColor1, hypnoticColor2) = getHypnoticColors(state)
    val hypnoticSpeed by animateFloatAsState(targetValue = state.meshSpeed)

    LaunchedEffect(state.song) {
        delay(100)
        lazyListState.animateScrollToItem(0)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .let {
                    if (state.hypnoticCanvas) {
                        it.shaderBackground(
                            shader = MeshGradient(
                                colors = generateGradientColors(
                                    color1 = hypnoticColor1,
                                    color2 = hypnoticColor2,
                                    steps = 6
                                ).toTypedArray()
                            ),
                            speed = hypnoticSpeed,
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
                    } else {
                        it
                    }
                }
                .fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = if (state.hypnoticCanvas) Color.Transparent else cardBackground,
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

                    Empty(suggestion = false)

                } else {
                    // Updating colors
                    LaunchedEffect(state.song.artUrl) {
                        state.song.artUrl?.let {
                            action(LyricsPageAction.UpdateExtractedColors(it))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 40.dp, vertical = 120.dp)
                            .fillMaxSize(),
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
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(250.dp)
                                        .clip(MaterialTheme.shapes.small)
                                )
                            }

                            Text(
                                text = state.song.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .basicMarquee()
                            )

                            Text(
                                text = state.song.artists,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .basicMarquee()
                            )

                            ActionsRow(
                                state = state,
                                action = action,
                                notificationAccess = true,
                                cardBackground = cardBackground,
                                cardContent = cardContent,
                                onShare = onShare,
                                onEdit = onEdit,
                                modifier = Modifier.padding(vertical = 16.dp)
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
                                    .fadeBottomToTop(0.1f)
                            )
                        } else if (state.song.syncedLyrics != null) {
                            SyncedLyrics(
                                state = state,
                                coroutineScope = coroutineScope,
                                lazyListState = lazyListState,
                                cardContent = cardContent,
                                action = action,
                                textAnimatedPadding = 64.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .fadeTopToBottom()
                                    .fadeBottomToTop(0.1f)
                            )
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
                        FontAwesomeIcons.Solid.Play
                    } else {
                        FontAwesomeIcons.Solid.Pause
                    },
                    modifier = Modifier.size(20.dp),
                    contentDescription = "Pause or Resume"
                )
            }
        }
    }

    if (state.lyricsCorrect) {
        LrcCorrectDialog(
            action = action,
            state = state
        )
    }
}