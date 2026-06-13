/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.shared.ui.setting.section

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.component.PageFill
import com.shub39.rush.shared.ui.component.RushDialog
import com.shub39.rush.shared.ui.detachedItemShape
import com.shub39.rush.shared.ui.endItemShape
import com.shub39.rush.shared.ui.leadingItemShape
import com.shub39.rush.shared.ui.listItemColors
import com.shub39.rush.shared.ui.setting.SettingsPageAction
import com.shub39.rush.shared.ui.setting.SettingsPageState
import com.shub39.rush.shared.ui.setting.appLanguagePicker
import com.shub39.rush.shared.ui.setting.component.LocalePickerSheet
import com.shub39.rush.shared.ui.setting.notificationToggle
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

// topmost settings page
@Composable
fun SettingRootPage(
    modifier: Modifier = Modifier,
    notificationAccess: Boolean,
    state: SettingsPageState,
    onShowPaywall: () -> Unit,
    onAction: (SettingsPageAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLookAndFeel: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onNavigateToChangelog: () -> Unit,
    onNavigateToAppInfo: () -> Unit,
    onUpdateNotificationAccess: () -> Unit,
) =
    PageFill(modifier = modifier) {
        var deleteConfirmationDialog by remember { mutableStateOf(false) }
        var showLocalePicker by remember { mutableStateOf(false) }

        val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Scaffold(
            modifier =
                Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection).widthIn(max = 700.dp),
            topBar = {
                LargeFlexibleTopAppBar(
                    scrollBehavior = scrollBehaviour,
                    title = {
                        Text(
                            text = stringResource(Res.string.settings),
                            fontFamily = flexFontEmphasis(),
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                painter = painterResource(Res.drawable.arrow_back),
                                contentDescription = "Navigate Back",
                            )
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            scrolledContainerColor = MaterialTheme.colorScheme.surface
                        ),
                )
            },
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding =
                    PaddingValues(
                        top = paddingValues.calculateTopPadding() + 16.dp,
                        bottom = paddingValues.calculateBottomPadding() + 60.dp,
                        start =
                            paddingValues.calculateLeftPadding(LocalLayoutDirection.current) +
                                16.dp,
                        end =
                            paddingValues.calculateRightPadding(LocalLayoutDirection.current) +
                                16.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Pro
                rushProItem(onShowPaywall = onShowPaywall)

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        // navigate to look and feel
                        ListItem(
                            modifier =
                                Modifier.clip(leadingItemShape()).clickable {
                                    onNavigateToLookAndFeel()
                                },
                            colors = listItemColors(),
                            headlineContent = {
                                Text(text = stringResource(Res.string.look_and_feel))
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(Res.string.look_and_feel_info),
                                    maxLines = 1,
                                    modifier = Modifier.basicMarquee(),
                                )
                            },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(Res.drawable.palette),
                                    contentDescription = "Navigate",
                                    modifier = Modifier.size(24.dp),
                                )
                            },
                            trailingContent = {
                                Icon(
                                    painter = painterResource(Res.drawable.arrow_forward_ios),
                                    contentDescription = null,
                                )
                            },
                        )

                        // navigate to backup
                        ListItem(
                            modifier =
                                Modifier.clip(endItemShape()).clickable { onNavigateToBackup() },
                            colors = listItemColors(),
                            headlineContent = { Text(text = stringResource(Res.string.backup)) },
                            supportingContent = {
                                Text(
                                    text = stringResource(Res.string.backup_info),
                                    maxLines = 1,
                                    modifier = Modifier.basicMarquee(),
                                )
                            },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(Res.drawable.upload_file),
                                    contentDescription = "Backup",
                                    modifier = Modifier.size(24.dp),
                                )
                            },
                            trailingContent = {
                                Icon(
                                    painter = painterResource(Res.drawable.arrow_forward_ios),
                                    contentDescription = null,
                                )
                            },
                        )
                    }
                }

                // navigate to notification access permission page
                notificationToggle(notificationAccess, onUpdateNotificationAccess)

                // nuke everything
                item {
                    ListItem(
                        leadingContent = {
                            Icon(
                                painter = painterResource(Res.drawable.warning),
                                contentDescription = "Caution",
                            )
                        },
                        headlineContent = { Text(text = stringResource(Res.string.delete_all)) },
                        colors = listItemColors(),
                        trailingContent = {
                            Icon(
                                painter = painterResource(Res.drawable.arrow_forward_ios),
                                contentDescription = null,
                            )
                        },
                        modifier =
                            Modifier.clip(detachedItemShape()).clickable(
                                enabled = state.deleteButtonEnabled
                            ) {
                                deleteConfirmationDialog = true
                            },
                    )
                }

                // navigate to changelog
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        ListItem(
                            colors = listItemColors(),
                            leadingContent = {
                                Icon(
                                    painter = painterResource(Res.drawable.info),
                                    contentDescription = null,
                                )
                            },
                            supportingContent = {
                                Text(
                                    text =
                                        "Rush ${state.changelog.firstOrNull()?.version ?: "x.x.x"}"
                                )
                            },
                            trailingContent = {
                                Icon(
                                    painter = painterResource(Res.drawable.arrow_forward_ios),
                                    contentDescription = "Navigate",
                                )
                            },
                            headlineContent = { Text(text = stringResource(Res.string.about)) },
                            modifier =
                                Modifier.clip(leadingItemShape()).clickable {
                                    onNavigateToAppInfo()
                                },
                        )

                        ListItem(
                            colors = listItemColors(),
                            leadingContent = {
                                Icon(
                                    painter = painterResource(Res.drawable.check_list),
                                    contentDescription = null,
                                )
                            },
                            trailingContent = {
                                Icon(
                                    painter = painterResource(Res.drawable.arrow_forward_ios),
                                    contentDescription = "Navigate",
                                )
                            },
                            headlineContent = { Text(text = stringResource(Res.string.changelog)) },
                            modifier =
                                Modifier.clip(endItemShape()).clickable { onNavigateToChangelog() },
                        )
                    }
                }

                // pick app language
                appLanguagePicker { showLocalePicker = true }
            }
        }

        // locale picker
        if (showLocalePicker) {
            LocalePickerSheet(onDismissRequest = { showLocalePicker = false })
        }

        // dialog to confirm nuking
        if (deleteConfirmationDialog) {
            RushDialog(onDismissRequest = { deleteConfirmationDialog = false }) {
                Icon(
                    painter = painterResource(Res.drawable.warning),
                    contentDescription = "Warning",
                )
                Text(
                    text = stringResource(Res.string.delete_all),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(Res.string.delete_confirmation),
                    textAlign = TextAlign.Center,
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(
                        onClick = {
                            onAction(SettingsPageAction.OnDeleteSongs)
                            deleteConfirmationDialog = false
                        }
                    ) {
                        Text(text = stringResource(Res.string.delete_all))
                    }
                }
            }
        }
    }

expect fun LazyListScope.rushProItem(onShowPaywall: () -> Unit)

@PreviewWrapper(RushPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    SettingRootPage(
        notificationAccess = false,
        state = SettingsPageState(),
        onAction = {},
        onNavigateBack = {},
        onNavigateToLookAndFeel = {},
        onNavigateToBackup = {},
        onShowPaywall = {},
        onNavigateToChangelog = {},
        onNavigateToAppInfo = {},
        onUpdateNotificationAccess = {},
    )
}
