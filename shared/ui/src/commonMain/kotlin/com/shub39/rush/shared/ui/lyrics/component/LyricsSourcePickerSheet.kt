/*
 * Copyright (C) 2026  Karelisio
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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.core.enums.LyricsProviderId
import com.shub39.rush.shared.core.enums.LyricsQuality
import com.shub39.rush.shared.core.enums.Sources
import com.shub39.rush.shared.ui.component.RushBottomSheet
import com.shub39.rush.shared.ui.listItemColors
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import com.shub39.rush.shared.ui.lyrics.LyricsPageState
import com.shub39.rush.shared.ui.lyrics.SourcePicker
import com.shub39.rush.shared.ui.lyrics.SourceResult
import com.shub39.rush.shared.ui.segmentedListItemShapes
import com.shub39.rush.shared.ui.theme.flexFontRounded
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

/** Lets the user compare what every enabled source returns and switch the lyrics in one tap. */
@Composable
fun LyricsSourcePickerSheet(
    picker: SourcePicker,
    currentSource: LyricsProviderId?,
    action: (LyricsPageAction) -> Unit,
    onManualSearch: () -> Unit,
    onManageSources: () -> Unit,
) {
    RushBottomSheet(
        onDismissRequest = { action(LyricsPageAction.OnShowSourcePicker(false)) },
        padding = 16.dp,
    ) {
        Text(
            text = stringResource(Res.string.change_source),
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = flexFontRounded()),
        )

        if (picker.providers.isEmpty()) {
            Text(
                text = stringResource(Res.string.no_enabled_source),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            picker.providers.forEachIndexed { index, provider ->
                val result = picker.results[provider] ?: SourceResult.Loading

                ListItem(
                    selected = provider == currentSource,
                    onClick = {
                        when (result) {
                            is SourceResult.Found ->
                                picker.songId?.let {
                                    action(
                                        LyricsPageAction.OnUpdateSongLyrics(it, result.candidate)
                                    )
                                }
                            is SourceResult.Failed ->
                                action(LyricsPageAction.OnRetrySource(provider))
                            SourceResult.Loading -> {}
                        }
                    },
                    shapes = segmentedListItemShapes(index, picker.providers.size),
                    colors = listItemColors(),
                    overlineContent =
                        if (provider == currentSource) {
                            { Text(text = stringResource(Res.string.current_source)) }
                        } else null,
                    supportingContent = {
                        when (result) {
                            is SourceResult.Found ->
                                Text(
                                    text = "${result.candidate.title} · ${result.candidate.artist}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            is SourceResult.Failed -> Text(text = stringResource(result.error))
                            SourceResult.Loading -> {}
                        }
                    },
                    trailingContent = {
                        when (result) {
                            is SourceResult.Found ->
                                SuggestionChip(
                                    onClick = {},
                                    enabled = false,
                                    label = {
                                        Text(
                                            text = stringResource(result.candidate.quality.label())
                                        )
                                    },
                                )
                            is SourceResult.Failed ->
                                Icon(
                                    painter = painterResource(Res.drawable.refresh),
                                    contentDescription = stringResource(Res.string.retry),
                                )
                            SourceResult.Loading ->
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        }
                    },
                    content = { Text(text = provider.displayName) },
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onManualSearch) {
                Text(text = stringResource(Res.string.manual_search))
            }
            TextButton(onClick = onManageSources) {
                Text(text = stringResource(Res.string.manage_sources))
            }
        }
    }
}

/** "Lyrics: NetEase" under the song title, opens the source picker. */
@Composable
fun LyricsSourceLabel(
    state: LyricsPageState,
    song: SongUi,
    action: (LyricsPageAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val source = song.lyricsSource ?: return
    if (state.source != Sources.LRCLIB) return

    Text(
        text = stringResource(Res.string.lyrics_from, source.displayName),
        style = MaterialTheme.typography.labelMedium,
        maxLines = 1,
        modifier =
            modifier
                .clip(MaterialTheme.shapes.small)
                .clickable { action(LyricsPageAction.OnShowSourcePicker(true)) }
                .alpha(0.75f)
                .padding(horizontal = 4.dp, vertical = 2.dp),
    )
}

fun LyricsQuality.label(): StringResource =
    when (this) {
        LyricsQuality.SYLLABLE -> Res.string.syllable_synced_lyrics
        LyricsQuality.LINE -> Res.string.line_synced_lyrics
        LyricsQuality.PLAIN,
        LyricsQuality.NONE -> Res.string.plain_text
    }
