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

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.toShape
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
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.materialkolor.rememberDynamicColorScheme
import com.shub39.rush.R
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.enums.Fonts
import com.shub39.rush.domain.enums.PaletteStyle
import com.shub39.rush.presentation.components.ColorPickerDialog
import com.shub39.rush.presentation.components.PageFill
import com.shub39.rush.presentation.endItemShape
import com.shub39.rush.presentation.flexFontEmphasis
import com.shub39.rush.presentation.leadingItemShape
import com.shub39.rush.presentation.listItemColors
import com.shub39.rush.presentation.middleItemShape
import com.shub39.rush.presentation.setting.SettingsPageAction
import com.shub39.rush.presentation.setting.SettingsPageState
import com.shub39.rush.presentation.toFontRes
import com.shub39.rush.presentation.toFullName
import com.shub39.rush.presentation.toMPaletteStyle
import com.shub39.rush.presentation.toStringRes

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
                        text = stringResource(R.string.look_and_feel),
                        fontFamily = flexFontEmphasis(),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
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
                // appTheme picker
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Column(modifier = Modifier.clip(leadingItemShape())) {
                        ListItem(
                            leadingContent = {
                                Icon(
                                    painter =
                                        painterResource(
                                            when (state.theme.appTheme) {
                                                AppTheme.SYSTEM -> {
                                                    if (isSystemInDarkTheme()) R.drawable.dark_mode
                                                    else R.drawable.light_mode
                                                }

                                                AppTheme.DARK -> R.drawable.dark_mode
                                                AppTheme.LIGHT -> R.drawable.light_mode
                                            }
                                        ),
                                    contentDescription = null,
                                )
                            },
                            headlineContent = {
                                Text(text = stringResource(R.string.select_app_theme))
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ListItem(
                            headlineContent = {
                                Text(text = stringResource(R.string.material_theme))
                            },
                            supportingContent = {
                                Text(text = stringResource(R.string.material_theme_desc))
                            },
                            trailingContent = {
                                Switch(
                                    checked = state.theme.materialTheme,
                                    onCheckedChange = {
                                        onAction(SettingsPageAction.OnMaterialThemeToggle(it))
                                    },
                                )
                            },
                            colors = listItemColors(),
                            modifier =
                                Modifier.clip(if (isProUser) middleItemShape() else endItemShape()),
                        )
                    }

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
                                Text(text = stringResource(R.string.unlock_more_pro))
                            }
                        }
                    }

                    // font picker
                    Column(
                        modifier =
                            Modifier.clip(if (isProUser) middleItemShape() else leadingItemShape())
                    ) {
                        ListItem(
                            headlineContent = { Text(text = stringResource(R.string.font)) },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.font),
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

                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.amoled)) },
                        supportingContent = { Text(text = stringResource(R.string.amoled_desc)) },
                        trailingContent = {
                            Switch(
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

                    AnimatedVisibility(
                        visible = !state.theme.materialTheme,
                        enter = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()),
                        exit = fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()),
                    ) {
                        ListItem(
                            headlineContent = { Text(text = stringResource(R.string.seed_color)) },
                            supportingContent = {
                                Text(text = stringResource(R.string.seed_color_desc))
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
                                        painter = painterResource(R.drawable.edit),
                                        contentDescription = "Select Color",
                                    )
                                }
                            },
                            colors = listItemColors(),
                            modifier = Modifier.clip(middleItemShape()),
                        )
                    }

                    // palette style picker
                    Column(modifier = Modifier.clip(endItemShape())) {
                        ListItem(
                            headlineContent = {
                                Text(text = stringResource(R.string.palette_style))
                            },
                            supportingContent = {
                                Text(
                                    text =
                                        state.theme.style.toString().lowercase().replaceFirstChar {
                                            if (it.isLowerCase())
                                                it.titlecase(LocalLocale.current.platformLocale)
                                            else it.toString()
                                        }
                                )
                            },
                            colors = listItemColors(),
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.palette),
                                    contentDescription = null,
                                )
                            },
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier =
                                Modifier.fillParentMaxWidth()
                                    .background(listItemColors().containerColor)
                                    .padding(start = 52.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            PaletteStyle.entries.toList().forEach { style ->
                                val scheme =
                                    rememberDynamicColorScheme(
                                        primary =
                                            if (
                                                state.theme.materialTheme &&
                                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                                            ) {
                                                colorResource(android.R.color.system_accent1_900)
                                            } else Color(state.theme.seedColor),
                                        isDark =
                                            when (state.theme.appTheme) {
                                                AppTheme.SYSTEM -> isSystemInDarkTheme()
                                                AppTheme.DARK -> true
                                                AppTheme.LIGHT -> false
                                            },
                                        isAmoled = state.theme.withAmoled,
                                        style = style.toMPaletteStyle(),
                                    )
                                val selected = state.theme.style == style

                                Box(
                                    modifier =
                                        Modifier.size(50.dp)
                                            .clip(
                                                if (selected) MaterialShapes.VerySunny.toShape()
                                                else CircleShape
                                            )
                                            .clickable {
                                                onAction(SettingsPageAction.OnPaletteChange(style))
                                            },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Canvas(modifier = Modifier.matchParentSize()) {
                                        val colors =
                                            listOf(
                                                scheme.primary,
                                                scheme.primaryContainer,
                                                scheme.secondary,
                                                scheme.secondaryContainer,
                                                scheme.tertiary,
                                                scheme.tertiaryContainer,
                                            )
                                        val sweepAngle = 360f / colors.size
                                        colors.forEachIndexed { index, color ->
                                            drawArc(
                                                color = color,
                                                startAngle = index * sweepAngle,
                                                sweepAngle = sweepAngle,
                                                useCenter = true,
                                            )
                                        }
                                    }

                                    Box(
                                        modifier =
                                            Modifier.matchParentSize()
                                                .background(
                                                    color =
                                                        scheme.primary.copy(
                                                            alpha = if (selected) 0.7f else 0f
                                                        )
                                                )
                                    )

                                    if (selected) {
                                        Icon(
                                            painter = painterResource(R.drawable.check),
                                            contentDescription = null,
                                            tint = scheme.onPrimary,
                                        )
                                    }
                                }
                            }
                        }
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
