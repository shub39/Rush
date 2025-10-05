package com.shub39.rush.setting.section

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.core.presentation.RushDialog
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.setting.SettingsPageAction
import com.shub39.rush.setting.SettingsPageState
import com.shub39.rush.setting.component.AboutApp

// topmost settings page
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingRootPage(
    notificationAccess: Boolean,
    state: SettingsPageState,
    onAction: (SettingsPageAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLookAndFeel: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onNavigateToAboutLibraries: () -> Unit
) = PageFill {
    var deleteConfirmationDialog by remember { mutableStateOf(false) }

    val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehaviour.nestedScrollConnection)
            .widthIn(max = 1000.dp),
        topBar = {
            MediumFlexibleTopAppBar(
                scrollBehavior = scrollBehaviour,
                title = {
                    Text(stringResource(R.string.settings))
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
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            item {
                Card(
                    onClick = { onAction(SettingsPageAction.OnShowPaywall) },
                    modifier = Modifier.padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Rush Pro",
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.rush_pro),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Grit Plus"
                        )
                    }
                }
            }

            // about app
            item { AboutApp() }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // navigate to look and feel
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.look_and_feel)) },
                    supportingContent = { Text(text = stringResource(R.string.look_and_feel_info)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Palette,
                            contentDescription = "Navigate",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingContent = {
                        IconButton(
                            onClick = { onNavigateToLookAndFeel() },
                            shapes = IconButtonShapes(
                                shape = CircleShape,
                                pressedShape = RoundedCornerShape(10.dp)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            // navigate to backup
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.backup)) },
                    supportingContent = { Text(text = stringResource(R.string.backup_info)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Upload,
                            contentDescription = "Backup",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingContent = {
                        IconButton(
                            onClick = { onNavigateToBackup() },
                            shapes = IconButtonShapes(
                                shape = CircleShape,
                                pressedShape = RoundedCornerShape(10.dp)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            // navigate to about app
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.about_libraries)) },
                    supportingContent = { Text(text = stringResource(R.string.about_libraries)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = "About Libraries",
                        )
                    },
                    trailingContent = {
                        IconButton(
                            onClick = { onNavigateToAboutLibraries() },
                            shapes = IconButtonShapes(
                                shape = CircleShape,
                                pressedShape = RoundedCornerShape(10.dp)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            // navigate to notification access permission page
            if (!notificationAccess) {
                item {
                    val context = LocalContext.current
                    val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")

                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.grant_permission)) },
                        supportingContent = { Text(text = stringResource(R.string.notification_permission)) },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.Notifications,
                                contentDescription = null
                            )
                        },
                        trailingContent = {
                            IconButton(
                                onClick = { context.startActivity(intent) }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }

            // nuke everything
            item {
                ListItem(
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = "Caution"
                        )
                    },
                    headlineContent = { Text(text = stringResource(R.string.delete_all)) },
                    trailingContent = {
                        IconButton(
                            onClick = { deleteConfirmationDialog = true },
                            enabled = state.deleteButtonEnabled,
                            shapes = IconButtonShapes(
                                shape = CircleShape,
                                pressedShape = RoundedCornerShape(10.dp)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null
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
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Text(
                    text = stringResource(R.string.delete_all),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.delete_confirmation),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Button(
                    onClick = {
                        onAction(SettingsPageAction.OnDeleteSongs)
                        deleteConfirmationDialog = false
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.delete_all))
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
        SettingRootPage(
            notificationAccess = false,
            state = SettingsPageState(),
            onAction = {  },
            onNavigateBack = {  },
            onNavigateToLookAndFeel = {  },
            onNavigateToBackup = {  },
            onNavigateToAboutLibraries = {  }
        )
    }
}