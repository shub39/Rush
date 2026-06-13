/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.shared.ui.lyrics.section

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.dataclasses.ExtractedColors
import com.shub39.rush.shared.core.dataclasses.Lyric
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.core.enums.CardColors
import com.shub39.rush.shared.core.enums.LyricsAlignment
import com.shub39.rush.shared.core.enums.LyricsBackground
import com.shub39.rush.shared.ui.LocalWindowSizeClass
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.audioDependentBackgrounds
import com.shub39.rush.shared.ui.component.ArtFromUrl
import com.shub39.rush.shared.ui.component.Empty
import com.shub39.rush.shared.ui.component.PageFill
import com.shub39.rush.shared.ui.conditional
import com.shub39.rush.shared.ui.fadeBottomToTop
import com.shub39.rush.shared.ui.fadeTopToBottom
import com.shub39.rush.shared.ui.glowBackground
import com.shub39.rush.shared.ui.isExpanded
import com.shub39.rush.shared.ui.lyrics.ApplyLyricsBackground
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import com.shub39.rush.shared.ui.lyrics.LyricsPageState
import com.shub39.rush.shared.ui.lyrics.LyricsState
import com.shub39.rush.shared.ui.lyrics.PlaybackInfo
import com.shub39.rush.shared.ui.lyrics.SearchState
import com.shub39.rush.shared.ui.lyrics.TextPrefs
import com.shub39.rush.shared.ui.lyrics.calculateGlowMultiplier
import com.shub39.rush.shared.ui.lyrics.component.Actions
import com.shub39.rush.shared.ui.lyrics.component.ActionsState
import com.shub39.rush.shared.ui.lyrics.component.ErrorCard
import com.shub39.rush.shared.ui.lyrics.component.FetchingCard
import com.shub39.rush.shared.ui.lyrics.component.LineSyncedLyrics
import com.shub39.rush.shared.ui.lyrics.component.LrcCorrectDialog
import com.shub39.rush.shared.ui.lyrics.component.PlainLyrics
import com.shub39.rush.shared.ui.lyrics.component.SyllableSyncedLyrics
import com.shub39.rush.shared.ui.lyrics.getCardColors
import com.shub39.rush.shared.ui.lyrics.getHypnoticColors
import com.shub39.rush.shared.ui.lyrics.getWaveColors
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import com.shub39.rush.shared.ui.theme.flexFontRounded
import com.shub39.rush.shared.ui.toAlignment
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

@Composable
fun LyricsPage(
    modifier: Modifier = Modifier,
    onNavigateToCustomisations: () -> Unit,
    onShare: () -> Unit,
    action: (LyricsPageAction) -> Unit,
    state: LyricsPageState,
    playbackInfo: PlaybackInfo,
    waveData: List<Byte>?,
    notificationAccess: Boolean,
) =
    PageFill(modifier = modifier) {
        val lazyListState = rememberLazyListState()
        val windowSizeClass = LocalWindowSizeClass.current

        val (cardBackground, cardContent) = getCardColors(state)
        val (hypnoticColor1, hypnoticColor2) = getHypnoticColors(state)
        val waveColors = getWaveColors(state)

        var controlsVisible by remember { mutableStateOf(true) }
        val controlsAlpha by animateFloatAsState(targetValue = if (controlsVisible) 1f else 0f)

        val glowMultiplier by
            animateFloatAsState(
                targetValue = calculateGlowMultiplier(waveData),
                animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
            )

        val top by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }

        val songId = (state.lyricsState as? LyricsState.Loaded)?.song?.id
        val loadedSong = (state.lyricsState as? LyricsState.Loaded)?.song
        val lyricsAlignment =
            remember(state.textPrefs.lyricsAlignment) {
                state.textPrefs.lyricsAlignment.toAlignment()
            }
        val actionsState =
            remember(
                loadedSong,
                state.selectedLines,
                state.source,
                state.syncedAvailable,
                state.sync,
                state.autoChange,
            ) {
                ActionsState(
                    song = loadedSong,
                    selectedLines = state.selectedLines,
                    source = state.source,
                    syncedAvailable = state.syncedAvailable,
                    sync = state.sync,
                    autoChange = state.autoChange,
                )
            }

        LaunchedEffect(songId) {
            if (songId == null) return@LaunchedEffect
            delay(100.milliseconds)
            lazyListState.animateScrollToItem(0)
            action(LyricsPageAction.OnSetPosition(0))
        }

        LaunchedEffect(controlsVisible, state.hideUI) {
            if (controlsVisible && state.hideUI) {
                delay(5000.milliseconds)
                controlsVisible = false
            }
        }

        // Content Start
        Box(
            modifier =
                Modifier.fillMaxSize().keepScreenOn().pointerInput(Unit) {
                    detectTapGestures { controlsVisible = true }
                }
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = cardBackground,
                        contentColor = cardContent,
                    ),
                shape = RoundedCornerShape(0.dp),
            ) {
                AnimatedContent(
                    targetState = state.lyricsState,
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { lyricsState ->
                    when (lyricsState) {
                        LyricsState.Idle -> {
                            Empty(
                                suggestion = false,
                                color = cardContent,
                                icon = Res.drawable.music,
                            )
                        }

                        is LyricsState.Fetching -> {
                            FetchingCard(
                                fetching = lyricsState.name,
                                colors = Pair(cardContent, cardBackground),
                            )
                        }

                        is LyricsState.LyricsError -> {
                            ErrorCard(
                                error = lyricsState.errorCode,
                                debugMessage = lyricsState.debugMessage,
                                colors = Pair(cardContent, cardBackground),
                            )
                        }

                        is LyricsState.Loaded -> {
                            // Updating colors
                            LaunchedEffect(lyricsState.song.artUrl) {
                                lyricsState.song.artUrl?.let {
                                    action(LyricsPageAction.UpdateExtractedColors(it))
                                }
                            }

                            PageFill {
                                ApplyLyricsBackground(
                                    background = state.lyricsBackground,
                                    artUrl = lyricsState.song.artUrl,
                                    cardBackground = cardBackground,
                                    waveData = waveData,
                                    waveColors = waveColors,
                                    hypnoticColor1 = hypnoticColor1,
                                    hypnoticColor2 = hypnoticColor2,
                                )

                                Column(modifier = Modifier.widthIn(max = 1100.dp)) {
                                    if (!windowSizeClass.isExpanded()) {
                                        // portrait ui
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Column(modifier = Modifier.align(Alignment.TopCenter)) {
                                                AnimatedVisibility(
                                                    visible =
                                                        !state.sync &&
                                                            top > 2 &&
                                                            state.lyricsBackground !=
                                                                LyricsBackground.ALBUM_ART
                                                ) {
                                                    ArtFromUrl(
                                                        imageUrl = lyricsState.song.artUrl!!,
                                                        highlightColor = cardContent,
                                                        baseColor = Color.Transparent,
                                                        modifier =
                                                            Modifier.fillMaxWidth()
                                                                .height(120.dp)
                                                                .fadeBottomToTop(),
                                                    )
                                                }
                                            }

                                            Column(
                                                modifier =
                                                    Modifier.fillMaxWidth()
                                                        .padding(
                                                            start = 16.dp,
                                                            end = 16.dp,
                                                            top = 48.dp,
                                                        ),
                                                horizontalAlignment = lyricsAlignment,
                                            ) {
                                                Spacer(modifier = Modifier.height(20.dp))

                                                AnimatedVisibility(
                                                    visible = top <= 2 && !state.sync
                                                ) {
                                                    ArtFromUrl(
                                                        imageUrl = lyricsState.song.artUrl!!,
                                                        highlightColor = cardContent,
                                                        baseColor = Color.Transparent,
                                                        contentScale = ContentScale.Crop,
                                                        modifier =
                                                            Modifier.size(80.dp)
                                                                .aspectRatio(1f)
                                                                .clip(MaterialTheme.shapes.small),
                                                    )
                                                }

                                                Text(
                                                    text = lyricsState.song.title,
                                                    style = MaterialTheme.typography.headlineMedium,
                                                    fontFamily = flexFontEmphasis(),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier =
                                                        Modifier.padding(top = 8.dp).basicMarquee(),
                                                )

                                                Text(
                                                    text = lyricsState.song.artists,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontFamily = flexFontRounded(),
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.basicMarquee(),
                                                )

                                                // Actions Row
                                                Row(
                                                    modifier =
                                                        Modifier.alpha(controlsAlpha)
                                                            .padding(vertical = 8.dp)
                                                            .animateContentSize()
                                                ) {
                                                    Actions(
                                                        state = actionsState,
                                                        action = action,
                                                        notificationAccess = notificationAccess,
                                                        cardBackground = cardBackground,
                                                        cardContent = cardContent,
                                                        onShare = onShare,
                                                        onEdit = onNavigateToCustomisations,
                                                    )
                                                }
                                            }
                                        }

                                        if (!state.sync) {
                                            PlainLyrics(
                                                state = state,
                                                lazyListState = lazyListState,
                                                cardContent = cardContent,
                                                action = action,
                                                modifier = Modifier.weight(1f).fadeTopToBottom(),
                                            )
                                        } else if (lyricsState.song.ttmlLyrics != null) {
                                            SyllableSyncedLyrics(
                                                state = state,
                                                playbackInfo = playbackInfo,
                                                lazyListState = lazyListState,
                                                cardContent = cardContent,
                                                action = action,
                                                modifier = Modifier.weight(1f).fadeTopToBottom(),
                                            )
                                        } else if (lyricsState.song.syncedLyrics != null) {
                                            LineSyncedLyrics(
                                                state = state,
                                                playbackInfo = playbackInfo,
                                                lazyListState = lazyListState,
                                                cardContent = cardContent,
                                                action = action,
                                                modifier = Modifier.weight(1f).fadeTopToBottom(),
                                            )
                                        }
                                    } else {
                                        // landscape UI
                                        Row(
                                            modifier =
                                                Modifier.fillMaxSize()
                                                    .padding(start = 32.dp, end = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        ) {
                                            Column(
                                                modifier =
                                                    Modifier.alpha(controlsAlpha)
                                                        .animateContentSize()
                                            ) {
                                                Actions(
                                                    state = actionsState,
                                                    action = action,
                                                    notificationAccess = notificationAccess,
                                                    cardBackground = cardBackground,
                                                    cardContent = cardContent,
                                                    onShare = onShare,
                                                    onEdit = onNavigateToCustomisations,
                                                )
                                            }

                                            Column(
                                                modifier =
                                                    Modifier.fillMaxWidth(0.3f)
                                                        .animateContentSize(),
                                                horizontalAlignment = Alignment.Start,
                                            ) {
                                                ArtFromUrl(
                                                    imageUrl = lyricsState.song.artUrl!!,
                                                    highlightColor = cardContent,
                                                    baseColor = Color.Transparent,
                                                    contentScale = ContentScale.Fit,
                                                    modifier =
                                                        Modifier.fillMaxWidth(0.8f)
                                                            .aspectRatio(1f)
                                                            .clip(MaterialTheme.shapes.large),
                                                )

                                                Spacer(modifier = Modifier.height(12.dp))

                                                Text(
                                                    text = lyricsState.song.title,
                                                    style =
                                                        MaterialTheme.typography.titleLarge.copy(
                                                            fontFamily = flexFontEmphasis()
                                                        ),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.basicMarquee(),
                                                )

                                                Text(
                                                    text = lyricsState.song.artists,
                                                    style =
                                                        MaterialTheme.typography.bodyMedium.copy(
                                                            fontFamily = flexFontRounded()
                                                        ),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.basicMarquee(),
                                                )
                                            }

                                            if (!state.sync) {
                                                PlainLyrics(
                                                    state = state,
                                                    lazyListState = lazyListState,
                                                    cardContent = cardContent,
                                                    action = action,
                                                    modifier =
                                                        Modifier.weight(1f)
                                                            .fillMaxHeight()
                                                            .fadeTopToBottom(),
                                                )
                                            } else if (lyricsState.song.ttmlLyrics != null) {
                                                SyllableSyncedLyrics(
                                                    state = state,
                                                    playbackInfo = playbackInfo,
                                                    lazyListState = lazyListState,
                                                    cardContent = cardContent,
                                                    action = action,
                                                    modifier =
                                                        Modifier.weight(1f)
                                                            .fillMaxHeight()
                                                            .fadeTopToBottom(),
                                                )
                                            } else if (lyricsState.song.syncedLyrics != null) {
                                                LineSyncedLyrics(
                                                    state = state,
                                                    playbackInfo = playbackInfo,
                                                    lazyListState = lazyListState,
                                                    cardContent = cardContent,
                                                    action = action,
                                                    modifier =
                                                        Modifier.weight(1f)
                                                            .fillMaxHeight()
                                                            .fadeTopToBottom(),
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

            Column(
                modifier =
                    Modifier.alpha(controlsAlpha)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val autoChangeAlpha by
                    animateFloatAsState(
                        targetValue = if (state.autoChange) 1f else 0f,
                        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                        label = "autoChangeAlpha",
                    )

                if (autoChangeAlpha > 0f) {
                    Box(
                        modifier =
                            Modifier.widthIn(max = 150.dp).graphicsLayer { alpha = autoChangeAlpha }
                    ) {
                        AnimatedContent(
                            targetState = state.searchState,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "searchState",
                        ) { searchState ->
                            when (searchState) {
                                SearchState.Idle -> {}
                                is SearchState.Searching -> {
                                    LaunchedEffect(Unit) { controlsVisible = true }

                                    Card(
                                        shape = RoundedCornerShape(100.dp),
                                        colors =
                                            CardDefaults.cardColors(
                                                contentColor = cardBackground,
                                                containerColor = cardContent,
                                            ),
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            LoadingIndicator(
                                                color = cardBackground,
                                                modifier = Modifier.size(20.dp),
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = searchState.query,
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    }
                                }

                                SearchState.UserPrompt -> {
                                    LaunchedEffect(Unit) { controlsVisible = true }

                                    Card(
                                        shape = RoundedCornerShape(100.dp),
                                        colors =
                                            CardDefaults.cardColors(
                                                contentColor = cardBackground,
                                                containerColor = cardContent,
                                            ),
                                        onClick = { action(LyricsPageAction.OnToggleSearchSheet) },
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Icon(
                                                painter = painterResource(Res.drawable.search_off),
                                                contentDescription = "No exact match found",
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = stringResource(Res.string.not_found),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                                Text(
                                                    text = stringResource(Res.string.open_search),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
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
                    enter = fadeIn(MaterialTheme.motionScheme.fastSpatialSpec()),
                    exit = fadeOut(MaterialTheme.motionScheme.fastSpatialSpec()),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = { action(LyricsPageAction.OnPlayPrevious) },
                            colors =
                                IconButtonDefaults.outlinedIconButtonColors(
                                    contentColor = cardBackground,
                                    containerColor = cardContent,
                                ),
                            shapes =
                                IconButtonShapes(
                                    shape = CircleShape,
                                    pressedShape = RoundedCornerShape(16.dp),
                                ),
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.skip_previous),
                                contentDescription = "Skip Previous",
                            )
                        }

                        IconButton(
                            colors =
                                IconButtonDefaults.iconButtonColors(
                                    contentColor = cardBackground,
                                    containerColor = cardContent,
                                ),
                            shapes =
                                IconButtonShapes(
                                    shape = CircleShape,
                                    pressedShape = RoundedCornerShape(16.dp),
                                ),
                            onClick = { action(LyricsPageAction.OnPauseOrResume) },
                            modifier =
                                Modifier.conditional(
                                        state.lyricsBackground in audioDependentBackgrounds
                                    ) {
                                        glowBackground(
                                            (24 * glowMultiplier).dp,
                                            CircleShape,
                                            cardContent,
                                        )
                                    }
                                    .height(60.dp)
                                    .width(120.dp),
                        ) {
                            Icon(
                                painter =
                                    painterResource(
                                        if (playbackInfo.speed == 0f) {
                                            Res.drawable.play
                                        } else {
                                            Res.drawable.pause
                                        }
                                    ),
                                contentDescription = "Pause or Resume",
                            )
                        }

                        IconButton(
                            onClick = { action(LyricsPageAction.OnPlayNext) },
                            shapes =
                                IconButtonShapes(
                                    shape = CircleShape,
                                    pressedShape = RoundedCornerShape(16.dp),
                                ),
                            colors =
                                IconButtonDefaults.outlinedIconButtonColors(
                                    contentColor = cardBackground,
                                    containerColor = cardContent,
                                ),
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.skip_next),
                                contentDescription = "Skip Next",
                            )
                        }
                    }
                }
            }
        }

        // Lyrics Correction from LRCLIB
        if (state.lyricsCorrect) {
            LrcCorrectDialog(
                action = action,
                state = state,
                track = (state.lyricsState as? LyricsState.Loaded)?.song?.title ?: "",
                artist = (state.lyricsState as? LyricsState.Loaded)?.song?.artists ?: "",
            )
        }
    }

@PreviewWrapper(RushPreviewWrapper::class)
@PreviewScreenSizes
@Preview(
    device = "spec:width=411dp,height=891dp",
    showSystemUi = false,
    showBackground = false,
    fontScale = 1.0f,
)
@Composable
private fun LyricsPagePreview() {
    LyricsPage(
        onNavigateToCustomisations = {},
        onShare = {},
        action = {},
        state =
            LyricsPageState(
                syncedAvailable = true,
                sync = true,
                autoChange = true,
                searchState = SearchState.UserPrompt,
                extractedColors =
                    ExtractedColors(
                        cardContentMuted = Color.White.toArgb(),
                        cardContentDominant = Color.White.toArgb(),
                        cardBackgroundMuted = Color.Cyan.toArgb(),
                        cardBackgroundDominant = Color.Blue.toArgb(),
                    ),
                cardColors = CardColors.VIBRANT,
                textPrefs = TextPrefs(lyricsAlignment = LyricsAlignment.START),
                lyricsBackground = LyricsBackground.HYPNOTIC,
                lyricsState =
                    LyricsState.Loaded(
                        SongUi(
                            id = 0L,
                            title = "Sample Title",
                            artists = "Sample Artist",
                            album = "Sample Album",
                            sourceUrl = "",
                            artUrl = "",
                            lyrics = (0..100).associateWith { "Line No : $it" }.entries.toList(),
                            syncedLyrics = (0..100).map { Lyric(it.toLong(), "Line No : $it") },
                            geniusLyrics = null,
                            ttmlLyrics = null,
                        )
                    ),
            ),
        waveData = null,
        notificationAccess = true,
        playbackInfo = PlaybackInfo(),
    )
}
