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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.interfaces.CorrectionSearchResult
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.component.RushDialog
import com.shub39.rush.shared.ui.listItemColors
import com.shub39.rush.shared.ui.lyrics.LrcCorrect
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import com.shub39.rush.shared.ui.lyrics.LyricsPageState
import com.shub39.rush.shared.ui.lyrics.LyricsState
import com.shub39.rush.shared.ui.segmentedListItemShapes
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

@Composable
fun LrcCorrectDialog(
    track: String,
    artist: String,
    action: (LyricsPageAction) -> Unit,
    state: LyricsPageState,
) {
    var track by remember { mutableStateOf(track) }
    var artist by remember { mutableStateOf(artist) }

    RushDialog(
        onDismissRequest = { action(LyricsPageAction.OnLyricsCorrect(false)) },
        modifier = Modifier.heightIn(max = 700.dp),
        padding = 0.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.correct_lyrics),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                OutlinedTextField(
                    value = track,
                    onValueChange = { track = it },
                    singleLine = true,
                    shape = MaterialTheme.shapes.extraLarge,
                    label = { Text(text = stringResource(Res.string.track)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    singleLine = true,
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(Res.string.artist)) },
                )
            }

            Button(
                onClick = { action(LyricsPageAction.OnCorrectionSearch(track, artist)) },
                enabled = track.isNotBlank() && !state.lrcCorrect.searching,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            ) {
                if (!state.lrcCorrect.searching) {
                    Icon(
                        painter = painterResource(Res.drawable.search),
                        contentDescription = "Search",
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    CircularProgressIndicator(
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            var expandedIndex: Int? by remember { mutableStateOf(null) }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp),
                modifier =
                    Modifier.padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            ) {
                itemsIndexed(items = state.lrcCorrect.searchResults, key = { index, _ -> index }) {
                    index,
                    result ->
                    ListItem(
                        selected = expandedIndex == index,
                        shapes =
                            segmentedListItemShapes(index, state.lrcCorrect.searchResults.size),
                        colors = listItemColors(),
                        onClick = { expandedIndex = if (expandedIndex == index) null else index },
                        overlineContent = {
                            Text(
                                text =
                                    stringResource(
                                        when (result) {
                                            is LineSyncedLyricsSearchResult ->
                                                Res.string.line_synced_lyrics

                                            is PlainLyricsSearchResult -> Res.string.plain_lyrics

                                            is SyllableSyncedLyricsSearchResult ->
                                                Res.string.syllable_synced_lyrics
                                        }
                                    ),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                        content = {
                            Text(
                                text = result.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        supportingContent = {
                            Column {
                                Text(
                                    text = result.artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )

                                AnimatedVisibility(visible = expandedIndex == index) {
                                    Column {
                                        Card(
                                            colors =
                                                CardDefaults.cardColors(
                                                    containerColor =
                                                        MaterialTheme.colorScheme
                                                            .surfaceContainerLow
                                                ),
                                            modifier = Modifier.padding(top = 4.dp),
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                    text =
                                                        when (result) {
                                                            is LineSyncedLyricsSearchResult ->
                                                                result.lineSyncedLyrics
                                                            is PlainLyricsSearchResult ->
                                                                result.plainLyrics
                                                            is SyllableSyncedLyricsSearchResult ->
                                                                result.syllableSyncedLyrics
                                                        },
                                                    maxLines = 20,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.padding(vertical = 2.dp))

                                        Button(
                                            onClick = {
                                                (state.lyricsState as? LyricsState.Loaded)
                                                    ?.song
                                                    ?.id
                                                    ?.let {
                                                        action(
                                                            LyricsPageAction.OnUpdateSongLyrics(
                                                                it,
                                                                result,
                                                            )
                                                        )
                                                    }
                                                action(LyricsPageAction.OnLyricsCorrect(false))
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            Text(text = stringResource(Res.string.save))
                                        }
                                    }
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}

@PreviewWrapper(RushPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    LrcCorrectDialog(
        track = "Track Name",
        artist = "Artist Name",
        action = {},
        state =
            LyricsPageState(
                lrcCorrect =
                    LrcCorrect(
                        searchResults =
                            (0..3).map {
                                CorrectionSearchResult.PlainLyricsSearchResult(
                                    plainLyrics = "TODO()",
                                    title = "Song One",
                                    artist = "GABsdjhgva",
                                )
                            } +
                                (0..3).map {
                                    CorrectionSearchResult.LineSyncedLyricsSearchResult(
                                        lineSyncedLyrics = "TODO",
                                        plainLyrics = "TODO",
                                        title = "Synced Song",
                                        artist = "Sync Joe",
                                    )
                                } +
                                (0..3).map {
                                    CorrectionSearchResult.SyllableSyncedLyricsSearchResult(
                                        syllableSyncedLyrics = "TODO()",
                                        title = "Syllable Search",
                                        artist = "ligma",
                                    )
                                }
                    )
            ),
    )
}
