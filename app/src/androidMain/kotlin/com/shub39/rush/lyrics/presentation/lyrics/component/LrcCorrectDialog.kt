package com.shub39.rush.lyrics.presentation.lyrics.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.core.presentation.RushDialog
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.SyncAlt
import org.jetbrains.compose.resources.stringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.artist
import rush.app.generated.resources.correct_lyrics
import rush.app.generated.resources.track

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LrcCorrectDialog(
    action: (LyricsPageAction) -> Unit,
    state: LyricsPageState
) {
    var track by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }

    RushDialog(
        onDismissRequest = { action(LyricsPageAction.OnLyricsCorrect(false)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.correct_lyrics),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutlinedTextField(
                    value = track,
                    onValueChange = { track = it },
                    singleLine = true,
                    shape = MaterialTheme.shapes.extraLarge,
                    label = { Text(text = stringResource(Res.string.track)) }
                )

                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    singleLine = true,
                    shape = MaterialTheme.shapes.extraLarge,
                    label = { Text(text = stringResource(Res.string.artist)) }
                )
            }

            Button(
                onClick = {
                    action(LyricsPageAction.OnLrcSearch(track, artist))
                },
                enabled = track.isNotBlank() && !state.lrcCorrect.searching,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!state.lrcCorrect.searching) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    CircularProgressIndicator(
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(state.lrcCorrect.searchResults, key = { it.id }) {
                    Card(
                        onClick = {
                            action(
                                LyricsPageAction.OnUpdateSongLyrics(
                                    state.song?.id!!,
                                    it.plainLyrics!!,
                                    it.syncedLyrics
                                )
                            )

                            action(
                                LyricsPageAction.OnLyricsCorrect(false)
                            )
                        },
                        colors = when (it.syncedLyrics) {
                            null -> CardDefaults.elevatedCardColors()
                            else -> CardDefaults.elevatedCardColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        },
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(0.7f)
                            ) {
                                Text(
                                    text = it.name,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = it.artistName,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (it.syncedLyrics != null) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.SyncAlt,
                                    contentDescription = "Synced",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}