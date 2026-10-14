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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.component.ListItemCard
import com.shub39.rush.shared.ui.component.PageFill
import com.shub39.rush.shared.ui.endItemShape
import com.shub39.rush.shared.ui.leadingItemShape
import com.shub39.rush.shared.ui.middleItemShape
import com.shub39.rush.shared.ui.setting.BackgroundLyricsSettings
import com.shub39.rush.shared.ui.setting.SettingsPageAction
import com.shub39.rush.shared.ui.setting.SettingsPageState
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

/** Manufacturers with a page on dontkillmyapp.com, named like Android names them. */
private val dontKillMyAppVendors =
    setOf(
        "asus",
        "google",
        "honor",
        "huawei",
        "lenovo",
        "meizu",
        "motorola",
        "nokia",
        "oneplus",
        "oppo",
        "realme",
        "samsung",
        "sony",
        "vivo",
        "xiaomi",
    )

/** Whether the phone runs MagicOS or EMUI, whose app launch manager stops background apps. */
private fun BackgroundLyricsSettings.isHonor(): Boolean =
    manufacturer.lowercase() in setOf("honor", "huawei")

/** How to keep the background lyrics alive on phones closing apps to save battery. */
@Composable
fun BatteryHelpPage(
    state: SettingsPageState,
    onAction: (SettingsPageAction) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) =
    PageFill(modifier = modifier) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val background = state.background
        val uriHandler = LocalUriHandler.current

        // The battery optimization is changed in the system settings, check it when coming back
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
                            text = stringResource(Res.string.battery_help),
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
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 16.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.battery_help_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (background.manufacturer.isNotBlank()) {
                            Text(
                                text =
                                    stringResource(
                                        Res.string.battery_detected,
                                        background.manufacturer.replaceFirstChar { it.uppercase() },
                                    ),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }

                item {
                    Step(
                        number = 1,
                        title = stringResource(Res.string.battery_step_optimization),
                        description = stringResource(Res.string.battery_step_optimization_desc),
                        shape = leadingItemShape(),
                        done = background.ignoringBatteryOptimizations,
                        action = stringResource(Res.string.battery_turn_off),
                        onClick = {
                            onAction(SettingsPageAction.OnRequestIgnoreBatteryOptimizations)
                        },
                    )
                }

                item {
                    Step(
                        number = 2,
                        title = stringResource(Res.string.battery_step_autostart),
                        description =
                            if (background.isHonor()) {
                                stringResource(Res.string.battery_step_autostart_honor)
                            } else stringResource(Res.string.battery_step_autostart_desc),
                        shape = middleItemShape(),
                        action = stringResource(Res.string.open_settings),
                        onClick = { onAction(SettingsPageAction.OnOpenAutoStartSettings) },
                    )
                }

                item {
                    Step(
                        number = 3,
                        title = stringResource(Res.string.battery_step_lock),
                        description = stringResource(Res.string.battery_step_lock_desc),
                        shape = middleItemShape(),
                    )
                }

                item {
                    Step(
                        number = 4,
                        title = stringResource(Res.string.battery_step_power_saving),
                        description = stringResource(Res.string.battery_step_power_saving_desc),
                        shape = endItemShape(),
                    )
                }

                item {
                    OutlinedButton(
                        onClick = {
                            val vendor =
                                background.manufacturer.lowercase().takeIf {
                                    it in dontKillMyAppVendors
                                }
                            runCatching {
                                uriHandler.openUri("https://dontkillmyapp.com/${vendor.orEmpty()}")
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    ) {
                        Text(text = stringResource(Res.string.battery_more_tips))
                    }
                }
            }
        }
    }

/** A numbered step, with a check mark once done and an optional button to do it. */
@Composable
private fun Step(
    number: Int,
    title: String,
    description: String,
    shape: Shape,
    done: Boolean = false,
    action: String? = null,
    onClick: () -> Unit = {},
) {
    ListItemCard(shape = shape) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier.size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (done) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondaryContainer
                        ),
            ) {
                if (done) {
                    Icon(
                        painter = painterResource(Res.drawable.check),
                        contentDescription = stringResource(Res.string.battery_step_done),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                } else {
                    Text(
                        text = number.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f),
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                when {
                    done ->
                        Text(
                            text = stringResource(Res.string.battery_step_done),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    action != null ->
                        FilledTonalButton(
                            onClick = onClick,
                            modifier = Modifier.padding(top = 4.dp),
                        ) {
                            Text(text = action)
                        }
                }
            }
        }
    }
}

@PreviewWrapper(RushPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    BatteryHelpPage(
        state =
            SettingsPageState(
                background =
                    BackgroundLyricsSettings(
                        supported = true,
                        ignoringBatteryOptimizations = true,
                        manufacturer = "HONOR",
                    )
            ),
        onAction = {},
        onNavigateBack = {},
    )
}
