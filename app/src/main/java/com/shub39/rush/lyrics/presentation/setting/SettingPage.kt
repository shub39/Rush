package com.shub39.rush.lyrics.presentation.setting

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.lyrics.presentation.setting.component.AudioFile
import com.shub39.rush.core.data.RushDataStore
import com.shub39.rush.core.domain.AppTheme
import com.shub39.rush.lyrics.domain.backup.ExportRepo
import com.shub39.rush.lyrics.presentation.setting.component.GetAudioFiles
import com.shub39.rush.lyrics.presentation.setting.component.GetLibraryPath
import com.shub39.rush.lyrics.domain.UILogic.openLinkInBrowser
import com.shub39.rush.lyrics.domain.backup.ExportResult
import com.shub39.rush.lyrics.domain.backup.ExportState
import com.shub39.rush.lyrics.domain.backup.RestoreRepo
import com.shub39.rush.lyrics.domain.backup.RestoreResult
import com.shub39.rush.lyrics.domain.backup.RestoreState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(
    state: SettingsPageState,
    notificationAccess: Boolean,
    action: (SettingsPageAction) -> Unit,
    paddingValues: PaddingValues,
    exportRepo: ExportRepo = koinInject(),
    restoreRepo: RestoreRepo = koinInject()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val exportState by ExportResult.state.collectAsState()
    val restoreState by RestoreResult.state.collectAsState()

    var deleteButtonStatus by remember { mutableStateOf(true) }
    var deleteConfirmationDialog by remember { mutableStateOf(false) }
    var batchDownloadDialog by remember { mutableStateOf(false) }

    val maxLinesFlow by RushDataStore.getMaxLinesFlow(context).collectAsState(initial = 6)
    val appTheme by RushDataStore.getToggleThemeFlow(context)
        .collectAsState(initial = "Gruvbox")
    val colorPreference by RushDataStore.getLyricsColorFlow(context)
        .collectAsState(CardColors.MUTED.color)

    LaunchedEffect(Unit) {
        ExportResult.resetState()
        RestoreResult.resetState()
    }

    LaunchedEffect(exportState) {
        when (exportState) {
            ExportState.EXPORTING -> {
                Toast.makeText(context, context.getText(R.string.exporting), Toast.LENGTH_SHORT).show()
            }
            ExportState.EXPORTED -> {
                Toast.makeText(context, context.getText(R.string.exported), Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    LaunchedEffect(restoreState) {
        when (restoreState) {
            RestoreState.RESTORING -> {
                Toast.makeText(context, context.getText(R.string.restoring), Toast.LENGTH_SHORT).show()
            }
            RestoreState.RESTORED -> {
                Toast.makeText(context, context.getText(R.string.restored), Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.theme)) },
                    trailingContent = {
                        val material = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

                        IconButton(
                            onClick = {
                                val newTheme = when (appTheme) {
                                    AppTheme.LIME.type -> AppTheme.YELLOW.type
                                    AppTheme.YELLOW.type -> if (material) AppTheme.MATERIAL.type else AppTheme.LIME.type
                                    else -> AppTheme.LIME.type
                                }

                                coroutineScope.launch {
                                    RushDataStore.updateToggleTheme(context, newTheme)
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.vibrant_colors)) },
                    supportingContent = { Text(text = stringResource(id = R.string.vibrant_colors_info)) },
                    trailingContent = {
                        Switch(
                            checked = colorPreference == CardColors.VIBRANT.color,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    when (it) {
                                        true -> RushDataStore.setLyricsColor(
                                            context,
                                            CardColors.VIBRANT.color
                                        )

                                        else -> RushDataStore.setLyricsColor(
                                            context,
                                            CardColors.MUTED.color
                                        )
                                    }
                                }
                            }
                        )
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.max_lines)) },
                    supportingContent = {
                        Column {
                            Text(maxLinesFlow.toString())

                            Slider(
                                value = maxLinesFlow.toFloat(),
                                valueRange = 2f..8f,
                                steps = 5,
                                onValueChange = {
                                    coroutineScope.launch {
                                        RushDataStore.updateMaxLines(context, it.toInt())
                                    }
                                }
                            )
                        }
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.delete_all)) },
                    trailingContent = {
                        IconButton(
                            onClick = { deleteConfirmationDialog = true },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            enabled = deleteButtonStatus
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.batch_download)) },
                    supportingContent = { Text(text = stringResource(id = R.string.batch_download_info)) },
                    trailingContent = {
                        IconButton(
                            onClick = { batchDownloadDialog = true },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_download_done_24),
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            item {
                var uri by remember { mutableStateOf<Uri?>(null) }
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument()
                ) { uri = it }

                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.backup)) },
                    supportingContent = { Text(text = stringResource(R.string.backup_info)) },
                    trailingContent = {
                        Row {
                            IconButton(
                                onClick = { coroutineScope.launch {
                                    launcher.launch(arrayOf("application/json"))
                                } },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_drive_file_move_24),
                                    contentDescription = null
                                )
                            }

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        uri?.let {
                                            when (restoreRepo.restoreSongs(it, context)) {
                                                is RestoreResult.Failiure -> {
                                                    Toast.makeText(context, context.getText(R.string.restore_failed), Toast.LENGTH_SHORT).show()
                                                }
                                                else -> {}
                                            }

                                            uri = null
                                        }
                                    }
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                enabled = restoreState == RestoreState.IDLE && uri != null
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_file_download_24),
                                    contentDescription = null
                                )
                            }

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        exportRepo.exportToJson()
                                    }
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                enabled = exportState == ExportState.IDLE
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_file_upload_24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
            }

            if (!notificationAccess) {
                item {
                    val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")

                    ListItem(
                        headlineContent = { Text(text = stringResource(id = R.string.grant_permission)) },
                        supportingContent = { Text(text = stringResource(id = R.string.notification_permission)) },
                        trailingContent = {
                            IconButton(
                                onClick = { context.startActivity(intent) },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }

            item {
                ListItem(
                    headlineContent = { Text("Made by shub39") },
                    supportingContent = { Text("Your friendly neighbourhood audiophile schizoid") },
                    trailingContent = {
                        Row {
                            IconButton(
                                onClick = {
                                    openLinkInBrowser(
                                        context,
                                        "https://github.com/shub39/Rush"
                                    )
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.github_mark),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    openLinkInBrowser(
                                        context,
                                        "https://discord.gg/https://discord.gg/nxA2hgtEKf"
                                    )
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.discord_svgrepo_com),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    if (deleteConfirmationDialog) {
        BasicAlertDialog(
            onDismissRequest = { deleteConfirmationDialog = false }
        ) {
            Card(shape = MaterialTheme.shapes.extraLarge) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_warning_24),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Text(
                        text = stringResource(R.string.delete_all),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(id = R.string.delete_confirmation),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Button(
                        onClick = {
                            action(SettingsPageAction.OnDeleteSongs)
                            deleteConfirmationDialog = false
                            deleteButtonStatus = false
                        },
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.delete_all))
                    }
                }
            }
        }
    }

    if (batchDownloadDialog) {
        var selectedDirectoryUri by remember { mutableStateOf<Uri?>(null) }
        val audioFiles = remember { mutableStateListOf<AudioFile>() }
        var done by remember { mutableStateOf(false) }

        BasicAlertDialog(
            onDismissRequest = {
                if (!state.batchDownload.isDownloading) {
                    batchDownloadDialog = false
                    action(SettingsPageAction.OnClearIndexes)
                }
            }
        ) {
            Card(shape = MaterialTheme.shapes.extraLarge) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selectedDirectoryUri == null || audioFiles.isEmpty()) {
                        GetLibraryPath(
                            update = {
                                selectedDirectoryUri = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }

                    selectedDirectoryUri?.let { uri ->
                        GetAudioFiles(
                            directoryUri = uri,
                            context = context,
                            audioFiles = audioFiles,
                            update = {
                                audioFiles.add(it)
                            },
                            indexes = state.batchDownload.indexes,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(
                        onClick = {
                            if (!done) {
                                action(SettingsPageAction.OnBatchDownload(audioFiles))
                                done = true
                            } else {
                                batchDownloadDialog = false
                                action(SettingsPageAction.OnClearIndexes)
                            }
                        },
                        enabled = !state.batchDownload.isDownloading && audioFiles.isNotEmpty(),
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        if (state.batchDownload.isDownloading) {
                            CircularProgressIndicator(
                                strokeCap = StrokeCap.Round,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = when (done) {
                                    true -> stringResource(R.string.done)
                                    else -> stringResource(R.string.download)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    showSystemUi = true, showBackground = true, backgroundColor = 0xFFFFFFFF,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun SettingPagePreview() {
    Scaffold { inner ->
        SettingPage(
            state = SettingsPageState(),
            action = {},
            paddingValues = inner,
            notificationAccess = false
        )
    }
}