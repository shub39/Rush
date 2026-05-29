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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.enums.AppTheme
import com.shub39.rush.shared.core.enums.Fonts
import com.shub39.rush.shared.ui.component.ColorPickerDialog
import com.shub39.rush.shared.ui.component.ExpressiveSwitch
import com.shub39.rush.shared.ui.component.PageFill
import com.shub39.rush.shared.ui.detachedItemShape
import com.shub39.rush.shared.ui.endItemShape
import com.shub39.rush.shared.ui.leadingItemShape
import com.shub39.rush.shared.ui.listItemColors
import com.shub39.rush.shared.ui.middleItemShape
import com.shub39.rush.shared.ui.setting.MaterialYouToggle
import com.shub39.rush.shared.ui.setting.PaletteStylePicker
import com.shub39.rush.shared.ui.setting.SettingsPageAction
import com.shub39.rush.shared.ui.setting.SettingsPageState
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import com.shub39.rush.shared.ui.toFontRes
import com.shub39.rush.shared.ui.toFullName
import com.shub39.rush.shared.ui.toStringRes
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

@Composable
fun LookAndFeelPage(
    state: SettingsPageState,
    isProUser: Boolean,
    onAction: (SettingsPageAction) -> Unit,
    onShowPaywall: () -> Unit,
    onNavigateBack: () -> Unit,
) = PageFill {
    var colorPickerDialog by remember { mutableStateOf(false) }

    val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            MediumFlexibleTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.look_and_feel),
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
                scrollBehavior = scrollBehaviour,
            )
        },
        modifier =
            Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection).widthIn(max = 700.dp),
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.animateContentSize(),
                ) {
                    // appTheme picker
                    Column(modifier = Modifier.clip(leadingItemShape())) {
                        ListItem(
                            leadingContent = {
                                Icon(
                                    painter =
                                        painterResource(
                                            when (state.theme.appTheme) {
                                                AppTheme.SYSTEM -> {
                                                    if (isSystemInDarkTheme())
                                                        Res.drawable.dark_mode
                                                    else Res.drawable.light_mode
                                                }

                                                AppTheme.DARK -> Res.drawable.dark_mode
                                                AppTheme.LIGHT -> Res.drawable.light_mode
                                            }
                                        ),
                                    contentDescription = null,
                                )
                            },
                            headlineContent = {
                                Text(text = stringResource(Res.string.select_app_theme))
                            },
                            colors = listItemColors(),
                        )

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                            modifier =
                                Modifier.fillParentMaxWidth()
                                    .background(listItemColors().containerColor)
                                    .padding(start = 52.dp, end = 16.dp, bottom = 8.dp),
                        ) {
                            AppTheme.entries.forEach { appTheme ->
                                ToggleButton(
                                    checked = appTheme == state.theme.appTheme,
                                    onCheckedChange = {
                                        onAction(SettingsPageAction.OnThemeSwitch(appTheme))
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                                ) {
                                    Text(
                                        text = stringResource(appTheme.toStringRes()),
                                        modifier = Modifier.basicMarquee(),
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }

                    // material theme
                    MaterialYouToggle(
                        checked = state.theme.materialTheme,
                        onCheckedChange = {
                            onAction(SettingsPageAction.OnMaterialThemeToggle(it))
                        },
                        modifier =
                            Modifier.clip(if (isProUser) middleItemShape() else endItemShape()),
                    )

                    // plus redirect
                    if (!isProUser) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillParentMaxWidth().height(60.dp),
                        ) {
                            LinearWavyProgressIndicator(
                                progress = { 0.90f },
                                modifier = Modifier.fillParentMaxWidth(),
                            )

                            Button(onClick = onShowPaywall) {
                                Text(text = stringResource(Res.string.unlock_more_pro))
                            }
                        }
                    }

                    // font picker
                    Column(
                        modifier =
                            Modifier.clip(
                                when {
                                    state.theme.materialTheme && !isProUser -> detachedItemShape()
                                    state.theme.materialTheme -> endItemShape()
                                    isProUser -> middleItemShape()
                                    else -> leadingItemShape()
                                }
                            )
                    ) {
                        ListItem(
                            headlineContent = { Text(text = stringResource(Res.string.font)) },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(Res.drawable.font),
                                    contentDescription = null,
                                )
                            },
                            colors = listItemColors(),
                        )

                        FlowRow(
                            modifier =
                                Modifier.fillParentMaxWidth()
                                    .background(listItemColors().containerColor)
                                    .padding(start = 52.dp, end = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Fonts.entries.forEach { font ->
                                ToggleButton(
                                    checked = state.theme.font == font,
                                    onCheckedChange = {
                                        onAction(SettingsPageAction.OnFontChange(font))
                                    },
                                    enabled = isProUser,
                                    colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                                ) {
                                    Text(
                                        text = font.toFullName(),
                                        fontFamily =
                                            font.toFontRes()?.let { FontFamily(Font(it)) }
                                                ?: FontFamily.Default,
                                    )
                                }
                            }
                        }
                    }

                    if (!state.theme.materialTheme) {
                        // amoled switch
                        ListItem(
                            headlineContent = { Text(text = stringResource(Res.string.amoled)) },
                            supportingContent = {
                                Text(text = stringResource(Res.string.amoled_desc))
                            },
                            trailingContent = {
                                ExpressiveSwitch(
                                    checked = state.theme.withAmoled,
                                    enabled = isProUser,
                                    onCheckedChange = {
                                        onAction(SettingsPageAction.OnAmoledSwitch(it))
                                    },
                                )
                            },
                            colors = listItemColors(),
                            modifier = Modifier.clip(middleItemShape()),
                        )

                        // seed color picker
                        ListItem(
                            headlineContent = {
                                Text(text = stringResource(Res.string.seed_color))
                            },
                            supportingContent = {
                                Text(text = stringResource(Res.string.seed_color_desc))
                            },
                            trailingContent = {
                                IconButton(
                                    onClick = { colorPickerDialog = true },
                                    colors =
                                        IconButtonDefaults.iconButtonColors(
                                            containerColor = Color(state.theme.seedColor),
                                            contentColor =
                                                contentColorFor(Color(state.theme.seedColor)),
                                        ),
                                    enabled = isProUser,
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.edit),
                                        contentDescription = "Select Color",
                                    )
                                }
                            },
                            colors = listItemColors(),
                            modifier = Modifier.clip(middleItemShape()),
                        )

                        // palette style picker
                        PaletteStylePicker(
                            enabled = isProUser,
                            theme = state.theme,
                            onChange = { onAction(SettingsPageAction.OnPaletteChange(it)) },
                        )
                    }
                }
            }
        }
    }

    if (colorPickerDialog) {
        ColorPickerDialog(
            initialColor = Color(state.theme.seedColor),
            onSelect = { onAction(SettingsPageAction.OnSeedColorChange(it.toArgb())) },
            onDismiss = { colorPickerDialog = false },
        )
    }
}
