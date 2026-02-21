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
package com.shub39.rush.presentation.lyrics.component.customisation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.domain.enums.CardColors
import com.shub39.rush.domain.enums.LyricsAlignment
import com.shub39.rush.presentation.allBackgrounds
import com.shub39.rush.presentation.audioDependentBackgrounds
import com.shub39.rush.presentation.blurAvailable
import com.shub39.rush.presentation.components.ListSelect
import com.shub39.rush.presentation.components.SettingSlider
import com.shub39.rush.presentation.lyrics.LyricsPageAction
import com.shub39.rush.presentation.lyrics.LyricsPageState
import com.shub39.rush.presentation.toStringRes
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
fun LazyListScope.lyricsCustomisationSettings(
    state: LyricsPageState,
    onAction: (LyricsPageAction) -> Unit,
    isShowingSynced: Boolean,
    microphonePermission: Boolean,
    onShowAudioPermissionDialog: () -> Unit,
    onShowColorPickerDialog: (target: String) -> Unit,
) {
    item {
        ListSelect(
            title = stringResource(R.string.lyrics_background),
            options = allBackgrounds,
            selected = state.lyricsBackground,
            onSelectedChange = {
                if (it !in audioDependentBackgrounds || microphonePermission) {
                    onAction(LyricsPageAction.OnChangeLyricsBackground(background = it))
                } else {
                    onShowAudioPermissionDialog()
                }
            },
            labelProvider = { Text(text = stringResource(it.toStringRes())) },
        )
    }

    item {
        AnimatedVisibility(
            visible = isShowingSynced && blurAvailable(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(R.string.blur_synced),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                trailingContent = {
                    Switch(
                        checked = state.blurSyncedLyrics,
                        onCheckedChange = { onAction(LyricsPageAction.OnBlurSyncedChange(it)) },
                    )
                },
            )
        }
    }

    item {
        SettingSlider(
            title = stringResource(R.string.text_alignment),
            value =
                when (state.textPrefs.lyricsAlignment) {
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
            valueToShow =
                when (state.textPrefs.lyricsAlignment) {
                    LyricsAlignment.CENTER -> stringResource(R.string.center)
                    LyricsAlignment.END -> stringResource(R.string.end)
                    LyricsAlignment.START -> stringResource(R.string.start)
                },
            steps = 1,
            valueRange = 0f..2f,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        SettingSlider(
            title = stringResource(R.string.font_size),
            value = state.textPrefs.fontSize,
            steps = 33,
            valueRange = 16f..50f,
            onValueChange = { onAction(LyricsPageAction.OnFontSizeChange(it)) },
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        SettingSlider(
            title = stringResource(R.string.line_height),
            value = state.textPrefs.lineHeight,
            onValueChange = { onAction(LyricsPageAction.OnLineHeightChange(it)) },
            modifier = Modifier.padding(horizontal = 16.dp),
            steps = 33,
            valueRange = 16f..50f,
        )

        SettingSlider(
            title = stringResource(R.string.letter_spacing),
            value = state.textPrefs.letterSpacing,
            onValueChange = { onAction(LyricsPageAction.OnLetterSpacingChange(it)) },
            modifier = Modifier.padding(horizontal = 16.dp),
            steps = 3,
            valueRange = -2f..2f,
        )
    }

    item { HorizontalDivider() }

    item {
        ListSelect(
            title = stringResource(R.string.card_color),
            options = CardColors.entries.toList(),
            selected = state.cardColors,
            onSelectedChange = { onAction(LyricsPageAction.OnUpdateColorType(it)) },
            labelProvider = { Text(text = stringResource(it.toStringRes())) },
        )

        AnimatedVisibility(
            visible = state.cardColors == CardColors.CUSTOM,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconButton(
                    onClick = { onShowColorPickerDialog("content") },
                    colors =
                        IconButtonDefaults.iconButtonColors(
                            containerColor = Color(state.mCardContent),
                            contentColor = Color(state.mCardBackground),
                        ),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = "Select Color",
                    )
                }

                IconButton(
                    onClick = { onShowColorPickerDialog("background") },
                    colors =
                        IconButtonDefaults.iconButtonColors(
                            containerColor = Color(state.mCardBackground),
                            contentColor = Color(state.mCardContent),
                        ),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = "Select Color",
                    )
                }
            }
        }
    }

    item { HorizontalDivider() }

    item {
        ListItem(
            headlineContent = { Text(text = stringResource(R.string.fullscreen)) },
            supportingContent = { Text(text = stringResource(R.string.fullscreen_desc)) },
            trailingContent = {
                Switch(
                    checked = state.fullscreen,
                    onCheckedChange = { onAction(LyricsPageAction.OnFullscreenChange(it)) },
                )
            },
        )

        SettingSlider(
            title = stringResource(R.string.max_lines),
            value = state.maxLines.toFloat(),
            onValueChange = { onAction(LyricsPageAction.OnMaxLinesChange(it.toInt())) },
            valueToShow = state.maxLines.toString(),
            steps = 13,
            valueRange = 2f..16f,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }

    item { HorizontalDivider() }
}
