package com.shub39.rush.lyrics.presentation.setting

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.domain.Route
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.lyrics.presentation.setting.component.BetterIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(
    state: SettingsPageState,
    notificationAccess: Boolean,
    action: (SettingsPageAction) -> Unit,
    navigator: (Route) -> Unit,
) = PageFill {
    val context = LocalContext.current

    var deleteButtonStatus by remember { mutableStateOf(true) }
    var deleteConfirmationDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.widthIn(max = 700.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings))
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.look_and_feel)) },
                    supportingContent = { Text(text = stringResource(R.string.look_and_feel_info)) },
                    trailingContent = {
                        BetterIconButton(
                            onClick = { navigator(Route.LookAndFeelPage) },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_arrow_forward_ios_24),
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.max_lines)) },
                    supportingContent = {
                        Column {
                            Text(state.maxLines.toString())

                            Slider(
                                value = state.maxLines.toFloat(),
                                valueRange = 2f..16f,
                                onValueChange = {
                                    action(SettingsPageAction.OnUpdateMaxLines(it.toInt()))
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
                        BetterIconButton(
                            onClick = { deleteConfirmationDialog = true },
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
                        BetterIconButton(
                            onClick = { navigator(Route.BatchDownloaderPage) },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_arrow_forward_ios_24),
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.backup)) },
                    supportingContent = { Text(text = stringResource(R.string.backup_info)) },
                    trailingContent = {
                        BetterIconButton(
                            onClick = { navigator(Route.BackupPage) }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_arrow_forward_ios_24),
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            if (!notificationAccess) {
                item {
                    val intent =
                        Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")

                    ListItem(
                        headlineContent = { Text(text = stringResource(id = R.string.grant_permission)) },
                        supportingContent = { Text(text = stringResource(id = R.string.notification_permission)) },
                        trailingContent = {
                            BetterIconButton(
                                onClick = { context.startActivity(intent) }
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
                    headlineContent = { Text(stringResource(R.string.about)) },
                    trailingContent = {
                        BetterIconButton(
                            onClick = { navigator(Route.AboutPage) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_arrow_forward_ios_24),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
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
}