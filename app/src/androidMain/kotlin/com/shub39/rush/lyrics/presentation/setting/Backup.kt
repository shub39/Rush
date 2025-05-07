package com.shub39.rush.lyrics.presentation.setting

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.data.Theme
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.lyrics.domain.backup.ExportState
import com.shub39.rush.lyrics.domain.backup.RestoreState
import org.jetbrains.compose.resources.stringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.backup
import rush.app.generated.resources.choose_file
import rush.app.generated.resources.export
import rush.app.generated.resources.export_info
import rush.app.generated.resources.restore
import rush.app.generated.resources.restore_info

// backup page
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Backup(
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit
) = PageFill {
    var uri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri = it }

    LaunchedEffect(Unit) {
        action(SettingsPageAction.ResetBackup)
    }

    Scaffold(
        modifier = Modifier.widthIn(max = 500.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.backup))
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
                                    painter = painterResource(R.drawable.round_play_arrow_24),
                                    contentDescription = null
                                )
                            }

                            ExportState.EXPORTING -> {
                                CircularProgressIndicator(
                                    strokeCap = StrokeCap.Round,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            ExportState.EXPORTED -> {
                                Icon(
                                    painter = painterResource(R.drawable.round_check_circle_outline_24),
                                    contentDescription = null
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
                        if (uri == null) {
                            TextButton(
                                onClick = { launcher.launch(arrayOf("application/json")) }
                            ) {
                                Text(text = stringResource(Res.string.choose_file))
                            }
                        }

                        if (uri != null) {
                            FilledTonalIconButton(
                                onClick = { action(SettingsPageAction.OnRestoreSongs(uri!!)) }
                            ) {
                                when (state.restoreState) {
                                    RestoreState.IDLE -> {
                                        Icon(
                                            painter = painterResource(R.drawable.round_play_arrow_24),
                                            contentDescription = null
                                        )
                                    }

                                    RestoreState.RESTORING -> {
                                        CircularProgressIndicator(
                                            strokeCap = StrokeCap.Round,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    RestoreState.RESTORED -> {
                                        Icon(
                                            painter = painterResource(R.drawable.round_check_circle_outline_24),
                                            contentDescription = null
                                        )
                                    }

                                    RestoreState.FAILURE -> {
                                        Icon(
                                            painter = painterResource(R.drawable.round_disabled_by_default_24),
                                            contentDescription = null
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

@PreviewLightDark
@Composable
private fun Preview() {
    RushTheme(
        state = Theme()
    ) {
        Backup(
            SettingsPageState()
        ) { }
    }
}