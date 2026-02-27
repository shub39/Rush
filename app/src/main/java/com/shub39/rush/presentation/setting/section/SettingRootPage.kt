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
package com.shub39.rush.presentation.setting.section

import android.content.Intent
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.presentation.components.PageFill
import com.shub39.rush.presentation.components.RushDialog
import com.shub39.rush.presentation.components.RushTheme
import com.shub39.rush.presentation.detachedItemShape
import com.shub39.rush.presentation.endItemShape
import com.shub39.rush.presentation.flexFontBold
import com.shub39.rush.presentation.flexFontEmphasis
import com.shub39.rush.presentation.flexFontRounded
import com.shub39.rush.presentation.getRandomLine
import com.shub39.rush.presentation.leadingItemShape
import com.shub39.rush.presentation.listItemColors
import com.shub39.rush.presentation.setting.SettingsPageAction
import com.shub39.rush.presentation.setting.SettingsPageState
import com.shub39.rush.presentation.setting.component.AboutApp

// topmost settings page
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingRootPage(
    notificationAccess: Boolean,
    state: SettingsPageState,
    onShowPaywall: () -> Unit,
    onAction: (SettingsPageAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLookAndFeel: () -> Unit,
    onNavigateToBackup: () -> Unit,
) = PageFill {
    var deleteConfirmationDialog by remember { mutableStateOf(false) }

    val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier =
            Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection).widthIn(max = 700.dp),
        topBar = {
            LargeFlexibleTopAppBar(
                scrollBehavior = scrollBehaviour,
                title = {
                    Text(text = stringResource(R.string.settings), fontFamily = flexFontEmphasis())
                },
                subtitle = { Text(text = getRandomLine(), fontFamily = flexFontRounded()) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
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
                        paddingValues.calculateLeftPadding(LocalLayoutDirection.current) + 16.dp,
                    end = paddingValues.calculateRightPadding(LocalLayoutDirection.current) + 16.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // about app
            item { AboutApp() }

            // Pro
            item {
                Card(
                    onClick = onShowPaywall,
                    shape = MaterialTheme.shapes.extraLarge,
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.add),
                            contentDescription = "Rush Pro",
                            modifier = Modifier.size(24.dp),
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.rush_pro),
                            style =
                                MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = flexFontBold()
                                ),
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            painter = painterResource(R.drawable.arrow_forward_ios),
                            contentDescription = "Grit Plus",
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    // navigate to look and feel
                    ListItem(
                        modifier =
                            Modifier.clip(leadingItemShape()).clickable {
                                onNavigateToLookAndFeel()
                            },
                        colors = listItemColors(),
                        headlineContent = { Text(text = stringResource(R.string.look_and_feel)) },
                        supportingContent = {
                            Text(
                                text = stringResource(R.string.look_and_feel_info),
                                maxLines = 1,
                                modifier = Modifier.basicMarquee(),
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.palette),
                                contentDescription = "Navigate",
                                modifier = Modifier.size(24.dp),
                            )
                        },
                        trailingContent = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_forward_ios),
                                contentDescription = null,
                            )
                        },
                    )

                    // navigate to backup
                    ListItem(
                        modifier = Modifier.clip(endItemShape()).clickable { onNavigateToBackup() },
                        colors = listItemColors(),
                        headlineContent = { Text(text = stringResource(R.string.backup)) },
                        supportingContent = {
                            Text(
                                text = stringResource(R.string.backup_info),
                                maxLines = 1,
                                modifier = Modifier.basicMarquee(),
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.upload_file),
                                contentDescription = "Backup",
                                modifier = Modifier.size(24.dp),
                            )
                        },
                        trailingContent = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_forward_ios),
                                contentDescription = null,
                            )
                        },
                    )
                }
            }

            // navigate to notification access permission page
            if (!notificationAccess) {
                item {
                    val context = LocalContext.current
                    val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")

                    ListItem(
                        headlineContent = {
                            Text(text = stringResource(R.string.grant_permission))
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(R.string.notification_permission),
                                maxLines = 1,
                                modifier = Modifier.basicMarquee(),
                            )
                        },
                        colors = listItemColors(),
                        modifier =
                            Modifier.clip(detachedItemShape()).clickable {
                                context.startActivity(intent)
                            },
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.notifications),
                                contentDescription = null,
                            )
                        },
                        trailingContent = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_forward_ios),
                                contentDescription = null,
                            )
                        },
                    )
                }
            }

            // nuke everything
            item {
                ListItem(
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.warning),
                            contentDescription = "Caution",
                        )
                    },
                    headlineContent = { Text(text = stringResource(R.string.delete_all)) },
                    colors = listItemColors(),
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.arrow_forward_ios),
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
        }
    }

    // dialog to confirm nuking
    if (deleteConfirmationDialog) {
        RushDialog(onDismissRequest = { deleteConfirmationDialog = false }) {
            Icon(painter = painterResource(R.drawable.warning), contentDescription = "Warning")
            Text(
                text = stringResource(R.string.delete_all),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(text = stringResource(R.string.delete_confirmation), textAlign = TextAlign.Center)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(
                    onClick = {
                        onAction(SettingsPageAction.OnDeleteSongs)
                        deleteConfirmationDialog = false
                    }
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
    RushTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        SettingRootPage(
            notificationAccess = false,
            state = SettingsPageState(),
            onAction = {},
            onNavigateBack = {},
            onNavigateToLookAndFeel = {},
            onNavigateToBackup = {},
            onShowPaywall = {},
        )
    }
}
