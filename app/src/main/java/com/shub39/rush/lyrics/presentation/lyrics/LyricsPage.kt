package com.shub39.rush.lyrics.presentation.lyrics

import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.lighten
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.MeshGradient
import com.shub39.rush.R
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.findActivity
import com.shub39.rush.core.presentation.generateGradientColors
import com.shub39.rush.lyrics.data.listener.MediaListener
import com.shub39.rush.lyrics.presentation.lyrics.component.ActionsRow
import com.shub39.rush.lyrics.presentation.lyrics.component.ArtHeader
import com.shub39.rush.lyrics.presentation.lyrics.component.Empty
import com.shub39.rush.lyrics.presentation.lyrics.component.ErrorCard
import com.shub39.rush.lyrics.presentation.lyrics.component.LoadingCard
import com.shub39.rush.lyrics.presentation.lyrics.component.LrcCorrectDialog
import com.shub39.rush.lyrics.presentation.lyrics.component.PlainLyrics
import com.shub39.rush.lyrics.presentation.lyrics.component.SyncedLyrics
import kotlinx.coroutines.delay

@Composable
fun LyricsPage(
    onShare: () -> Unit,
    action: (LyricsPageAction) -> Unit,
    state: LyricsPageState,
    notificationAccess: Boolean
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    // keeping the screen on
    DisposableEffect(Unit) {
        (context as? ComponentActivity)?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            (context as? ComponentActivity)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // going fullscreen
    DisposableEffect(Unit) {
        val window = context.findActivity()?.window ?: return@DisposableEffect onDispose {}
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        insetsController.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }

        onDispose {
            insetsController.apply {
                show(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }
    }

    val top by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }

    val (cardBackground, cardContent) = getCardColors(state)

    val (hypnoticColor1, hypnoticColor2) = getHypnoticColors(state)

    val hypnoticSpeed by animateFloatAsState(
        targetValue = state.meshSpeed
    )

    LaunchedEffect(state.song) {
        delay(100)
        lazyListState.animateScrollToItem(0)
    }

    // Content Start
    Box {
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

                    Empty(suggestion = false)

                } else {

                    // Updating colors (requires context)
                    LaunchedEffect(state.song) {
                        action(
                            LyricsPageAction.UpdateExtractedColors(context)
                        )
                    }

                    Box {
                        ArtHeader(top, state, cardContent, cardBackground)

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
                                modifier = Modifier.padding(
                                    top = 16.dp,
                                    start = 16.dp,
                                    end = 16.dp
                                ),
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
                            ActionsRow(
                                state,
                                context,
                                action,
                                notificationAccess,
                                cardBackground,
                                cardContent,
                                onShare
                            )
                        }
                    }

                    // Plain lyrics
                    if (!state.sync) {
                        PlainLyrics(
                            lazyListState,
                            state,
                            cardContent,
                            action,
                            coroutineScope,
                            context
                        )
                    } else if (state.song.syncedLyrics != null) {
                        SyncedLyrics(state, coroutineScope, lazyListState, cardContent)
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
                onClick = { MediaListener.pauseOrResume(state.playingSong.speed == 0f) },
            ) {
                Icon(
                    painter = if (state.playingSong.speed == 0f) {
                        painterResource(R.drawable.round_play_arrow_24)
                    } else {
                        painterResource(R.drawable.round_pause_24)
                    },
                    contentDescription = "Pause or Resume"
                )
            }
        }
    }

    // Lyrics Correction from LRCLIB
    if (state.lyricsCorrect) {
        LrcCorrectDialog(action, state)
    }
}

@Composable
private fun getHypnoticColors(state: LyricsPageState): Pair<Color, Color> {
    val hypnoticColor1 by animateColorAsState(
        targetValue = if (state.useExtractedColors) {
            when (state.cardColors) {
                CardColors.MUTED -> state.extractedColors.cardBackgroundMuted.lighten(2f)
                else -> state.extractedColors.cardBackgroundDominant.lighten(2f)
            }
        } else {
            Color(state.mCardBackground).lighten(2f)
        },
        label = "hypnotic color 1"
    )
    val hypnoticColor2 by animateColorAsState(
        targetValue = if (state.useExtractedColors) {
            when (state.cardColors) {
                CardColors.MUTED -> state.extractedColors.cardBackgroundMuted.darken(2f)
                else -> state.extractedColors.cardBackgroundDominant.darken(2f)
            }
        } else {
            Color(state.mCardBackground).darken(2f)
        },
        label = "hypnotic color 2"
    )
    return Pair(hypnoticColor1, hypnoticColor2)
}

@Composable
private fun getCardColors(
    state: LyricsPageState
): Pair<Color, Color> {
    val cardBackground by animateColorAsState(
        targetValue = if (state.useExtractedColors) {
            when (state.cardColors) {
                CardColors.MUTED -> state.extractedColors.cardBackgroundMuted
                else -> state.extractedColors.cardBackgroundDominant
            }
        } else {
            Color(state.mCardBackground)
        },
        label = "cardBackground"
    )
    val cardContent by animateColorAsState(
        targetValue = if (state.useExtractedColors) {
            when (state.cardColors) {
                CardColors.MUTED -> state.extractedColors.cardContentMuted
                else -> state.extractedColors.cardContentDominant
            }
        } else {
            Color(state.mCardContent)
        },
        label = "cardContent"
    )
    return Pair(cardBackground, cardContent)
}