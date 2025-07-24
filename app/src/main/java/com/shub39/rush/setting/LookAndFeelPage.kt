package com.shub39.rush.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.domain.enums.Fonts
import com.shub39.rush.core.presentation.ColorPickerDialog
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.core.presentation.RushDialog
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowLeft
import compose.icons.fontawesomeicons.solid.CloudSun
import compose.icons.fontawesomeicons.solid.Font

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LookAndFeelPage(
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit,
    onNavigateBack: () -> Unit
) = PageFill {
    var colorPickerDialog by remember { mutableStateOf(false) }
    var fontPickerDialog by remember { mutableStateOf(false) }
    var themePickerDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.widthIn(max = 1000.dp),
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
                            imageVector = FontAwesomeIcons.Solid.ArrowLeft,
                            contentDescription = "Navigate Back",
                            modifier = Modifier.size(20.dp)
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
                                imageVector = FontAwesomeIcons.Solid.Font,
                                contentDescription = "Pick Font",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                    }
                )
            }

            // App theme switch
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.select_app_theme)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(state.theme.appTheme.stringRes)
                        )
                    },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = { themePickerDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Pick"
                            )
                        }
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
            materialYouToggle(state, action)

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
            paletteStyles(state, action)

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

    // theme picker
    if (themePickerDialog) {
        RushDialog(
            onDismissRequest = { themePickerDialog = false }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.CloudSun,
                    contentDescription = "Select App Theme",
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = stringResource(R.string.select_app_theme),
                    style = MaterialTheme.typography.titleLarge
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    AppTheme.entries.forEach { appTheme ->
                        ToggleButton(
                            checked = state.theme.appTheme == appTheme,
                            onCheckedChange = {
                                action(SettingsPageAction.OnThemeSwitch(appTheme))
                                themePickerDialog = false
                            }
                        ) {
                            Text(text = stringResource(appTheme.stringRes))
                        }
                    }
                }
            }
        }
    }

    // Font picker
    if (fontPickerDialog) {
        RushDialog(
            onDismissRequest = { fontPickerDialog = false }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Font,
                    contentDescription = "Select App Theme",
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = stringResource(R.string.font),
                    style = MaterialTheme.typography.titleLarge
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Fonts.entries.forEach { font ->
                        ToggleButton(
                            checked = state.theme.fonts == font,
                            onCheckedChange = {
                                action(SettingsPageAction.OnFontChange(font))
                                fontPickerDialog = false
                            }
                        ) {
                            Text(
                                text = font.fullName,
                                fontFamily = FontFamily(Font(font.font))
                            )
                        }
                    }
                }
            }
        }
    }
}