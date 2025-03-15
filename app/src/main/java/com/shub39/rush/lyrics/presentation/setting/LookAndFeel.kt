package com.shub39.rush.lyrics.presentation.setting

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTooltipState
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.materialkolor.ktx.from
import com.materialkolor.palettes.TonalPalette
import com.materialkolor.rememberDynamicColorScheme
import com.shub39.rush.R
import com.shub39.rush.core.data.Theme
import com.shub39.rush.core.domain.Fonts
import com.shub39.rush.core.presentation.ColorPickerDialog
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.core.presentation.RushDialog
import com.shub39.rush.core.presentation.RushTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.FolderOpen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LookAndFeel(
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit
) = PageFill {
    var colorPickerDialog by remember { mutableStateOf(false) }
    var fontPickerDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.widthIn(max = 500.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.look_and_feel)
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Font Picker
            item {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(R.string.font))
                    },
                    supportingContent = {
                        Text(text = state.theme.fonts.fullName)
                    },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = { fontPickerDialog = true }
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.FolderOpen,
                                contentDescription = "Pick Font",
                                modifier = Modifier.size(24.dp)
                            )
                        }

                    }
                )
            }

            // System theme toggle
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.use_system_theme)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.use_system_theme_desc)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = state.theme.useDarkTheme == null,
                            onCheckedChange = {
                                action(
                                    SettingsPageAction.OnThemeSwitch(
                                        if (it) null else true
                                    )
                                )
                            }
                        )
                    }
                )
            }

            // Dark Theme Toggle
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.dark_theme)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = state.theme.useDarkTheme == true,
                            onCheckedChange = {
                                action(
                                    SettingsPageAction.OnThemeSwitch(it)
                                )
                            },
                            enabled = state.theme.useDarkTheme != null
                        )
                    }
                )
            }

            // Amoled variant toggle
            item {
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
                            onCheckedChange = {
                                action(
                                    SettingsPageAction.OnAmoledSwitch(it)
                                )
                            }
                        )
                    }
                )
            }

            // Material you toggle
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                item {
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
                                    action(
                                        SettingsPageAction.OnMaterialThemeToggle(it)
                                    )
                                }
                            )
                        }
                    )
                }
            }

            // Seed color picker
            item {
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
                            enabled = !state.theme.materialTheme
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Select Color"
                            )
                        }
                    }
                )
            }

            // palette styles
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.palette_style)
                        )
                    },
                    supportingContent = {
                        val scrollState = rememberScrollState()

                        Row(
                            modifier = Modifier
                                .horizontalScroll(scrollState)
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            PaletteStyle.entries.toList().forEach { style ->
                                val scheme = rememberDynamicColorScheme(
                                    primary = if (state.theme.materialTheme && Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                                        colorResource(android.R.color.system_accent1_200)
                                    } else {
                                        Color(state.theme.seedColor)
                                    },
                                    isDark = state.theme.useDarkTheme == true,
                                    isAmoled = state.theme.withAmoled,
                                    style = style
                                )

                                SelectableMiniPalette(
                                    selected = state.theme.style == style,
                                    onClick = {
                                        action(
                                            SettingsPageAction.OnPaletteChange(style = style)
                                        )
                                    },
                                    contentDescription = { style.name },
                                    accents = listOf(
                                        TonalPalette.from(scheme.primary),
                                        TonalPalette.from(scheme.tertiary),
                                        TonalPalette.from(scheme.secondary)
                                    )
                                )
                            }
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.padding(60.dp))
            }
        }
    }

    // Seed color picker
    if (colorPickerDialog) {
        ColorPickerDialog(
            initialColor = Color(state.theme.seedColor),
            onSelect = { action(SettingsPageAction.OnSeedColorChange(it.toArgb())) },
            onDismiss = { colorPickerDialog = false },
        )
    }

    // Font picker
    if (fontPickerDialog) {
        RushDialog(
            onDismissRequest = { fontPickerDialog = false }
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Fonts.entries.forEach { font ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                action(SettingsPageAction.OnFontChange(font))
                                fontPickerDialog = false
                            }
                    ) {
                        RadioButton(
                            selected = state.theme.fonts == font,
                            onClick = {
                                action(SettingsPageAction.OnFontChange(font))
                                fontPickerDialog = false
                            }
                        )

                        Text(
                            text = font.fullName,
                            fontFamily = FontFamily(Font(font.fontId))
                        )
                    }
                }
            }
        }
    }
}

// yeeted from https://github.com/SkyD666/PodAura/blob/master/app/src/main/java/com/skyd/anivu/ui/screen/settings/appearance/AppearanceScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectableMiniPalette(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    contentDescription: () -> String,
    accents: List<TonalPalette>,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.inverseOnSurface,
    ) {
        TooltipBox(
            modifier = modifier,
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Text(contentDescription())
                }
            },
            state = rememberTooltipState()
        ) {
            Surface(
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .padding(12.dp)
                    .size(50.dp),
                shape = CircleShape,
                color = Color(accents[0].tone(60))
            ) {
                Box {
                    Surface(
                        modifier = Modifier
                            .size(50.dp)
                            .offset((-25).dp, 25.dp),
                        color = Color(accents[1].tone(85)),
                    ) {}
                    Surface(
                        modifier = Modifier
                            .size(50.dp)
                            .offset(25.dp, 25.dp),
                        color = Color(accents[2].tone(75)),
                    ) {}
                    val animationSpec = spring<Float>(stiffness = Spring.StiffnessMedium)
                    AnimatedVisibility(
                        visible = selected,
                        enter = scaleIn(animationSpec) + fadeIn(animationSpec),
                        exit = scaleOut(animationSpec) + fadeOut(animationSpec),
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Checked",
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(16.dp),
                                tint = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RushTheme(
        state = Theme(
            useDarkTheme = true
        )
    ) {
        LookAndFeel(SettingsPageState()) {}
    }
}