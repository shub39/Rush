package com.shub39.rush.logic

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.shub39.rush.R
import com.shub39.rush.database.AudioFile
import com.shub39.rush.ui.component.DownloaderCard
import com.shub39.rush.viewmodel.RushViewModel

object BatchDownloader {

    @Composable
    fun GetLibraryPath(
        update: (Uri?) -> Unit
    ) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree()
        ) { uri ->
            update(uri)
        }

        Button(
            onClick = {
                launcher.launch(null)
            }
        ) {
            Text(
                text = stringResource(R.string.select_folder)
            )
        }
    }

    @Composable
    fun GetAudioFiles(
        directoryUri: Uri,
        context: Context,
        audioFiles: List<AudioFile>,
        update: (AudioFile) -> Unit,
        indexes: Map<Int, Boolean>
    ) {
        val documentFile = DocumentFile.fromTreeUri(context, directoryUri)
        var isLoadingFiles by remember { mutableStateOf(true) }

        LaunchedEffect(directoryUri) {
            if (documentFile != null && documentFile.isDirectory) {
                for (file in documentFile.listFiles()) {
                    if (file.isFile && file.type?.startsWith("audio/") == true) {
                        val retriever = MediaMetadataRetriever()
                        retriever.setDataSource(context, file.uri)

                        val title =
                            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                        val artist =
                            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)

                        if (title != null && artist != null) {
                            update(AudioFile(title, artist))
                        }

                        retriever.release()
                    }
                }
            }

            isLoadingFiles = false
        }

        OutlinedCard {
            LazyColumn(
                modifier = Modifier
                    .height(400.dp)
                    .padding(8.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                if (!isLoadingFiles) {
                    items(audioFiles.size) { index ->
                        DownloaderCard(
                            title = audioFiles[index].title,
                            artist = audioFiles[index].artist,
                            state = indexes[index]
                        )
                    }
                } else {
                    item {
                        CircularProgressIndicator(
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun Download(
        audioFiles: List<AudioFile>,
        rushViewModel: RushViewModel,
        isDownloading: Boolean
    ) {
        Button(
            onClick = {
                rushViewModel.batchDownload(
                    list = audioFiles
                )
            },
            enabled = !isDownloading
        ) {
            Text(
                stringResource(R.string.download)
            )

            if (isDownloading) {
                CircularProgressIndicator(
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }

}