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
package com.shub39.rush.shared.ui.lyrics.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.shared.core.dataclasses.LyricsPrefs
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.core.enums.Sources
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.copyToClipboard
import com.shub39.rush.shared.ui.lyrics.FIT_MIN_FONT_SIZE
import com.shub39.rush.shared.ui.lyrics.FitEdgePadding
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import com.shub39.rush.shared.ui.lyrics.LyricsPageState
import com.shub39.rush.shared.ui.lyrics.LyricsState
import com.shub39.rush.shared.ui.lyrics.TextPrefs
import com.shub39.rush.shared.ui.lyrics.fitBottomPadding
import com.shub39.rush.shared.ui.lyrics.fitLineHeight
import com.shub39.rush.shared.ui.lyrics.fitViewport
import com.shub39.rush.shared.ui.lyrics.largestFittingFontSize
import com.shub39.rush.shared.ui.lyrics.linesFitHeight
import com.shub39.rush.shared.ui.lyrics.scaledToFontSize
import com.shub39.rush.shared.ui.lyrics.updateSelectedLines
import com.shub39.rush.shared.ui.toAlignment
import com.shub39.rush.shared.ui.toArrangement
import com.shub39.rush.shared.ui.toTextAlignment
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

@Composable
fun PlainLyrics(
    state: LyricsPageState,
    lazyListState: LazyListState,
    cardContent: Color,
    action: (LyricsPageAction) -> Unit,
    onOpenSearchSheet: () -> Unit,
    modifier: Modifier = Modifier,
    fitInsets: WindowInsets = WindowInsets(0),
    fitOverlayClearance: Dp = 0.dp,
) {
    val song = (state.lyricsState as? LyricsState.Loaded)?.song ?: return
    val items = if (state.source == Sources.LRCLIB) song.lyrics else song.geniusLyrics
    val nonEmptyItems = remember(items) { items?.filter { it.value.isNotBlank() } ?: emptyList() }
    val shouldAutoFetch =
        state.source == Sources.GENIUS &&
            nonEmptyItems.isEmpty() &&
            !state.fetchingGenius.first &&
            state.fetchingGenius.second == null

    LaunchedEffect(song.id, state.source, shouldAutoFetch) {
        if (shouldAutoFetch) {
            action(LyricsPageAction.OnFetchGeniusLyrics(song.id, song.sourceUrl))
        }
    }

    val romanizedLyrics =
        when {
            !state.romanizationEnabled -> emptyMap()
            state.source == Sources.GENIUS -> song.romanizedGeniusLyrics
            else -> song.romanizedLyrics
        }
    val fitting = state.fitToScreen && nonEmptyItems.isNotEmpty()

    BoxWithConstraints(modifier = modifier) {
        val fitPadding = fitContentPadding(fitInsets, fitOverlayClearance)
        val textPrefs =
            if (fitting) {
                rememberFittedTextPrefs(
                    items = nonEmptyItems,
                    romanizedLyrics = romanizedLyrics,
                    textPrefs = state.textPrefs,
                    contentPadding = fitPadding,
                    maxWidth = constraints.maxWidth,
                    maxHeight = constraints.maxHeight,
                )
            } else {
                state.textPrefs
            }

        // The 48dp touch target of every line would decide how many lines fit, not the font size
        CompositionLocalProvider(
            LocalMinimumInteractiveComponentSize provides
                if (fitting) 0.dp else LocalMinimumInteractiveComponentSize.current
        ) {
            PlainLyricsList(
                state = state,
                song = song,
                items = nonEmptyItems,
                romanizedLyrics = romanizedLyrics,
                textPrefs = textPrefs,
                lineGap = with(LocalDensity.current) { lineGap(textPrefs, fitting) },
                lyricPadding = if (fitting) FitLyricPadding else LyricPadding,
                contentPadding =
                    if (fitting) fitPadding
                    else PaddingValues(horizontal = 16.dp, vertical = 64.dp),
                // There is no room for the actions when the lyrics take up the whole page
                showBottomActions = !fitting,
                lazyListState = lazyListState,
                cardContent = cardContent,
                action = action,
                onOpenSearchSheet = onOpenSearchSheet,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun PlainLyricsList(
    state: LyricsPageState,
    song: SongUi,
    items: List<Map.Entry<Int, String>>,
    romanizedLyrics: Map<Int, String>,
    textPrefs: TextPrefs,
    lineGap: Dp,
    lyricPadding: PaddingValues,
    contentPadding: PaddingValues,
    showBottomActions: Boolean,
    lazyListState: LazyListState,
    cardContent: Color,
    action: (LyricsPageAction) -> Unit,
    onOpenSearchSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(lineGap),
        state = lazyListState,
    ) {
        // plain lyrics with logic
        if (items.isNotEmpty()) {
            items(items = items, key = { it.key }, contentType = { "lyric" }) {
                val isSelected = state.selectedLines.contains(it.key)
                val containerColor by
                    animateColorAsState(
                        targetValue =
                            when (!isSelected) {
                                true -> Color.Transparent
                                else -> cardContent.copy(alpha = 0.3f)
                            },
                        label = "container",
                        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                    )

                PlainLyric(
                    entry = it.toPair(),
                    romanizedText = romanizedLyrics[it.key],
                    containerColor = containerColor,
                    textPrefs = textPrefs,
                    contentPadding = lyricPadding,
                    cardContent = cardContent,
                    onClick = {
                        action(
                            LyricsPageAction.OnChangeSelectedLines(
                                updateSelectedLines(
                                    state.selectedLines,
                                    it.key,
                                    it.value,
                                    state.maxLines,
                                )
                            )
                        )

                        if (!isSelected) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    },
                )
            }
        } else {
            when (state.source) {
                Sources.GENIUS -> {
                    item(key = "genius_empty", contentType = "empty_state") {
                        AnimatedContent(targetState = state.fetchingGenius) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                if (it.first) {
                                    CircularProgressIndicator(
                                        color = cardContent,
                                        modifier = Modifier.padding(16.dp),
                                    )

                                    Text(stringResource(Res.string.loading_genius))
                                } else if (it.second != null) {
                                    Icon(
                                        painter = painterResource(Res.drawable.warning),
                                        contentDescription = null,
                                        modifier = Modifier.size(100.dp),
                                    )

                                    Text(stringResource(it.second!!.errorCode))

                                    TextButton(
                                        onClick = {
                                            action(
                                                LyricsPageAction.OnFetchGeniusLyrics(
                                                    song.id,
                                                    song.sourceUrl,
                                                )
                                            )
                                        },
                                        colors =
                                            ButtonDefaults.buttonColors(
                                                contentColor = cardContent,
                                                containerColor = Color.Transparent,
                                            ),
                                        modifier = Modifier.padding(vertical = 4.dp),
                                    ) {
                                        Text(stringResource(Res.string.retry))
                                    }

                                    it.second?.debugMessage?.let { debug ->
                                        TextButton(
                                            onClick = {
                                                scope.launch { clipboard.copyToClipboard(debug) }
                                            },
                                            colors =
                                                ButtonDefaults.buttonColors(
                                                    contentColor = cardContent,
                                                    containerColor = Color.Transparent,
                                                ),
                                            modifier = Modifier.padding(vertical = 4.dp),
                                        ) {
                                            Text(stringResource(Res.string.copy_error))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Sources.LRCLIB -> {
                    item(key = "lrclib_empty", contentType = "empty_state") {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Spacer(modifier = Modifier.padding(10.dp))

                            Icon(
                                painter = painterResource(Res.drawable.warning),
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                            )

                            Spacer(modifier = Modifier.padding(10.dp))

                            Text(text = stringResource(Res.string.no_lyrics))
                        }
                    }
                }
            }
        }

        // Bottom Actions Row
        if (showBottomActions)
            item(key = "bottom_actions", contentType = "actions") {
                Row(
                    modifier = Modifier.padding(vertical = 100.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    IconButton(
                        onClick = { scope.launch { lazyListState.scrollToItem(0) } },
                        enabled =
                            if (state.source == Sources.LRCLIB) song.lyrics.isNotEmpty()
                            else !song.geniusLyrics.isNullOrEmpty(),
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_warm_up),
                            contentDescription = "Move To Top",
                        )
                    }

                    Button(
                        onClick = { uriHandler.openUri(song.sourceUrl) },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = cardContent.copy(alpha = 0.3f),
                                contentColor = cardContent,
                            ),
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.genius),
                            contentDescription = "Genius Source",
                        )
                    }

                    IconButton(onClick = onOpenSearchSheet) {
                        Icon(
                            painter = painterResource(Res.drawable.search),
                            contentDescription = "Search",
                        )
                    }
                }
            }
    }
}

@Composable
fun PlainLyric(
    textPrefs: TextPrefs,
    entry: Pair<Int, String>,
    romanizedText: String?,
    onClick: () -> Unit,
    containerColor: Color,
    cardContent: Color,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = LyricPadding,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = textPrefs.lyricsAlignment.toArrangement(),
    ) {
        Card(
            onClick = onClick,
            shape = MaterialTheme.shapes.small,
            colors =
                CardDefaults.cardColors(containerColor = containerColor, contentColor = cardContent),
        ) {
            Column(
                horizontalAlignment = textPrefs.lyricsAlignment.toAlignment(),
                modifier = Modifier.padding(contentPadding),
            ) {
                Text(
                    text = entry.second,
                    style = LocalTextStyle.current.merge(textPrefs.lyricStyle()),
                )
                if (!romanizedText.isNullOrBlank()) {
                    Text(
                        text = romanizedText,
                        style = LocalTextStyle.current.merge(textPrefs.romanizedStyle()),
                        color = cardContent.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = RomanizedTopPadding),
                    )
                }
            }
        }
    }
}

private val LyricPadding = PaddingValues(6.dp)

/** Spacing is what limits how many lines fit, so it gives way to the text when fitting. */
private val FitLyricPadding = PaddingValues(horizontal = 6.dp, vertical = 1.dp)

private val RomanizedTopPadding = 4.dp

private fun TextPrefs.lyricStyle() =
    TextStyle(
        fontSize = fontSize.sp,
        letterSpacing = letterSpacing.sp,
        lineHeight = lineHeight.sp,
        textAlign = lyricsAlignment.toTextAlignment(),
        fontWeight = FontWeight.Bold,
    )

private fun TextPrefs.romanizedStyle() =
    TextStyle(
        fontSize = (fontSize * 0.75f).sp,
        letterSpacing = (letterSpacing * 0.75f).sp,
        lineHeight = (lineHeight * 0.75f).sp,
        textAlign = lyricsAlignment.toTextAlignment(),
        fontWeight = FontWeight.Normal,
    )

private fun Density.lineGap(textPrefs: TextPrefs, fitting: Boolean): Dp =
    textPrefs.lineHeight.sp.toDp() / if (fitting) 8 else 2

/**
 * Keeps the fitted lyrics clear of the system bars and of what is drawn over the bottom of the page
 * in [overlayClearance], as they can reach the edges of the page and might have no scroll range to
 * move out from under them.
 */
@Composable
private fun fitContentPadding(insets: WindowInsets, overlayClearance: Dp): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    val padding = insets.asPaddingValues()

    return PaddingValues(
        start = padding.calculateStartPadding(layoutDirection) + 16.dp,
        top = padding.calculateTopPadding() + FitEdgePadding,
        end = padding.calculateEndPadding(layoutDirection) + 16.dp,
        bottom = fitBottomPadding(padding.calculateBottomPadding(), overlayClearance),
    )
}

/**
 * [textPrefs] at the largest font size that shows all of [items] in a [maxWidth] x [maxHeight] page
 * without scrolling, or at [FIT_MIN_FONT_SIZE] if they do not fit even at that size.
 *
 * The result only depends on the keys of the [remember], so it is recomputed when the lyrics or the
 * available space change and not when anything else on the page does.
 */
@Composable
private fun rememberFittedTextPrefs(
    items: List<Map.Entry<Int, String>>,
    romanizedLyrics: Map<Int, String>,
    textPrefs: TextPrefs,
    contentPadding: PaddingValues,
    maxWidth: Int,
    maxHeight: Int,
): TextPrefs {
    // Nearly every measurement is at a different size, a cache would only hold on to memory
    val textMeasurer = rememberTextMeasurer(cacheSize = 0)
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val textStyle = LocalTextStyle.current

    return remember(
        items,
        romanizedLyrics,
        textPrefs,
        contentPadding,
        maxWidth,
        maxHeight,
        textMeasurer,
        density,
        layoutDirection,
        textStyle,
    ) {
        with(density) {
            val viewport =
                fitViewport(
                    pageWidth = maxWidth,
                    pageHeight = maxHeight,
                    startPadding =
                        contentPadding.calculateStartPadding(layoutDirection).roundToPx(),
                    endPadding = contentPadding.calculateEndPadding(layoutDirection).roundToPx(),
                    topPadding = contentPadding.calculateTopPadding().roundToPx(),
                    bottomPadding = contentPadding.calculateBottomPadding().roundToPx(),
                    linePadding =
                        2 * FitLyricPadding.calculateStartPadding(layoutDirection).roundToPx(),
                ) ?: return@remember textPrefs.scaledToFontSize(FIT_MIN_FONT_SIZE)

            val constraints = Constraints(maxWidth = viewport.textWidth)
            val verticalPadding = 2 * FitLyricPadding.calculateTopPadding().roundToPx()
            val romanizedGap = RomanizedTopPadding.roundToPx()

            // Same wrapping as the Text in PlainLyric, so a word wider than a line takes up lines
            // here as it does there. Whether that fits is only up to the height.
            fun measuredHeight(text: String, style: TextStyle): Int =
                textMeasurer
                    .measure(text = text, style = style, constraints = constraints)
                    .size
                    .height

            val fontSize = largestFittingFontSize { size ->
                val scaled = textPrefs.scaledToFontSize(size)
                val lyricStyle = textStyle.merge(scaled.lyricStyle())
                val romanizedStyle = textStyle.merge(scaled.romanizedStyle())

                linesFitHeight(
                    lines = items,
                    gap = lineGap(scaled, fitting = true).roundToPx(),
                    maxHeight = viewport.listHeight,
                ) { item ->
                    fitLineHeight(
                        lyricHeight = measuredHeight(item.value, lyricStyle),
                        romanizedHeight =
                            romanizedLyrics[item.key]
                                ?.takeIf { it.isNotBlank() }
                                ?.let { measuredHeight(it, romanizedStyle) },
                        verticalPadding = verticalPadding,
                        romanizedGap = romanizedGap,
                    )
                }
            }

            textPrefs.scaledToFontSize(fontSize)
        }
    }
}

@PreviewWrapper(RushPreviewWrapper::class)
@Preview(device = "spec:width=411dp,height=600dp")
@Composable
private fun FitToScreenPreview() {
    PlainLyrics(
        state =
            LyricsPageState(
                prefs = LyricsPrefs(fitToScreen = true),
                lyricsState =
                    LyricsState.Loaded(
                        SongUi(
                            id = 0L,
                            title = "Sample Title",
                            artists = "Sample Artist",
                            album = "Sample Album",
                            sourceUrl = "",
                            artUrl = "",
                            lyrics = (0..12).associateWith { "Line No : $it" }.entries.toList(),
                            syncedLyrics = null,
                            geniusLyrics = null,
                            ttmlLyrics = null,
                        )
                    ),
            ),
        lazyListState = rememberLazyListState(),
        cardContent = Color.White,
        action = {},
        onOpenSearchSheet = {},
        modifier = Modifier.fillMaxSize(),
    )
}
