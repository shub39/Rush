package com.shub39.rush.ui.page.setting

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.shub39.rush.R
import com.shub39.rush.database.CardColors
import com.shub39.rush.ui.page.setting.component.AudioFile
import com.shub39.rush.database.SettingsDataStore
import com.shub39.rush.listener.NotificationListener
import com.shub39.rush.ui.page.setting.component.GetAudioFiles
import com.shub39.rush.ui.page.setting.component.GetLibraryPath
import com.shub39.rush.logic.UILogic.openLinkInBrowser
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(
   state: SettingsPageState,
   action: (SettingsPageAction) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var deleteButtonStatus by remember { mutableStateOf(true) }
    var notificationRequestDialog by remember { mutableStateOf(false) }
    var deleteConfirmationDialog by remember { mutableStateOf(false) }
    var batchDownload by remember { mutableStateOf(false) }
    val maxLinesFlow by SettingsDataStore.getMaxLinesFlow(context).collectAsState(initial = 6)
    val appTheme by SettingsDataStore.getToggleThemeFlow(context)
        .collectAsState(initial = "Gruvbox")
    val colorPreference by SettingsDataStore.getLyricsColorFlow(context)
        .collectAsState(CardColors.MUTED.color)

    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomEnd = 8.dp,
                        bottomStart = 8.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = stringResource(id = R.string.theme))

                        Spacer(modifier = Modifier.padding(4.dp))

                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                listOf(
                                    "Material",
                                    "Yellow",
                                    "Lime"
                                ).forEachIndexed { index, color ->
                                    SegmentedButton(
                                        label = { Text(text = color) },
                                        selected = appTheme == color,
                                        onClick = {
                                            coroutineScope.launch {
                                                SettingsDataStore.updateToggleTheme(context, color)
                                            }
                                        },
                                        shape = when (index) {
                                            0 -> RoundedCornerShape(
                                                topStart = 16.dp,
                                                bottomStart = 16.dp
                                            )

                                            2 -> RoundedCornerShape(
                                                topEnd = 16.dp,
                                                bottomEnd = 16.dp
                                            )

                                            else -> RoundedCornerShape(0.dp)
                                        }
                                    )
                                }
                            } else {
                                listOf("Yellow", "Lime").forEachIndexed { index, color ->
                                    SegmentedButton(
                                        label = { Text(text = color) },
                                        selected = appTheme == color,
                                        onClick = {
                                            coroutineScope.launch {
                                                SettingsDataStore.updateToggleTheme(context, color)
                                            }
                                        },
                                        shape = when (index) {
                                            0 -> RoundedCornerShape(
                                                topStart = 16.dp,
                                                bottomStart = 16.dp
                                            )

                                            else -> RoundedCornerShape(
                                                topEnd = 16.dp,
                                                bottomEnd = 16.dp
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    shape = RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp,
                        bottomEnd = 8.dp,
                        bottomStart = 8.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.vibrant_colors))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Switch(
                                checked = colorPreference == CardColors.VIBRANT.color,
                                onCheckedChange = {
                                    coroutineScope.launch {
                                        when (it) {
                                            true -> SettingsDataStore.setLyricsColor(
                                                context,
                                                CardColors.VIBRANT.color
                                            )

                                            else -> SettingsDataStore.setLyricsColor(
                                                context,
                                                CardColors.MUTED.color
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.max_lines))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_arrow_back_ios_24),
                                contentDescription = null,
                                modifier = Modifier.clickable(
                                    enabled = maxLinesFlow > 2 && coroutineScope.isActive
                                ) {
                                    coroutineScope.launch {
                                        SettingsDataStore.updateMaxLines(context, maxLinesFlow - 1)
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.padding(4.dp))

                            Text(text = maxLinesFlow.toString())

                            Spacer(modifier = Modifier.padding(4.dp))

                            Icon(
                                painter = painterResource(id = R.drawable.round_arrow_forward_ios_24),
                                contentDescription = null,
                                modifier = Modifier.clickable(
                                    enabled = maxLinesFlow < 8 && coroutineScope.isActive
                                ) {
                                    coroutineScope.launch {
                                        SettingsDataStore.updateMaxLines(context, maxLinesFlow + 1)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            if (!NotificationListener.canAccessNotifications(context)) {
                item {
                    Button(
                        onClick = {
                            notificationRequestDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge,
                    ) {
                        Text(text = stringResource(id = R.string.grant_permission))
                    }
                }
            }

            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        deleteConfirmationDialog = true
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    enabled = deleteButtonStatus
                ) {
                    Text(text = stringResource(id = R.string.delete_all))
                }
            }

            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        batchDownload = true
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    Text(text = stringResource(id = R.string.batch_download))
                }
            }

        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = "Made by shub39",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Row(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { openLinkInBrowser(context, "https://github.com/shub39/Rush") },
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.github_mark),
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.padding(8.dp))

                IconButton(
                    onClick = {
                        openLinkInBrowser(
                            context,
                            "https://discord.gg/https://discord.gg/nxA2hgtEKf"
                        )
                    },
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.discord_svgrepo_com),
                        contentDescription = null
                    )
                }
            }
        }
    }

    if (notificationRequestDialog) {
        BasicAlertDialog(
            onDismissRequest = { notificationRequestDialog = false }
        ) {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")

            Card(shape = MaterialTheme.shapes.extraLarge) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_warning_24),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Text(
                        text = stringResource(id = R.string.notification_permission),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Button(
                        onClick = {
                            startActivity(context, intent, null)
                            notificationRequestDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Text(text = stringResource(id = R.string.grant_permission))
                    }
                }
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
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_warning_24),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Text(
                        text = stringResource(id = R.string.delete_confirmation),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Button(
                        onClick = {
                            action(SettingsPageAction.OnDeleteSongs)
                            deleteConfirmationDialog = false
                            deleteButtonStatus = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Text(text = stringResource(id = R.string.delete_all))
                    }
                }
            }
        }
    }

    if (batchDownload) {
        var selectedDirectoryUri by remember { mutableStateOf<Uri?>(null) }
        val audioFiles = remember { mutableStateListOf<AudioFile>() }
        var done by remember { mutableStateOf(false) }

        BasicAlertDialog(
            onDismissRequest = {
                if (!state.batchDownload.isDownloading) {
                    batchDownload = false
                    action(SettingsPageAction.OnClearIndexes)
                }
            }
        ) {
            Card(shape = MaterialTheme.shapes.extraLarge) {
                Column(
                    modifier = Modifier
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
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Button(
                        onClick = {
                            if (!done) {
                                action(SettingsPageAction.OnBatchDownload(audioFiles))
                                done = true
                            } else {
                                batchDownload = false
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