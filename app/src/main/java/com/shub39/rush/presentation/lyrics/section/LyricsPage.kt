package com.shub39.rush.presentation.lyrics.section

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.domain.dataclasses.ExtractedColors
import com.shub39.rush.domain.dataclasses.Lyric
import com.shub39.rush.domain.dataclasses.SongUi
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.CardColors
import com.shub39.rush.domain.enums.LyricsAlignment
import com.shub39.rush.domain.enums.LyricsBackground
import com.shub39.rush.presentation.KeepScreenOn
import com.shub39.rush.presentation.audioDependentBackgrounds
import com.shub39.rush.presentation.components.ArtFromUrl
import com.shub39.rush.presentation.components.Empty
import com.shub39.rush.presentation.components.PageFill
import com.shub39.rush.presentation.components.RushTheme
import com.shub39.rush.presentation.fadeBottomToTop
import com.shub39.rush.presentation.fadeTopToBottom
import com.shub39.rush.presentation.glowBackground
import com.shub39.rush.presentation.lyrics.ApplyLyricsBackground
import com.shub39.rush.presentation.lyrics.LyricsPageAction
import com.shub39.rush.presentation.lyrics.LyricsPageState
import com.shub39.rush.presentation.lyrics.LyricsState
import com.shub39.rush.presentation.lyrics.SearchState
import com.shub39.rush.presentation.lyrics.TextPrefs
import com.shub39.rush.presentation.lyrics.calculateGlowMultiplier
import com.shub39.rush.presentation.lyrics.component.Actions
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
import kotlinx.coroutines.delay

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
                            icon = R.drawable.music
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
                        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

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
                                hypnoticColor2 = hypnoticColor2
                            )

                            Column {
                                if (!windowSizeClass.isWidthAtLeastBreakpoint(840)) {
                                    // portrait ui
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
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                            ) {
                                                Actions(
                                                    state = state,
                                                    action = action,
                                                    notificationAccess = notificationAccess,
                                                    cardBackground = cardBackground,
                                                    cardContent = cardContent,
                                                    onShare = onShare,
                                                    onEdit = onEdit,
                                                    glowMultiplier = glowMultiplier,
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
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fadeTopToBottom()
                                        )
                                    } else if (lyricsState.song.syncedLyrics != null) {
                                        SyncedLyrics(
                                            state = state,
                                            lazyListState = lazyListState,
                                            cardContent = cardContent,
                                            action = action,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fadeTopToBottom()
                                        )
                                    }
                                } else {
                                    // landscape UI

                                    Row(
                                        modifier = Modifier
                                            .padding(start = 32.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Actions Row
                                        Column {
                                            Actions(
                                                state = state,
                                                action = action,
                                                notificationAccess = notificationAccess,
                                                cardBackground = cardBackground,
                                                cardContent = cardContent,
                                                onShare = onShare,
                                                onEdit = onEdit,
                                                glowMultiplier = glowMultiplier,
                                            )
                                        }

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
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fadeTopToBottom()
                                            )
                                        } else if (lyricsState.song.syncedLyrics != null) {
                                            SyncedLyrics(
                                                state = state,
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

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = state.autoChange,
                modifier = Modifier.widthIn(max = 150.dp),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AnimatedContent(
                    targetState = state.searchState
                ) {
                    when (it) {
                        SearchState.Idle -> {}
                        is SearchState.Searching -> {
                            Card(
                                shape = RoundedCornerShape(100.dp),
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
                                shape = RoundedCornerShape(100.dp),
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
                                        painter = painterResource(R.drawable.search_off),
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
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedIconButton(
                        onClick = { action(LyricsPageAction.OnPlayPrevious) },
                        colors = IconButtonDefaults.outlinedIconButtonColors(
                            contentColor = cardBackground,
                            containerColor = cardContent
                        ),
                        shapes = IconButtonShapes(
                            shape = RoundedCornerShape(1000.dp),
                            pressedShape = RoundedCornerShape(16.dp)
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.skip_previous),
                            contentDescription = "Skip Previous"
                        )
                    }

                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = cardBackground,
                            containerColor = cardContent
                        ),
                        shapes = IconButtonShapes(
                            shape = RoundedCornerShape(1000.dp),
                            pressedShape = RoundedCornerShape(16.dp)
                        ),
                        onClick = { action(LyricsPageAction.OnPauseOrResume) },
                        modifier = Modifier
                            .run {
                                if (state.lyricsBackground in audioDependentBackgrounds) {
                                    glowBackground(
                                        (24 * glowMultiplier).dp,
                                        CircleShape,
                                        cardContent
                                    )
                                } else {
                                    this
                                }
                            }
                            .height(60.dp)
                            .width(120.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                if (state.playingSong.speed == 0f) {
                                    R.drawable.play
                                } else {
                                    R.drawable.pause
                                }
                            ),
                            contentDescription = "Pause or Resume"
                        )
                    }

                    OutlinedIconButton(
                        onClick = { action(LyricsPageAction.OnPlayNext) },
                        shapes = IconButtonShapes(
                            shape = RoundedCornerShape(1000.dp),
                            pressedShape = RoundedCornerShape(16.dp)
                        ),
                        colors = IconButtonDefaults.outlinedIconButtonColors(
                            contentColor = cardBackground,
                            containerColor = cardContent
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.skip_next),
                            contentDescription = "Skip Next"
                        )
                    }
                }
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

@Preview(
    device = "spec:width=411dp,height=891dp",
    showSystemUi = false, showBackground = false, fontScale = 1.0f
)
@Composable
fun LyricsPagePreview() {
    RushTheme(theme = Theme()) {
        LyricsPage(
            onEdit = {},
            onShare = {},
            action = {},
            state = LyricsPageState(
                syncedAvailable = true,
                sync = true,
                autoChange = true,
                searchState = SearchState.Searching("What the fuck"),
                extractedColors = ExtractedColors(
                    cardContentMuted = Color.White.toArgb(),
                    cardContentDominant = Color.White.toArgb(),
                    cardBackgroundMuted = Color.Cyan.toArgb(),
                    cardBackgroundDominant = Color.Blue.toArgb()
                ),
                cardColors = CardColors.VIBRANT,
                textPrefs = TextPrefs(
                    lyricsAlignment = LyricsAlignment.START
                ),
                lyricsBackground = LyricsBackground.HYPNOTIC,
                lyricsState = LyricsState.Loaded(
                    SongUi(
                        id = 0L,
                        title = "Sample Title",
                        artists = "Sample Artist",
                        album = "Sample Album",
                        sourceUrl = "",
                        artUrl = "",
                        lyrics = (0..100).associateWith {
                            "Line No : $it"
                        }.entries.toList(),
                        syncedLyrics = (0..100).map {
                            Lyric(it.toLong(), "Line No : $it")
                        },
                        geniusLyrics = null
                    )
                )
            ),
            waveData = null,
            notificationAccess = true
        )
    }
}
