package com.shub39.rush.lyrics.presentation.setting

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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.core.presentation.RushDialog
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowLeft
import compose.icons.fontawesomeicons.solid.ArrowRight
import org.jetbrains.compose.resources.stringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.about_libraries
import rush.app.generated.resources.backup
import rush.app.generated.resources.backup_info
import rush.app.generated.resources.delete_all
import rush.app.generated.resources.delete_confirmation
import rush.app.generated.resources.look_and_feel
import rush.app.generated.resources.look_and_feel_info
import rush.app.generated.resources.settings

// topmost settings page
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingRootPage(
    notificationAccess: Boolean,
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLookAndFeel: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onNavigateToAboutLibraries: () -> Unit
) = PageFill {
    var deleteConfirmationDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.widthIn(max = 1000.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.settings))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.ArrowLeft,
                            contentDescription = "Navigate Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // navigate to look and feel
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(Res.string.look_and_feel)) },
                    supportingContent = { Text(text = stringResource(Res.string.look_and_feel_info)) },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = onNavigateToLookAndFeel,
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.ArrowRight,
                                contentDescription = "Navigate",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )
            }

            // nuke everything
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(Res.string.delete_all)) },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = { deleteConfirmationDialog = true },
                            enabled = state.deleteButtonEnabled
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            // navigate to backup
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(Res.string.backup)) },
                    supportingContent = { Text(text = stringResource(Res.string.backup_info)) },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = onNavigateToBackup
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.ArrowRight,
                                contentDescription = "Navigate",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )
            }

            // navigate to notification access permission page
            notificationAccessReminder(notificationAccess)

            // navigate to about app
            item {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.about_libraries)) },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = onNavigateToAboutLibraries
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.ArrowRight,
                                contentDescription = "Navigate",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )
            }
        }
    }

    // dialog to confirm nuking
    if (deleteConfirmationDialog) {
        RushDialog(
            onDismissRequest = { deleteConfirmationDialog = false }
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Text(
                    text = stringResource(Res.string.delete_all),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(Res.string.delete_confirmation),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Button(
                    onClick = {
                        action(SettingsPageAction.OnDeleteSongs)
                        deleteConfirmationDialog = false
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(Res.string.delete_all))
                }
            }
        }
    }
}