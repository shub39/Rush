package com.shub39.rush.lyrics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.materialkolor.ktx.lighten
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.MeshGradient
import com.shub39.rush.R
import com.shub39.rush.core.domain.enums.CardColors
import com.shub39.rush.core.presentation.ColorPickerDialog
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.core.presentation.SettingSlider
import com.shub39.rush.core.presentation.generateGradientColors
import com.shub39.rush.core.presentation.hypnoticAvailable
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowLeft
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsCustomisationsPage(
    onNavigateBack: () -> Unit,
    state: LyricsPageState,
    onAction: (LyricsPageAction) -> Unit,
    modifier: Modifier = Modifier,
) = PageFill {

    val (cardBackground, cardContent) = getCardColors(state)
    val (hypnoticColor1, hypnoticColor2) = getHypnoticColors(state)
    val hypnoticSpeed by animateFloatAsState(targetValue = state.meshSpeed)

    var colorPickerDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf("content") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.customisations))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.ArrowLeft,
                            contentDescription = "Navigate Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onAction(LyricsPageAction.OnCustomisationReset)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Defaults"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Card(
                        modifier = Modifier
                            .let {
                                if (state.hypnoticCanvas) {
                                    it.shaderBackground(
                                        shader = MeshGradient(
                                            colors = generateGradientColors(
                                                color1 = hypnoticColor1,
                                                color2 = hypnoticColor2,
                                                steps = 6
                                            ).toTypedArray()
                                        ),
                                        speed = hypnoticSpeed,
                                        fallback = {
                                            Brush.horizontalGradient(
                                                generateGradientColors(
                                                    color1 = hypnoticColor1,
                                                    color2 = hypnoticColor2,
                                                    steps = 6
                                                )
                                            )
                                        }
                                    )
                                } else {
                                    it
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (state.hypnoticCanvas) Color.Transparent else cardBackground,
                            contentColor = cardContent
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "This is a very very long text depicting how lyrics should appear based on these settings",
                                textAlign = state.textAlign,
                                fontWeight = FontWeight.Bold,
                                fontSize = state.fontSize.sp,
                                lineHeight = state.lineHeight.sp,
                                letterSpacing = state.letterSpacing.sp
                            )
                        }
                    }
                }
            }

            item {
                SettingSlider(
                    title = stringResource(R.string.text_alignment),
                    value = when (state.textAlign) {
                        TextAlign.Center -> 1f
                        TextAlign.End -> 2f
                        else -> 0f
                    },
                    onValueChange = {
                        onAction(
                            LyricsPageAction.OnAlignmentChange(
                                when (it.roundToInt()) {
                                    1 -> TextAlign.Center
                                    2 -> TextAlign.End
                                    else -> TextAlign.Start
                                }
                            )
                        )
                    },
                    valueToShow = when (state.textAlign) {
                        TextAlign.Center -> stringResource(R.string.center)
                        TextAlign.End -> stringResource(R.string.end)
                        else -> stringResource(R.string.start)
                    },
                    steps = 1,
                    valueRange = 0f..2f,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                SettingSlider(
                    title = stringResource(R.string.font_size),
                    value = state.fontSize,
                    steps = 33,
                    valueRange = 16f..50f,
                    onValueChange = {
                        onAction(LyricsPageAction.OnFontSizeChange(it))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                SettingSlider(
                    title = stringResource(R.string.line_height),
                    value = state.lineHeight,
                    onValueChange = {
                        onAction(LyricsPageAction.OnLineHeightChange(it))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    steps = 33,
                    valueRange = 16f..50f
                )

                SettingSlider(
                    title = stringResource(R.string.letter_spacing),
                    value = state.letterSpacing,
                    onValueChange = {
                        onAction(LyricsPageAction.OnLetterSpacingChange(it))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    steps = 3,
                    valueRange = -2f..2f
                )
            }

            if (hypnoticAvailable()) {
                item {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 32.dp))
                }

                item {
                    ListItem(
                        modifier = Modifier.clip(MaterialTheme.shapes.large),
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
                                checked = state.hypnoticCanvas,
                                onCheckedChange = { onAction(LyricsPageAction.OnHypnoticToggle(it)) }
                            )
                        }
                    )

                    SettingSlider(
                        title = stringResource(R.string.mesh_speed),
                        value = state.meshSpeed,
                        valueRange = 0.5f..3f,
                        enabled = state.hypnoticCanvas,
                        valueToShow = when (state.meshSpeed) {
                            in 0f..1f -> "<1"
                            else -> state.meshSpeed.toInt().toString()
                        },
                        onValueChange = {
                            onAction(LyricsPageAction.OnMeshSpeedChange(it))
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 32.dp))
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.use_extracted_colors)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = state.useExtractedColors,
                            onCheckedChange = { onAction(LyricsPageAction.OnToggleColorPref(it)) }
                        )
                    }
                )

                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.vibrant_colors)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = state.cardColors == CardColors.VIBRANT,
                            onCheckedChange = { onAction(LyricsPageAction.OnVibrantToggle(it)) },
                            enabled = state.useExtractedColors
                        )
                    }
                )

                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.colors)
                        )
                    },
                    trailingContent = {
                        Row {
                            IconButton(
                                onClick = {
                                    editTarget = "content"
                                    colorPickerDialog = true
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color(state.mCardContent),
                                    contentColor = Color(state.mCardContent).lighten(2f)
                                ),
                                enabled = !state.useExtractedColors
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = "Select Color",
                                )
                            }

                            IconButton(
                                onClick = {
                                    editTarget = "background"
                                    colorPickerDialog = true
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color(state.mCardBackground),
                                    contentColor = Color(state.mCardBackground).lighten(2f)
                                ),
                                enabled = !state.useExtractedColors
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = "Select Color"
                                )
                            }
                        }
                    }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 32.dp))
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.fullscreen)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.fullscreen_desc)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = state.fullscreen,
                            onCheckedChange = {
                                onAction(LyricsPageAction.OnFullscreenChange(it))
                            }
                        )
                    }
                )

                SettingSlider(
                    title = stringResource(R.string.max_lines),
                    value = state.maxLines.toFloat(),
                    onValueChange = {
                        onAction(LyricsPageAction.OnMaxLinesChange(it.toInt()))
                    },
                    valueToShow = state.maxLines.toString(),
                    steps = 13,
                    valueRange = 2f..16f,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 32.dp))
            }

            item {
                Spacer(modifier = Modifier.padding(bottom = 60.dp))
            }
        }
    }

    if (colorPickerDialog) {
        ColorPickerDialog(
            initialColor = if (editTarget == "content") {
                Color(state.mCardContent)
            } else Color(state.mCardBackground),
            onSelect = {
                if (editTarget == "content") {
                    onAction(LyricsPageAction.OnUpdatemContent(it.toArgb()))
                } else {
                    onAction(LyricsPageAction.OnUpdatemBackground(it.toArgb()))
                }
            },
            onDismiss = { colorPickerDialog = false }
        )
    }

}