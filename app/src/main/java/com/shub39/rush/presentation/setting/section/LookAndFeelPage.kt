package com.shub39.rush.presentation.setting.section

import android.R.color.system_accent1_200
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.FontDownload
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Palette
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
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
import com.shub39.rush.presentation.leadingItemShape
import com.shub39.rush.presentation.listItemColors
import com.shub39.rush.presentation.middleItemShape
import com.shub39.rush.presentation.setting.SettingsPageAction
import com.shub39.rush.presentation.setting.SettingsPageState
import com.shub39.rush.presentation.toFontRes
import com.shub39.rush.presentation.toMPaletteStyle
import com.shub39.rush.presentation.toStringRes
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LookAndFeelPage(
    state: SettingsPageState,
    isProUser: Boolean,
    onAction: (SettingsPageAction) -> Unit,
    onShowPaywall: () -> Unit,
    onNavigateBack: () -> Unit
) = PageFill {
    var colorPickerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.look_and_feel)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                }
            )
        },
        modifier = Modifier.widthIn(max = 700.dp)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 16.dp,
                bottom = padding.calculateBottomPadding() + 60.dp,
                start = padding.calculateLeftPadding(LocalLayoutDirection.current) + 16.dp,
                end = padding.calculateRightPadding(LocalLayoutDirection.current) + 16.dp
            ),
        ) {
            item {
                // appTheme picker
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Column(
                        modifier = Modifier.clip(leadingItemShape())
                    ) {
                        ListItem(
                            leadingContent = {
                                Icon(
                                    imageVector = when (state.theme.appTheme) {
                                        AppTheme.SYSTEM -> {
                                            if (isSystemInDarkTheme()) Icons.Rounded.DarkMode else Icons.Rounded.LightMode
                                        }

                                        AppTheme.DARK -> Icons.Rounded.DarkMode
                                        AppTheme.LIGHT -> Icons.Rounded.LightMode
                                    },
                                    contentDescription = null
                                )
                            },
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.select_app_theme)
                                )
                            },
                            colors = listItemColors()
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(listItemColors().containerColor)
                                .padding(start = 52.dp, end = 16.dp, bottom = 8.dp)
                        ) {
                            AppTheme.entries.forEach { appTheme ->
                                ToggleButton(
                                    checked = appTheme == state.theme.appTheme,
                                    onCheckedChange = {
                                        onAction(
                                            SettingsPageAction.OnThemeSwitch(
                                                appTheme
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ToggleButtonDefaults.toggleButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                ) {
                                    Text(text = stringResource(appTheme.toStringRes()))
                                }
                            }
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.material_theme)
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(R.string.material_theme_desc)
                                )
                            },
                            trailingContent = {
                                Switch(
                                    checked = state.theme.materialTheme,
                                    onCheckedChange = {
                                        onAction(
                                            SettingsPageAction.OnMaterialThemeToggle(it)
                                        )
                                    }
                                )
                            },
                            colors = listItemColors(),
                            modifier = Modifier.clip(
                                if (isProUser) middleItemShape() else endItemShape()
                            )
                        )
                    }

                    // plus redirect
                    if (!isProUser) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .height(60.dp)
                        ) {
                            LinearWavyProgressIndicator(
                                progress = { 0.90f },
                                modifier = Modifier.fillParentMaxWidth()
                            )

                            Button(
                                onClick = onShowPaywall
                            ) {
                                Text(
                                    text = stringResource(R.string.unlock_more_pro)
                                )
                            }
                        }
                    }

                    // font picker
                    Column(
                        modifier = Modifier.clip(
                            if (isProUser) middleItemShape() else leadingItemShape()
                        )
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.font)
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Rounded.FontDownload,
                                    contentDescription = null
                                )
                            },
                            colors = listItemColors()
                        )

                        FlowRow(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(listItemColors().containerColor)
                                .padding(start = 52.dp, end = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Fonts.entries.forEach { font ->
                                ToggleButton(
                                    checked = state.theme.font == font,
                                    onCheckedChange = {
                                        onAction(SettingsPageAction.OnFontChange(font))
                                    },
                                    colors = ToggleButtonDefaults.toggleButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                ) {
                                    Text(
                                        text = font.name.lowercase()
                                            .replaceFirstChar {
                                                if (it.isLowerCase()) it.titlecase(
                                                    Locale.getDefault()
                                                ) else it.toString()
                                            }.replace("_", " "),
                                        fontFamily = FontFamily(Font(font.toFontRes()))
                                    )
                                }
                            }
                        }
                    }

                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.amoled)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(R.string.amoled_desc)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.theme.withAmoled,
                                enabled = isProUser,
                                onCheckedChange = {
                                    onAction(SettingsPageAction.OnAmoledSwitch(it))
                                }
                            )
                        },
                        colors = listItemColors(),
                        modifier = Modifier.clip(middleItemShape())
                    )

                    AnimatedVisibility(
                        visible = !state.theme.materialTheme
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.seed_color)
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(R.string.seed_color_desc)
                                )
                            },
                            trailingContent = {
                                IconButton(
                                    onClick = { colorPickerDialog = true },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color(state.theme.seedColor),
                                        contentColor = contentColorFor(Color(state.theme.seedColor))
                                    ),
                                    enabled = isProUser
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Create,
                                        contentDescription = "Select Color"
                                    )
                                }
                            },
                            colors = listItemColors(),
                            modifier = Modifier.clip(middleItemShape())
                        )
                    }

                    // palette style picker
                    Column(
                        modifier = Modifier.clip(endItemShape())
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.palette_style)
                                )
                            },
                            colors = listItemColors(),
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Rounded.Palette,
                                    contentDescription = null
                                )
                            }
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(listItemColors().containerColor)
                                .padding(start = 52.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PaletteStyle.entries.toList().forEach { style ->
                                val scheme = rememberDynamicColorScheme(
                                    primary = if (state.theme.materialTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        colorResource(system_accent1_200)
                                    } else Color(state.theme.seedColor),
                                    isDark = when (state.theme.appTheme) {
                                        AppTheme.SYSTEM -> isSystemInDarkTheme()
                                        AppTheme.DARK -> true
                                        AppTheme.LIGHT -> false
                                    },
                                    isAmoled = state.theme.withAmoled,
                                    style = style.toMPaletteStyle()
                                )
                                val selected = state.theme.style == style

                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(
                                            color = scheme.tertiary,
                                            shape = if (selected) MaterialShapes.VerySunny.toShape() else CircleShape
                                        )
                                        .clickable {
                                            onAction(SettingsPageAction.OnPaletteChange(style))
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null,
                                            tint = scheme.onTertiary
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
            onDismiss = { colorPickerDialog = false }
        )
    }
}