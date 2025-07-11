package com.shub39.rush.lyrics.presentation.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.lyrics.domain.backup.ExportState
import com.shub39.rush.lyrics.domain.backup.RestoreState
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowLeft
import compose.icons.fontawesomeicons.solid.Check
import compose.icons.fontawesomeicons.solid.Play
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import org.jetbrains.compose.resources.stringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.backup
import rush.app.generated.resources.choose_file
import rush.app.generated.resources.export
import rush.app.generated.resources.export_info
import rush.app.generated.resources.restore
import rush.app.generated.resources.restore_info

// backup page
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BackupPage(
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit,
    onNavigateBack: () -> Unit
) = PageFill {
    var restoreFile by remember { mutableStateOf<PlatformFile?>(null) }
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.File(extensions = listOf("json"))
    ) { file -> restoreFile = file }

    LaunchedEffect(Unit) {
        action(SettingsPageAction.ResetBackup)
    }

    Scaffold(
        modifier = Modifier.widthIn(max = 1000.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.backup))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.ArrowLeft,
                            contentDescription = "Navigate Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            ListItem(
                headlineContent = { Text(stringResource(Res.string.export)) },
                supportingContent = { Text(stringResource(Res.string.export_info)) },
                trailingContent = {
                    FilledTonalIconButton(
                        onClick = { action(SettingsPageAction.OnExportSongs) },
                        enabled = state.exportState == ExportState.IDLE
                    ) {
                        when (state.exportState) {
                            ExportState.IDLE -> {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.Play,
                                    contentDescription = "Start Export",
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            ExportState.EXPORTING -> {
                                LoadingIndicator(
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            ExportState.EXPORTED -> {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.Check,
                                    contentDescription = "Done",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            )

            ListItem(
                headlineContent = { Text(stringResource(Res.string.restore)) },
                supportingContent = { Text(stringResource(Res.string.restore_info)) },
                trailingContent = {
                    Row {
                        if (restoreFile == null) {
                            TextButton(
                                onClick = { launcher.launch() }
                            ) {
                                Text(text = stringResource(Res.string.choose_file))
                            }
                        }

                        if (restoreFile != null) {
                            FilledTonalIconButton(
                                onClick = { action(SettingsPageAction.OnRestoreSongs(restoreFile!!.toString())) }
                            ) {
                                when (state.restoreState) {
                                    RestoreState.IDLE -> {
                                        Icon(
                                            imageVector = FontAwesomeIcons.Solid.Play,
                                            contentDescription = "Start Restore",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    RestoreState.RESTORING -> {
                                        LoadingIndicator(
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    RestoreState.RESTORED -> {
                                        Icon(
                                            imageVector = FontAwesomeIcons.Solid.Check,
                                            contentDescription = "Restored",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    RestoreState.FAILURE -> {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Error",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}