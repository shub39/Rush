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
package com.shub39.rush.presentation.lyrics.section

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.enums.LyricsBackground
import com.shub39.rush.presentation.components.ColorPickerDialog
import com.shub39.rush.presentation.components.RushDialog
import com.shub39.rush.presentation.components.RushTheme
import com.shub39.rush.presentation.flexFontBold
import com.shub39.rush.presentation.lyrics.LyricsPageAction
import com.shub39.rush.presentation.lyrics.LyricsPageState
import com.shub39.rush.presentation.lyrics.component.customisation.LyricsCustomisationPreview
import com.shub39.rush.presentation.lyrics.component.customisation.lyricsCustomisationSettings
import com.shub39.rush.presentation.lyrics.getCardColors
import com.shub39.rush.presentation.lyrics.getHypnoticColors
import com.shub39.rush.presentation.lyrics.getWaveColors
import io.gitlab.bpavuk.viz.VisualizerData
import io.gitlab.bpavuk.viz.VisualizerState
import io.gitlab.bpavuk.viz.rememberVisualizerState

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
    var isShowingSynced by rememberSaveable { mutableStateOf(state.sync) }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    AudioPermissionDialog(
        show = audioPermissionDialog,
        onDismiss = { audioPermissionDialog = false },
        onLaunchPermission = requestMicrophonePermission,
    )

    if (colorPickerDialog) {
        ColorPickerDialog(
            initialColor =
                if (editTarget == "content") {
                    Color(state.mCardContent)
                } else Color(state.mCardBackground),
            onSelect = {
                if (editTarget == "content") {
                    onAction(LyricsPageAction.OnUpdatemContent(it.toArgb()))
                } else {
                    onAction(LyricsPageAction.OnUpdatemBackground(it.toArgb()))
                }
            },
            onDismiss = { colorPickerDialog = false },
        )
    }

    if (!windowSizeClass.isWidthAtLeastBreakpoint(840)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.customisations),
                            fontFamily = flexFontBold(),
                            maxLines = 1,
                            modifier = Modifier.basicMarquee(),
                        )
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "Navigate Back",
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { onAction(LyricsPageAction.OnCustomisationReset) }) {
                            Icon(
                                painter = painterResource(R.drawable.refresh),
                                contentDescription = "Reset Defaults",
                            )
                        }
                    },
                )
            },
            modifier = modifier,
        ) { paddingValues ->
            LazyColumn(
                modifier =
                    Modifier.padding(top = paddingValues.calculateTopPadding()).fillMaxSize(),
                contentPadding =
                    PaddingValues(
                        start = paddingValues.calculateLeftPadding(LocalLayoutDirection.current),
                        end = paddingValues.calculateRightPadding(LocalLayoutDirection.current),
                        bottom = paddingValues.calculateBottomPadding() + 60.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                stickyHeader {
                    Column(
                        modifier =
                            Modifier.background(
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                            )
                    ) {
                        if (notificationAccess) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                ToggleButton(
                                    checked = !isShowingSynced,
                                    onCheckedChange = { isShowingSynced = false },
                                    modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                                ) {
                                    Text(text = stringResource(R.string.plain_lyrics))
                                }

                                ToggleButton(
                                    checked = isShowingSynced,
                                    onCheckedChange = { isShowingSynced = true },
                                    modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                                ) {
                                    Text(text = stringResource(R.string.synced_lyrics))
                                }
                            }
                        }

                        LyricsCustomisationPreview(
                            state = state,
                            isShowingSynced = isShowingSynced,
                            cardBackground = cardBackground,
                            cardContent = cardContent,
                            waveData = waveData,
                            hypnoticColor1 = hypnoticColor1,
                            hypnoticColor2 = hypnoticColor2,
                            waveColors = waveColors,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        )
                    }
                }

                lyricsCustomisationSettings(
                    state = state,
                    onAction = onAction,
                    isShowingSynced = isShowingSynced,
                    microphonePermission = microphonePermission,
                    onShowAudioPermissionDialog = { audioPermissionDialog = true },
                    onShowColorPickerDialog = {
                        editTarget = it
                        colorPickerDialog = true
                    },
                )
            }
        }
    } else {
        // landscape ui
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.customisations)) },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "Navigate Back",
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { onAction(LyricsPageAction.OnCustomisationReset) }) {
                            Icon(
                                painter = painterResource(R.drawable.refresh),
                                contentDescription = "Reset Defaults",
                            )
                        }
                    },
                )
            },
            modifier = modifier,
        ) { paddingValues ->
            Row(
                modifier =
                    Modifier.padding(
                            start =
                                paddingValues.calculateLeftPadding(LocalLayoutDirection.current),
                            end = paddingValues.calculateRightPadding(LocalLayoutDirection.current),
                        )
                        .fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(
                    modifier =
                        Modifier.weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(
                                top = paddingValues.calculateTopPadding() + 16.dp,
                                start = 16.dp,
                                bottom = paddingValues.calculateBottomPadding() + 16.dp,
                            ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (notificationAccess) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ToggleButton(
                                checked = !isShowingSynced,
                                onCheckedChange = { isShowingSynced = false },
                                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                            ) {
                                Text(text = stringResource(R.string.plain_lyrics))
                            }

                            ToggleButton(
                                checked = isShowingSynced,
                                onCheckedChange = { isShowingSynced = true },
                                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                            ) {
                                Text(text = stringResource(R.string.synced_lyrics))
                            }
                        }
                    }

                    LyricsCustomisationPreview(
                        state = state,
                        isShowingSynced = isShowingSynced,
                        cardBackground = cardBackground,
                        cardContent = cardContent,
                        waveData = waveData,
                        hypnoticColor1 = hypnoticColor1,
                        hypnoticColor2 = hypnoticColor2,
                        waveColors = waveColors,
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    contentPadding =
                        PaddingValues(
                            top = paddingValues.calculateTopPadding() + 16.dp,
                            end = 16.dp,
                            bottom = paddingValues.calculateBottomPadding() + 60.dp,
                        ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    lyricsCustomisationSettings(
                        state = state,
                        onAction = onAction,
                        isShowingSynced = isShowingSynced,
                        microphonePermission = microphonePermission,
                        onShowAudioPermissionDialog = { audioPermissionDialog = true },
                        onShowColorPickerDialog = {
                            editTarget = it
                            colorPickerDialog = true
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AudioPermissionDialog(
    show: Boolean = true,
    onDismiss: () -> Unit = {},
    onLaunchPermission: () -> Unit = {},
) {
    if (show) {
        RushDialog(onDismissRequest = onDismiss) {
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(painter = painterResource(R.drawable.eq), contentDescription = null)

                Text(
                    text = stringResource(R.string.audio_permission),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(
                    text = stringResource(R.string.audio_permission_info),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Button(
                    onClick = {
                        onLaunchPermission()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.grant_permission))
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(LyricsPageState(sync = true, lyricsBackground = LyricsBackground.ALBUM_ART))
    }

    val waveData =
        rememberVisualizerState().let { if (it is VisualizerState.Ready) it.fft else null }

    RushTheme(theme = Theme(appTheme = AppTheme.DARK, seedColor = Color.Red.toArgb())) {
        LyricsCustomisationsPage(
            onNavigateBack = {},
            state = state,
            onAction = {},
            notificationAccess = true,
            microphonePermission = true,
            requestMicrophonePermission = {},
            waveData = waveData,
        )
    }
}
