package com.shub39.rush.lyrics.presentation.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.materialkolor.PaletteStyle
import com.shub39.rush.R
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.presentation.PageFill

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LookAndFeel(
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit
) = PageFill {
    var colorPickerDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.widthIn(max = 700.dp),
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
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.hypnotic_canvas)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.hypnotic_canvas_desc)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = state.theme.hypnoticCanvas,
                            onCheckedChange = {
                                action(SettingsPageAction.OnHypnoticToggle(it))
                            }
                        )
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.vibrant_colors)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.vibrant_colors_info)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = state.theme.lyricsColor == CardColors.VIBRANT.color,
                            onCheckedChange = {
                                action(SettingsPageAction.OnUpdateLyricsColor(it))
                            }
                        )
                    }
                )
            }

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
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Select Color"
                            )
                        }
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.palette_style)
                        )
                    },
                    supportingContent = {
                        FlowRow(
                            modifier = Modifier.padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PaletteStyle.entries.toList().forEach { style ->
                                FilterChip(
                                    selected = style == state.theme.style,
                                    onClick = {
                                        action(SettingsPageAction.OnPaletteChange(style))
                                    },
                                    label = {
                                        Text(
                                            text = style.name,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    if (colorPickerDialog) {
        val controller = rememberColorPickerController()

        BasicAlertDialog(
            onDismissRequest = { colorPickerDialog = false }
        ) {
            Card(
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    HsvColorPicker(
                        modifier = Modifier
                            .width(350.dp)
                            .height(300.dp)
                            .padding(top = 10.dp),
                        initialColor = Color(state.theme.seedColor),
                        controller = controller
                    )

                    BrightnessSlider(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .height(35.dp),
                        initialColor = Color(state.theme.seedColor),
                        controller = controller
                    )

                    AlphaTile(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(vertical = 10.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        controller = controller
                    )

                    Button(
                        onClick = {
                            action(SettingsPageAction.OnSeedColorChange(controller.selectedColor.value.toArgb()))
                            colorPickerDialog = false
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.done),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}