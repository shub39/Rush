package com.shub39.rush.lyrics.presentation.setting

import android.content.res.Configuration
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.shub39.rush.R
import com.shub39.rush.core.presentation.theme.RushTheme
import com.shub39.rush.lyrics.presentation.setting.component.AudioFile
import com.shub39.rush.lyrics.presentation.setting.component.BetterIconButton
import com.shub39.rush.lyrics.presentation.setting.component.DownloaderCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchDownloader(
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val audioFiles = remember { mutableStateListOf<AudioFile>() }
    var isLoadingFiles by remember { mutableStateOf(true) }
    var uri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri = it }

    BackHandler(enabled = state.batchDownload.isDownloading) {}

    LaunchedEffect(uri) {
        if (uri != null) {
            val documentFile = DocumentFile.fromTreeUri(context, uri!!)

            coroutineScope.launch(Dispatchers.IO) {
                if (documentFile != null && documentFile.isDirectory) {
                    fun processFiles(directory: DocumentFile) {
                        for (file in directory.listFiles()) {
                            if (file.isDirectory) {
                                processFiles(file)
                            } else if (file.isFile && file.type?.startsWith("audio/") == true) {
                                val retriever = MediaMetadataRetriever()
                                try {
                                    retriever.setDataSource(context, file.uri)

                                    val title =
                                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                                    val artist =
                                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)

                                    if (title != null && artist != null) {
                                        audioFiles.add(AudioFile(title, artist))
                                    }

                                    retriever.release()
                                } catch (e: Exception) {
                                    Log.d("BatchDownloader", "Can't set data source $e")
                                }
                            }
                        }
                    }

                    processFiles(documentFile)
                }

                isLoadingFiles = false
            }

        }
    }

    LaunchedEffect(state.batchDownload.indexes) {
        if (state.batchDownload.indexes.size - 3 > 0) {
            listState.animateScrollToItem(state.batchDownload.indexes.size - 3)
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 500.dp)
                    .fillMaxSize()
            ) {
                TopAppBar(
                    title = { Text(stringResource(R.string.batch_download)) }
                )

                ListItem(
                    headlineContent = { Text(stringResource(R.string.select_folder)) },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BetterIconButton(
                                onClick = { launcher.launch(null) },
                                enabled = uri == null
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_drive_file_move_24),
                                    contentDescription = null
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            BetterIconButton(
                                onClick = {
                                    uri = null
                                    audioFiles.clear()
                                    action(SettingsPageAction.OnClearIndexes)
                                },
                                enabled = !state.batchDownload.isDownloading && uri != null
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_delete_forever_24),
                                    contentDescription = null
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            AnimatedVisibility(
                                visible = audioFiles.isNotEmpty()
                            ) {
                                if (!state.batchDownload.isDownloading) {
                                    BetterIconButton(
                                        onClick = {
                                            action(
                                                SettingsPageAction.OnBatchDownload(
                                                    audioFiles
                                                )
                                            )
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.round_play_arrow_24),
                                            contentDescription = null
                                        )
                                    }
                                } else {
                                    CircularProgressIndicator(
                                        strokeCap = StrokeCap.Round,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                )

                OutlinedCard(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uri == null) {
                            Text(stringResource(R.string.no_folder_selected))
                        } else if (audioFiles.isEmpty()) {
                            Text(stringResource(R.string.no_audio_files))
                        } else if (isLoadingFiles) {
                            CircularProgressIndicator(
                                strokeCap = StrokeCap.Round
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = listState,
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(audioFiles.size) { index ->
                                    DownloaderCard(
                                        title = audioFiles[index].title,
                                        artist = audioFiles[index].artist,
                                        state = state.batchDownload.indexes[index],
                                        listItemColors = stateListColors(state.batchDownload.indexes[index]),
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

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    device = "spec:width=673dp,height=841dp", showSystemUi = true, showBackground = true
)
@Composable
fun BatchDownloaderPreview() {
    RushTheme(theme = "Yellow") {
        BatchDownloader(
            state = SettingsPageState(),
            action = {},
        )
    }
}