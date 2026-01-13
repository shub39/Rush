package com.shub39.rush.presentation.setting.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DriveFolderUpload
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.domain.backup.ExportState
import com.shub39.rush.domain.backup.RestoreState
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.presentation.PageFill
import com.shub39.rush.presentation.RushTheme
import com.shub39.rush.presentation.endItemShape
import com.shub39.rush.presentation.leadingItemShape
import com.shub39.rush.presentation.listItemColors
import com.shub39.rush.presentation.setting.SettingsPageAction
import com.shub39.rush.presentation.setting.SettingsPageState
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.writeString
import kotlinx.coroutines.launch

// backup page
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BackupPage(
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit,
    onNavigateBack: () -> Unit
) = PageFill {
    val scope = rememberCoroutineScope()

    var restoreFile by remember { mutableStateOf<PlatformFile?>(null) }
    val fileSaverLauncher = rememberFileSaverLauncher { file ->
        if (file != null && state.exportState is ExportState.ExportReady) {
            scope.launch {
                file.writeString(state.exportState.data)
            }
        }
    }
    val filePickerLauncher = rememberFilePickerLauncher(
        type = FileKitType.File(extensions = listOf("json"))
    ) { file -> restoreFile = file }

    LaunchedEffect(Unit) {
        action(SettingsPageAction.ResetBackup)
        action(SettingsPageAction.OnExportSongs)
    }

    BackupPageContent(
        onNavigateBack = onNavigateBack,
        onSaveFile = {
            fileSaverLauncher.launch(
                suggestedName = "Rush Export",
                extension = "json"
            )
        },
        state = state,
        restoreFile = restoreFile,
        action = action,
        onPickFile = { filePickerLauncher.launch() }
    )
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
private fun BackupPageContent(
    onNavigateBack: () -> Unit,
    onSaveFile: () -> Unit,
    state: SettingsPageState,
    restoreFile: PlatformFile?,
    onPickFile: () -> Unit,
    action: (SettingsPageAction) -> Unit
) {
    Scaffold(
        modifier = Modifier.widthIn(max = 700.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.backup))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Navigate Back",
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp,
                bottom = paddingValues.calculateBottomPadding() + 60.dp,
                start = paddingValues.calculateLeftPadding(LocalLayoutDirection.current) + 16.dp,
                end = paddingValues.calculateRightPadding(LocalLayoutDirection.current) + 16.dp
            ),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Column(
                        modifier = Modifier.clip(leadingItemShape())
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.export)
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Rounded.DriveFolderUpload,
                                    contentDescription = null
                                )
                            },
                            colors = listItemColors(),
                            supportingContent = {
                                Text(
                                    text = stringResource(R.string.export_info)
                                )
                            }
                        )

                        Row(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(listItemColors().containerColor)
                                .padding(start = 52.dp, end = 16.dp, bottom = 8.dp)
                        ) {
                            Button(
                                onClick = onSaveFile,
                                enabled = state.exportState is ExportState.ExportReady,
                                modifier = Modifier.weight(1f)
                            ) {
                                when (state.exportState) {
                                    is ExportState.ExportReady -> {
                                        Icon(
                                            imageVector = Icons.Rounded.PlayArrow,
                                            contentDescription = "Start Export",
                                        )
                                    }

                                    ExportState.Exporting -> {
                                        LoadingIndicator(
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    ExportState.Error -> {
                                        Icon(
                                            imageVector = Icons.Rounded.Error,
                                            contentDescription = "Error"
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.clip(endItemShape())
                    ) {
                        ListItem(
                            colors = listItemColors(),
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Rounded.FileDownload,
                                    contentDescription = null
                                )
                            },
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.restore)
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(R.string.restore_info)
                                )
                            }
                        )

                        Row(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(listItemColors().containerColor)
                                .padding(start = 52.dp, end = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Button(
                                onClick = onPickFile,
                                modifier = Modifier.weight(1f),
                                enabled = state.restoreState !is RestoreState.Restoring
                            ) {
                                Text(text = stringResource(R.string.choose_file))
                            }

                            if (restoreFile != null) {
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = { action(SettingsPageAction.OnRestoreSongs(restoreFile.toString())) },
                                    enabled = state.restoreState is RestoreState.Idle
                                ) {
                                    when (state.restoreState) {
                                        RestoreState.Idle -> {
                                            Icon(
                                                imageVector = Icons.Rounded.PlayArrow,
                                                contentDescription = "Start Restore",
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(ButtonDefaults.MediumIconSpacing))
                                            Text(
                                                text = stringResource(R.string.start)
                                            )
                                        }

                                        RestoreState.Restoring -> {
                                            LoadingIndicator(
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        RestoreState.Restored -> {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = "Restored",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        is RestoreState.Failure -> {
                                            Icon(
                                                imageVector = Icons.Rounded.Warning,
                                                contentDescription = "Error",
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
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RushTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        BackupPageContent(
            onNavigateBack = { },
            onSaveFile = { },
            state = SettingsPageState(),
            restoreFile = PlatformFile(""),
            onPickFile = { },
            action = { }
        )
    }
}