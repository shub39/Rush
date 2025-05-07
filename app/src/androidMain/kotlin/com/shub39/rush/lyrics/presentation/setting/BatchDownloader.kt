package com.shub39.rush.lyrics.presentation.setting

import android.net.Uri
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
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.lyrics.presentation.setting.component.DownloaderCard
import org.jetbrains.compose.resources.stringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.batch_download
import rush.app.generated.resources.no_audio_files
import rush.app.generated.resources.no_folder_selected
import rush.app.generated.resources.select_folder

// batch downloader page
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchDownloader(
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit,
) = PageFill {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var uri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri = it }

    BackHandler(enabled = state.batchDownload.isDownloading) {}

    LaunchedEffect(Unit) {
        action(SettingsPageAction.OnClearIndexes)
    }

    LaunchedEffect(uri) {
        uri?.let {
            action(SettingsPageAction.OnProcessAudioFiles(context, uri!!))
        }
    }

    LaunchedEffect(state.batchDownload.indexes) {
        if (state.batchDownload.indexes.size - 3 > 0) {
            listState.animateScrollToItem(state.batchDownload.indexes.size - 3)
        }
    }

    Scaffold(
        modifier = Modifier.widthIn(max = 500.dp),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.batch_download)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            ListItem(
                headlineContent = { Text(stringResource(Res.string.select_folder)) },
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledTonalIconButton(
                            onClick = { launcher.launch(null) },
                            enabled = uri == null
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_drive_file_move_24),
                                contentDescription = null
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        FilledTonalIconButton(
                            onClick = {
                                uri = null
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
                            visible = state.batchDownload.audioFiles.isNotEmpty()
                        ) {
                            if (!state.batchDownload.isDownloading) {
                                FilledTonalIconButton(
                                    onClick = {
                                        action(SettingsPageAction.OnBatchDownload)
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
                    .weight(1f),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (uri == null) {
                        Text(stringResource(Res.string.no_folder_selected))
                    } else if (state.batchDownload.audioFiles.isEmpty()) {
                        Text(stringResource(Res.string.no_audio_files))
                    } else if (state.batchDownload.isLoadingFiles) {
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
                            items(state.batchDownload.audioFiles.size) { index ->
                                DownloaderCard(
                                    audioFile = state.batchDownload.audioFiles[index],
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