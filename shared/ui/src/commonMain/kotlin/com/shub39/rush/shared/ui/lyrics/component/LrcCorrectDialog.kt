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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.shub39.rush.shared.ui.lyrics.LrcCorrect
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import com.shub39.rush.shared.ui.lyrics.LyricsPageState
import com.shub39.rush.shared.ui.lyrics.LyricsState
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

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp),
                modifier =
                    Modifier.padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            ) {
                itemsIndexed(items = state.lrcCorrect.searchResults, key = { index, _ -> index }) {
                    _,
                    result ->
                    Card(
                        onClick = {
                            (state.lyricsState as? LyricsState.Loaded)?.song?.id?.let {
                                action(LyricsPageAction.OnUpdateSongLyrics(it, result))
                            }
                            action(LyricsPageAction.OnLyricsCorrect(false))
                        },
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    painter =
                                        painterResource(
                                            if (
                                                result
                                                    is
                                                    CorrectionSearchResult.PlainLyricsSearchResult
                                            ) {
                                                Res.drawable.quote
                                            } else Res.drawable.sync
                                        ),
                                    modifier = Modifier.size(14.dp),
                                    contentDescription = null,
                                )

                                Text(
                                    text =
                                        stringResource(
                                            when (result) {
                                                is CorrectionSearchResult.LineSyncedLyricsSearchResult ->
                                                    Res.string.line_synced_lyrics

                                                is CorrectionSearchResult.PlainLyricsSearchResult ->
                                                    Res.string.plain_lyrics

                                                is CorrectionSearchResult.SyllableSyncedLyricsSearchResult ->
                                                    Res.string.syllable_synced_lyrics
                                            }
                                        ),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }

                            Text(
                                text = result.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = result.artist,
                                style = MaterialTheme.typography.bodySmall,
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
