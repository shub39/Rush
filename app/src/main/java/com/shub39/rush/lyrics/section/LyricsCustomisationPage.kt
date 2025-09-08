package com.shub39.rush.lyrics.section

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.MeshGradient
import com.shub39.rush.R
import com.shub39.rush.core.domain.data_classes.SongUi
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.domain.enums.CardColors
import com.shub39.rush.core.domain.enums.LyricsBackground
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.ColorPickerDialog
import com.shub39.rush.core.presentation.ListSelect
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.core.presentation.SettingSlider
import com.shub39.rush.core.presentation.generateGradientColors
import com.shub39.rush.core.presentation.getRandomLine
import com.shub39.rush.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.LyricsPageState
import com.shub39.rush.lyrics.component.PlainLyric
import com.shub39.rush.lyrics.getCardColors
import com.shub39.rush.lyrics.getHypnoticColors
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LyricsCustomisationsPage(
    onNavigateBack: () -> Unit,
    state: LyricsPageState,
    onAction: (LyricsPageAction) -> Unit,
    notificationAccess: Boolean,
    modifier: Modifier = Modifier,
) {

    val (cardBackground, cardContent) = getCardColors(state)
    val (hypnoticColor1, hypnoticColor2) = getHypnoticColors(state)
    val hypnoticSpeed by animateFloatAsState(targetValue = state.meshSpeed)

    var colorPickerDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf("content") }
    var isShowingSynced by remember { mutableStateOf(state.sync) }

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
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Navigate Back",
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
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            stickyHeader {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    if (notificationAccess) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ToggleButton(
                                checked = !isShowingSynced,
                                onCheckedChange = { isShowingSynced = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(text = stringResource(R.string.plain_lyrics))
                            }

                            ToggleButton(
                                checked = isShowingSynced,
                                onCheckedChange = { isShowingSynced = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(text = stringResource(R.string.synced_lyrics))
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        if (state.lyricsBackground == LyricsBackground.ALBUM_ART) {
                            ArtFromUrl(
                                imageUrl = state.song?.artUrl,
                                modifier = Modifier
                                    .blur(80.dp)
                                    .matchParentSize()
                            )

                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        color = cardBackground.copy(alpha = 0.5f)
                                    )
                            )
                        }

                        Card(
                            modifier = Modifier
                                .let {
                                    when (state.lyricsBackground) {
                                        LyricsBackground.HYPNOTIC -> {
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
                                        }

                                        else -> it
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (state.lyricsBackground != LyricsBackground.SOLID_COLOR) Color.Transparent else cardBackground,
                                contentColor = cardContent
                            )
                        ) {
                            AnimatedContent(
                                targetState = isShowingSynced,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (it) {
                                        val lines by remember {
                                            mutableStateOf(
                                                (0..2).map { getRandomLine() }
                                            )
                                        }


                                    } else {
                                        PlainLyric(
                                            state = state,
                                            action = { },
                                            entry = 1 to "This is a very very long text depicting how lyrics should appear based on these settings",
                                            isSelected = false,
                                            hapticFeedback = null,
                                            containerColor = if (state.lyricsBackground != LyricsBackground.SOLID_COLOR) Color.Transparent else cardBackground,
                                            cardContent = cardContent,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                ListSelect(
                    title = stringResource(R.string.lyrics_background),
                    options = LyricsBackground.allBackgrounds,
                    selected = state.lyricsBackground,
                    onSelectedChange = { onAction(LyricsPageAction.OnChangeLyricsBackground(it)) },
                    labelProvider = { Text(text = stringResource(it.stringRes)) },
                )
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

            item {
                HorizontalDivider()
            }

            item {
                ListSelect(
                    title = stringResource(R.string.card_color),
                    options = CardColors.entries.toList(),
                    selected = state.cardColors,
                    onSelectedChange = { onAction(LyricsPageAction.OnUpdateColorType(it)) },
                    labelProvider = { Text(text = stringResource(it.stringRes)) },
                )

                AnimatedVisibility(
                    visible = state.cardColors == CardColors.CUSTOM,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 32.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                editTarget = "content"
                                colorPickerDialog = true
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(state.mCardContent),
                                contentColor = Color(state.mCardBackground)
                            ),
                            modifier = Modifier.weight(1f)
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
                                contentColor = Color(state.mCardContent)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Select Color"
                            )
                        }
                    }
                }
            }

            item {
                HorizontalDivider()
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
                HorizontalDivider()
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

@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            LyricsPageState(
                song = SongUi(
                    id = 0,
                    title = "Random Song",
                    artists = "shub39",
                    artUrl = "",
                    lyrics = (0..100).associateWith { "Line no $it" }.entries.toList()
                ),
                sync = true,
                lyricsBackground = LyricsBackground.ALBUM_ART,
                textAlign = TextAlign.Start
            )
        )
    }

    RushTheme(
        theme = Theme(
            appTheme = AppTheme.DARK,
            seedColor = Color.Red.toArgb()
        )
    ) {
        LyricsCustomisationsPage(
            onNavigateBack = { },
            state = state,
            onAction = { },
            notificationAccess = true
        )
    }
}