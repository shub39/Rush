/*
 * Copyright (C) 2026  Karelisio
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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.shub39.rush.shared.core.enums.BackgroundLyricsMode
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.component.ListItemCard
import com.shub39.rush.shared.ui.component.PageFill
import com.shub39.rush.shared.ui.component.SettingSlider
import com.shub39.rush.shared.ui.component.rememberNotificationPermission
import com.shub39.rush.shared.ui.detachedItemShape
import com.shub39.rush.shared.ui.endItemShape
import com.shub39.rush.shared.ui.leadingItemShape
import com.shub39.rush.shared.ui.listItemColors
import com.shub39.rush.shared.ui.segmentedListItemShapes
import com.shub39.rush.shared.ui.setting.BackgroundLyricsSettings
import com.shub39.rush.shared.ui.setting.SettingsPageAction
import com.shub39.rush.shared.ui.setting.SettingsPageState
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import com.shub39.rush.shared.ui.theme.flexFontRounded
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

/** Modes offered to the user, in display order. */
val backgroundLyricsModes =
    listOf(
        BackgroundLyricsMode.OFF,
        BackgroundLyricsMode.OVERLAY,
        BackgroundLyricsMode.NOTIFICATION,
    )

fun BackgroundLyricsMode.label(): StringResource =
    when (this) {
        BackgroundLyricsMode.OFF -> Res.string.background_mode_off
        BackgroundLyricsMode.OVERLAY -> Res.string.background_mode_overlay
        BackgroundLyricsMode.NOTIFICATION -> Res.string.background_mode_notification
    }

private fun BackgroundLyricsMode.description(): StringResource? =
    when (this) {
        BackgroundLyricsMode.OFF -> null
        BackgroundLyricsMode.OVERLAY -> Res.string.background_mode_overlay_desc
        BackgroundLyricsMode.NOTIFICATION -> Res.string.background_mode_notification_desc
    }

/** Floating lyrics window and lyrics notification settings, with the permissions they need. */
@Composable
fun BackgroundLyricsPage(
    state: SettingsPageState,
    notificationAccess: Boolean,
    onRequestNotificationAccess: () -> Unit,
    onAction: (SettingsPageAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToBattery: () -> Unit,
    modifier: Modifier = Modifier,
) =
    PageFill(modifier = modifier) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val background = state.background
        val notificationPermission = rememberNotificationPermission {}

        // Permissions are granted in the system settings, check them again when coming back
        LifecycleResumeEffect(Unit) {
            onAction(SettingsPageAction.OnRefreshBackground)
            onPauseOrDispose {}
        }

        Scaffold(
            modifier =
                Modifier.widthIn(max = 700.dp).nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumFlexibleTopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = stringResource(Res.string.background_lyrics),
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
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding =
                    PaddingValues(
                        top = padding.calculateTopPadding() + 16.dp,
                        bottom = padding.calculateBottomPadding() + 60.dp,
                        start = padding.calculateLeftPadding(LocalLayoutDirection.current) + 16.dp,
                        end = padding.calculateRightPadding(LocalLayoutDirection.current) + 16.dp,
                    ),
            ) {
                item {
                    Text(
                        text = stringResource(Res.string.background_lyrics_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 16.dp),
                    )
                }

                backgroundLyricsModes.forEachIndexed { index, mode ->
                    item(key = mode.name) {
                        ListItem(
                            onClick = {
                                if (
                                    mode != BackgroundLyricsMode.OFF &&
                                        !notificationPermission.granted
                                ) {
                                    notificationPermission.request()
                                }
                                onAction(SettingsPageAction.OnSetBackgroundMode(mode))
                            },
                            shapes = segmentedListItemShapes(index, backgroundLyricsModes.size),
                            colors = listItemColors(),
                            leadingContent = {
                                RadioButton(selected = background.mode == mode, onClick = null)
                            },
                            supportingContent =
                                mode.description()?.let { description ->
                                    { Text(text = stringResource(description)) }
                                },
                            trailingContent =
                                if (
                                    background.mode == mode &&
                                        mode != BackgroundLyricsMode.OFF &&
                                        background.running
                                ) {
                                    {
                                        SuggestionChip(
                                            onClick = {},
                                            enabled = false,
                                            label = {
                                                Text(
                                                    text =
                                                        stringResource(
                                                            Res.string.background_running
                                                        )
                                                )
                                            },
                                        )
                                    }
                                } else null,
                            content = { Text(text = stringResource(mode.label())) },
                        )
                    }
                }

                if (background.mode != BackgroundLyricsMode.OFF) {
                    if (!notificationAccess) {
                        item {
                            WarningCard(
                                title = stringResource(Res.string.notification_access_needed),
                                description = stringResource(Res.string.media_access_required),
                                action = stringResource(Res.string.grant_permission),
                                onClick = onRequestNotificationAccess,
                            )
                        }
                    }

                    if (
                        background.mode == BackgroundLyricsMode.OVERLAY &&
                            !background.overlayPermission
                    ) {
                        item {
                            WarningCard(
                                title = stringResource(Res.string.overlay_permission),
                                description = stringResource(Res.string.overlay_permission_desc),
                                action = stringResource(Res.string.open_settings),
                                onClick = {
                                    onAction(SettingsPageAction.OnRequestOverlayPermission)
                                },
                            )
                        }
                    }

                    // Without notifications, the notification mode shows nothing at all
                    if (
                        background.mode == BackgroundLyricsMode.NOTIFICATION &&
                            !notificationPermission.granted
                    ) {
                        item {
                            WarningCard(
                                title = stringResource(Res.string.notifications_blocked),
                                description = stringResource(Res.string.notifications_blocked_desc),
                                action = stringResource(Res.string.allow),
                                onClick = notificationPermission.request,
                            )
                        }
                    } else if (!notificationPermission.granted) {
                        item {
                            ListItem(
                                colors = listItemColors(),
                                modifier = Modifier.padding(top = 16.dp).clip(detachedItemShape()),
                                leadingContent = {
                                    Icon(
                                        painter = painterResource(Res.drawable.notifications),
                                        contentDescription = null,
                                    )
                                },
                                headlineContent = {
                                    Text(
                                        text =
                                            stringResource(Res.string.notifications_permission_desc)
                                    )
                                },
                                trailingContent = {
                                    TextButton(onClick = notificationPermission.request) {
                                        Text(text = stringResource(Res.string.allow))
                                    }
                                },
                            )
                        }
                    }
                }

                if (background.mode == BackgroundLyricsMode.OVERLAY) {
                    overlayAppearance(background, onAction)
                }

                item {
                    val batteryWarning =
                        background.mode != BackgroundLyricsMode.OFF &&
                            !background.ignoringBatteryOptimizations

                    ListItem(
                        onClick = onNavigateToBattery,
                        shapes = segmentedListItemShapes(0, 1),
                        colors = listItemColors(),
                        modifier = Modifier.padding(top = 24.dp),
                        leadingContent = {
                            Icon(
                                painter = painterResource(Res.drawable.battery),
                                contentDescription = null,
                                tint =
                                    if (batteryWarning) MaterialTheme.colorScheme.error
                                    else LocalContentColor.current,
                            )
                        },
                        supportingContent = {
                            Text(
                                text =
                                    stringResource(
                                        if (batteryWarning) Res.string.battery_optimization_on
                                        else Res.string.battery_help_info
                                    )
                            )
                        },
                        trailingContent = {
                            Icon(
                                painter = painterResource(Res.drawable.arrow_forward_ios),
                                contentDescription = null,
                            )
                        },
                        content = { Text(text = stringResource(Res.string.battery_help)) },
                    )
                }
            }
        }
    }

private fun androidx.compose.foundation.lazy.LazyListScope.overlayAppearance(
    background: BackgroundLyricsSettings,
    onAction: (SettingsPageAction) -> Unit,
) {
    item {
        Text(
            text = stringResource(Res.string.background_mode_overlay),
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = flexFontRounded()),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, top = 24.dp, bottom = 8.dp),
        )
    }

    item {
        ListItemCard(shape = leadingItemShape()) {
            SettingSlider(
                title = stringResource(Res.string.overlay_opacity),
                value = background.overlay.opacity,
                onValueChange = { onAction(SettingsPageAction.OnOverlayOpacityChange(it)) },
                valueToShow = "${(background.overlay.opacity * 100).roundToInt()} %",
                valueRange = 0.2f..1f,
            )
        }
    }

    item {
        ListItemCard(shape = endItemShape()) {
            SettingSlider(
                title = stringResource(Res.string.overlay_text_size),
                value = background.overlay.textScale,
                onValueChange = { onAction(SettingsPageAction.OnOverlayTextScaleChange(it)) },
                valueToShow = "×${(background.overlay.textScale * 10).roundToInt() / 10f}",
                valueRange = 0.7f..1.8f,
            )
        }
    }

    item {
        OutlinedButton(
            onClick = { onAction(SettingsPageAction.OnResetOverlayLayout) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text(text = stringResource(Res.string.reset_overlay_layout))
        }
    }
}

@Composable
private fun WarningCard(title: String, description: String, action: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onClick) { Text(text = action) }
        }
    }
}

@PreviewWrapper(RushPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    BackgroundLyricsPage(
        state =
            SettingsPageState(
                background =
                    BackgroundLyricsSettings(
                        supported = true,
                        mode = BackgroundLyricsMode.OVERLAY,
                        overlayPermission = false,
                    )
            ),
        notificationAccess = false,
        onRequestNotificationAccess = {},
        onAction = {},
        onNavigateBack = {},
        onNavigateToBattery = {},
    )
}
