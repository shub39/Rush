package com.shub39.rush.presentation.lyrics.section

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material3.Button
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.R
import com.shub39.rush.domain.dataclasses.Lyric
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.enums.CardColors
import com.shub39.rush.domain.enums.LyricsAlignment
import com.shub39.rush.domain.enums.LyricsBackground
import com.shub39.rush.presentation.blurAvailable
import com.shub39.rush.presentation.components.ColorPickerDialog
import com.shub39.rush.presentation.components.ListSelect
import com.shub39.rush.presentation.components.RushDialog
import com.shub39.rush.presentation.components.RushTheme
import com.shub39.rush.presentation.components.SettingSlider
import com.shub39.rush.presentation.getRandomLine
import com.shub39.rush.presentation.lyrics.ApplyLyricsBackground
import com.shub39.rush.presentation.lyrics.LyricsPageAction
import com.shub39.rush.presentation.lyrics.LyricsPageState
import com.shub39.rush.presentation.lyrics.LyricsState
import com.shub39.rush.presentation.lyrics.component.PlainLyric
import com.shub39.rush.presentation.lyrics.component.SyncedLyric
import com.shub39.rush.presentation.lyrics.getCardColors
import com.shub39.rush.presentation.lyrics.getHypnoticColors
import com.shub39.rush.presentation.lyrics.getWaveColors
import io.gitlab.bpavuk.viz.VisualizerData
import io.gitlab.bpavuk.viz.VisualizerState
import io.gitlab.bpavuk.viz.rememberVisualizerState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LyricsCustomisationsPage(
    onNavigateBack: () -> Unit,
    state: LyricsPageState,
    onAction: (LyricsPageAction) -> Unit,
    notificationAccess: Boolean,
    microphonePermission: Boolean,
    requestMicrophonePermission: () -> Unit,
    waveData: VisualizerData?,
    modifier: Modifier = Modifier,
) {
    val (cardBackground, cardContent) = getCardColors(state)
    val (hypnoticColor1, hypnoticColor2) = getHypnoticColors(state)
    val waveColors = getWaveColors(state)

    var colorPickerDialog by remember { mutableStateOf(false) }
    var audioPermissionDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf("content") }
    var isShowingSynced by remember { mutableStateOf(state.sync) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.customisations))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
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
                Column(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            shape = RoundedCornerShape(
                                bottomStart = 32.dp,
                                bottomEnd = 32.dp
                            )
                        )
                ) {
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

                    Card(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = cardBackground,
                            contentColor = cardContent
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        BoxWithConstraints {
                            ApplyLyricsBackground(
                                background = state.lyricsBackground,
                                artUrl = (state.lyricsState as? LyricsState.Loaded)?.song?.artUrl,
                                cardBackground = cardBackground,
                                waveData = waveData,
                                waveColors = waveColors,
                                hypnoticColor1 = hypnoticColor1,
                                hypnoticColor2 = hypnoticColor2
                            )

                            AnimatedContent(
                                targetState = isShowingSynced,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(with(LocalDensity.current) { state.textPrefs.lineHeight.sp.toDp() / 2 })
                                ) {
                                    if (it) {
                                        val lines by remember {
                                            mutableStateOf((1..2).map { getRandomLine() })
                                        }

                                        SyncedLyric(
                                            textPrefs = state.textPrefs,
                                            blur = if (state.blurSyncedLyrics) 2.dp else 0.dp,
                                            action = {},
                                            lyric = Lyric(1L, lines.first()),
                                            hapticFeedback = null,
                                            glowAlpha = 0.2f,
                                            textColor = cardContent,
                                            animatedProgress = 1f
                                        )
                                        SyncedLyric(
                                            textPrefs = state.textPrefs,
                                            blur = 0.dp,
                                            action = {},
                                            lyric = Lyric(1L, lines.last()),
                                            hapticFeedback = null,
                                            glowAlpha = 0.5f,
                                            textColor = cardContent,
                                            animatedProgress = 0.5f
                                        )
                                        SyncedLyric(
                                            textPrefs = state.textPrefs,
                                            blur = 0.dp,
                                            action = {},
                                            lyric = Lyric(1L, ""),
                                            hapticFeedback = null,
                                            glowAlpha = 0.5f,
                                            textColor = cardContent,
                                            animatedProgress = 0.5f
                                        )
                                    } else {
                                        PlainLyric(
                                            entry = 1 to "This is a very very long text depicting how lyrics should appear based on these settings",
                                            textPrefs = state.textPrefs,
                                            onClick = { },
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
                    onSelectedChange = {
                        if (it !in LyricsBackground.audioDependentBackgrounds || microphonePermission) {
                            onAction(LyricsPageAction.OnChangeLyricsBackground(background = it))
                        } else {
                            audioPermissionDialog = true
                        }
                    },
                    labelProvider = { Text(text = stringResource(it.stringRes)) },
                )
            }

            item {
                AnimatedVisibility(
                    visible = isShowingSynced && blurAvailable(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.blur_synced),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.blurSyncedLyrics,
                                onCheckedChange = {
                                    onAction(LyricsPageAction.OnBlurSyncedChange(it))
                                }
                            )
                        }
                    )
                }
            }

            item {
                SettingSlider(
                    title = stringResource(R.string.text_alignment),
                    value = when (state.textPrefs.lyricsAlignment) {
                        LyricsAlignment.CENTER -> 1f
                        LyricsAlignment.END -> 2f
                        LyricsAlignment.START -> 0f
                    },
                    onValueChange = {
                        onAction(
                            LyricsPageAction.OnAlignmentChange(
                                when (it.roundToInt()) {
                                    1 -> LyricsAlignment.CENTER
                                    2 -> LyricsAlignment.END
                                    else -> LyricsAlignment.START
                                }
                            )
                        )
                    },
                    valueToShow = when (state.textPrefs.lyricsAlignment) {
                        LyricsAlignment.CENTER -> stringResource(R.string.center)
                        LyricsAlignment.END -> stringResource(R.string.end)
                        LyricsAlignment.START -> stringResource(R.string.start)
                    },
                    steps = 1,
                    valueRange = 0f..2f,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                SettingSlider(
                    title = stringResource(R.string.font_size),
                    value = state.textPrefs.fontSize,
                    steps = 33,
                    valueRange = 16f..50f,
                    onValueChange = {
                        onAction(LyricsPageAction.OnFontSizeChange(it))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                SettingSlider(
                    title = stringResource(R.string.line_height),
                    value = state.textPrefs.lineHeight,
                    onValueChange = {
                        onAction(LyricsPageAction.OnLineHeightChange(it))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    steps = 33,
                    valueRange = 16f..50f
                )

                SettingSlider(
                    title = stringResource(R.string.letter_spacing),
                    value = state.textPrefs.letterSpacing,
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

    AudioPermissionDialog(
        show = audioPermissionDialog,
        onDismiss = { audioPermissionDialog = false },
        onLaunchPermission = requestMicrophonePermission
    )

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
private fun AudioPermissionDialog(
    show: Boolean = true,
    onDismiss: () -> Unit = {},
    onLaunchPermission: () -> Unit = {}
) {
    if (show) {
        RushDialog(
            onDismissRequest = onDismiss
        ) {
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Audiotrack,
                    contentDescription = null
                )

                Text(
                    text = stringResource(R.string.audio_permission),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = stringResource(R.string.audio_permission_info),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = {
                        onLaunchPermission()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.grant_permission)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            LyricsPageState(
                sync = true,
                lyricsBackground = LyricsBackground.ALBUM_ART
            )
        )
    }

    val waveData = rememberVisualizerState().let {
        if (it is VisualizerState.Ready) it.fft else null
    }

    RushTheme(
        theme = Theme(
            appTheme = AppTheme.DARK,
            seedColor = Color.Red.toArgb().toLong()
        )
    ) {
        LyricsCustomisationsPage(
            onNavigateBack = { },
            state = state,
            onAction = { },
            notificationAccess = true,
            microphonePermission = true,
            requestMicrophonePermission = { },
            waveData = waveData
        )
    }
}