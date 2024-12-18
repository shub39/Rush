package com.shub39.rush.lyrics.presentation.setting

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.lyrics.domain.backup.ExportState
import com.shub39.rush.lyrics.domain.backup.RestoreState
import com.shub39.rush.lyrics.presentation.setting.component.BetterIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Backup (
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit
) {
    val context = LocalContext.current

    var uri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri = it }

    LaunchedEffect(Unit) {
        action(SettingsPageAction.ResetBackup)
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 500.dp)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.backup))
                    }
                )

                ListItem(
                    headlineContent = { Text(stringResource(R.string.export)) },
                    supportingContent = { Text(stringResource(R.string.export_info)) },
                    trailingContent = {
                        BetterIconButton(
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
                    headlineContent = { Text(stringResource(R.string.restore)) },
                    supportingContent = { Text(stringResource(R.string.restore_info)) },
                    trailingContent = {
                        Row {
                            if (uri == null) {
                                TextButton(
                                    onClick = { launcher.launch(arrayOf("application/json")) }
                                ) {
                                    Text(text = stringResource(R.string.choose_file))
                                }
                            }

                            if (uri != null) {
                                BetterIconButton(
                                    onClick = { action(SettingsPageAction.OnRestoreSongs(uri!!, context)) }
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
}