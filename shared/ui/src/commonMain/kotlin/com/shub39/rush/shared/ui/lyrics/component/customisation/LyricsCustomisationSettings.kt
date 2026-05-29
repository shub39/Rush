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
package com.shub39.rush.shared.ui.lyrics.component.customisation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.enums.CardColors
import com.shub39.rush.shared.core.enums.LyricsAlignment
import com.shub39.rush.shared.ui.allBackgrounds
import com.shub39.rush.shared.ui.audioDependentBackgrounds
import com.shub39.rush.shared.ui.blurAvailable
import com.shub39.rush.shared.ui.component.ExpressiveSwitch
import com.shub39.rush.shared.ui.component.ListItemCard
import com.shub39.rush.shared.ui.component.ListSelect
import com.shub39.rush.shared.ui.component.SettingSlider
import com.shub39.rush.shared.ui.endItemShape
import com.shub39.rush.shared.ui.leadingItemShape
import com.shub39.rush.shared.ui.listItemColors
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import com.shub39.rush.shared.ui.lyrics.LyricsPageState
import com.shub39.rush.shared.ui.middleItemShape
import com.shub39.rush.shared.ui.theme.flexFontRounded
import com.shub39.rush.shared.ui.toStringRes
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

fun LazyListScope.lyricsCustomisationSettings(
    state: LyricsPageState,
    onAction: (LyricsPageAction) -> Unit,
    isShowingSynced: Boolean,
    microphonePermission: Boolean,
    onShowAudioPermissionDialog: () -> Unit,
    onShowColorPickerDialog: (target: String) -> Unit,
) {
    item {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(Res.string.looks),
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = flexFontRounded()),
            )
            Spacer(modifier = Modifier.height(8.dp))
            ListItemCard(shape = leadingItemShape()) {
                ListSelect(
                    title = stringResource(Res.string.lyrics_background),
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

            if (isShowingSynced) {
                ListItem(
                    colors = listItemColors(),
                    modifier = Modifier.clip(middleItemShape()),
                    headlineContent = {
                        Text(text = stringResource(Res.string.expressive_syllables))
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(Res.string.expressive_syllables_desc),
                            modifier = Modifier.basicMarquee(),
                        )
                    },
                    trailingContent = {
                        ExpressiveSwitch(
                            checked = state.expressiveSyllables,
                            onCheckedChange = {
                                onAction(LyricsPageAction.OnExpressiveLyricsChange(it))
                            },
                        )
                    },
                )
            }

            if (isShowingSynced && blurAvailable()) {
                ListItem(
                    colors = listItemColors(),
                    modifier = Modifier.clip(middleItemShape()),
                    headlineContent = { Text(text = stringResource(Res.string.blur_synced)) },
                    trailingContent = {
                        ExpressiveSwitch(
                            checked = state.blurSyncedLyrics,
                            onCheckedChange = { onAction(LyricsPageAction.OnBlurSyncedChange(it)) },
                        )
                    },
                )
            }

            ListItemCard(shape = endItemShape(), modifier = Modifier.animateContentSize()) {
                ListSelect(
                    title = stringResource(Res.string.card_color),
                    options = CardColors.entries.toList(),
                    selected = state.cardColors,
                    onSelectedChange = { onAction(LyricsPageAction.OnUpdateColorType(it)) },
                    labelProvider = { Text(text = stringResource(it.toStringRes())) },
                )

                if (state.cardColors == CardColors.CUSTOM) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                painter = painterResource(Res.drawable.edit),
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
                                painter = painterResource(Res.drawable.edit),
                                contentDescription = "Select Color",
                            )
                        }
                    }
                }
            }
        }
    }

    item {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(Res.string.text),
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = flexFontRounded()),
            )
            Spacer(modifier = Modifier.height(8.dp))
            ListItemCard(shape = leadingItemShape()) {
                SettingSlider(
                    title = stringResource(Res.string.text_alignment),
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
                            LyricsAlignment.CENTER -> stringResource(Res.string.center)
                            LyricsAlignment.END -> stringResource(Res.string.end)
                            LyricsAlignment.START -> stringResource(Res.string.start)
                        },
                    steps = 1,
                    valueRange = 0f..2f,
                )
            }

            ListItemCard(shape = middleItemShape()) {
                SettingSlider(
                    title = stringResource(Res.string.font_size),
                    value = state.textPrefs.fontSize,
                    steps = 33,
                    valueRange = 16f..50f,
                    onValueChange = { onAction(LyricsPageAction.OnFontSizeChange(it)) },
                )
            }

            ListItemCard(shape = middleItemShape()) {
                SettingSlider(
                    title = stringResource(Res.string.line_height),
                    value = state.textPrefs.lineHeight,
                    onValueChange = { onAction(LyricsPageAction.OnLineHeightChange(it)) },
                    steps = 33,
                    valueRange = 16f..50f,
                )
            }

            ListItemCard(shape = endItemShape()) {
                SettingSlider(
                    title = stringResource(Res.string.letter_spacing),
                    value = state.textPrefs.letterSpacing,
                    onValueChange = { onAction(LyricsPageAction.OnLetterSpacingChange(it)) },
                    steps = 3,
                    valueRange = -2f..2f,
                )
            }
        }
    }

    item {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(Res.string.others),
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = flexFontRounded()),
            )
            Spacer(modifier = Modifier.height(8.dp))

            ListItem(
                colors = listItemColors(),
                modifier = Modifier.clip(leadingItemShape()),
                headlineContent = { Text(text = stringResource(Res.string.fullscreen)) },
                supportingContent = { Text(text = stringResource(Res.string.fullscreen_desc)) },
                trailingContent = {
                    ExpressiveSwitch(
                        checked = state.fullscreen,
                        onCheckedChange = { onAction(LyricsPageAction.OnFullscreenChange(it)) },
                    )
                },
            )

            ListItemCard(shape = middleItemShape()) {
                SettingSlider(
                    title = stringResource(Res.string.max_lines),
                    value = state.maxLines.toFloat(),
                    onValueChange = { onAction(LyricsPageAction.OnMaxLinesChange(it.toInt())) },
                    valueToShow = state.maxLines.toString(),
                    steps = 13,
                    valueRange = 2f..16f,
                )
            }

            ListItem(
                colors = listItemColors(),
                modifier = Modifier.clip(endItemShape()),
                headlineContent = { Text(text = stringResource(Res.string.romanization)) },
                supportingContent = { Text(text = stringResource(Res.string.romanization_desc)) },
                trailingContent = {
                    ExpressiveSwitch(
                        checked = state.romanizationEnabled,
                        onCheckedChange = { onAction(LyricsPageAction.OnRomanizationToggle(it)) },
                    )
                },
            )
        }
    }
}
