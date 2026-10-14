/*
 * Copyright (C) 2026  Karelisio
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
package com.shub39.rush.shared.ui.setting.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.dataclasses.LyricsSourceSetting
import com.shub39.rush.shared.core.enums.LyricsProviderId
import com.shub39.rush.shared.core.enums.LyricsQuality
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.component.ExpressiveSwitch
import com.shub39.rush.shared.ui.component.PageFill
import com.shub39.rush.shared.ui.listItemColors
import com.shub39.rush.shared.ui.segmentedListItemShapes
import com.shub39.rush.shared.ui.setting.SettingsPageAction
import com.shub39.rush.shared.ui.setting.SettingsPageState
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

/** Enable, disable and order the lyrics sources used by the cascade. */
@Composable
fun LyricsSourcesPage(
    state: SettingsPageState,
    onAction: (SettingsPageAction) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) =
    PageFill(modifier = modifier) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val enabledCount = state.lyricsSources.count { it.enabled }

        Scaffold(
            modifier =
                Modifier.widthIn(max = 700.dp).nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumFlexibleTopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = stringResource(Res.string.lyrics_sources),
                            fontFamily = flexFontEmphasis(),
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                painter = painterResource(Res.drawable.arrow_back),
                                contentDescription = "Navigate Back",
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { onAction(SettingsPageAction.OnResetLyricsSources) }
                        ) {
                            Text(text = stringResource(Res.string.reset_order))
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            scrolledContainerColor = MaterialTheme.colorScheme.surface
                        ),
                )
            },
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding =
                    PaddingValues(
                        top = padding.calculateTopPadding() + 16.dp,
                        bottom = padding.calculateBottomPadding() + 60.dp,
                        start = padding.calculateLeftPadding(LocalLayoutDirection.current) + 16.dp,
                        end = padding.calculateRightPadding(LocalLayoutDirection.current) + 16.dp,
                    ),
            ) {
                item {
                    Text(
                        text = stringResource(Res.string.lyrics_sources_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 16.dp),
                    )
                }

                itemsIndexed(
                    items = state.lyricsSources,
                    key = { _, source -> source.provider.name },
                ) { index, source ->
                    // The last enabled source can not be disabled
                    val canToggle = !source.enabled || enabledCount > 1

                    ListItem(
                        onClick = {
                            if (canToggle) {
                                onAction(
                                    SettingsPageAction.OnToggleLyricsSource(
                                        source.provider,
                                        !source.enabled,
                                    )
                                )
                            }
                        },
                        shapes = segmentedListItemShapes(index, state.lyricsSources.size),
                        colors = listItemColors(),
                        leadingContent = {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.titleMedium,
                                color =
                                    if (source.enabled) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline,
                            )
                        },
                        supportingContent = {
                            Text(
                                text =
                                    stringResource(
                                        if (source.provider.maxQuality == LyricsQuality.SYLLABLE) {
                                            Res.string.source_capability_syllable
                                        } else Res.string.source_capability_line
                                    )
                            )
                        },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        onAction(
                                            SettingsPageAction.OnMoveLyricsSource(
                                                source.provider,
                                                -1,
                                            )
                                        )
                                    },
                                    enabled = index > 0,
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.arrow_upward),
                                        contentDescription = stringResource(Res.string.move_up),
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onAction(
                                            SettingsPageAction.OnMoveLyricsSource(
                                                source.provider,
                                                1,
                                            )
                                        )
                                    },
                                    enabled = index < state.lyricsSources.lastIndex,
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.arrow_downward),
                                        contentDescription = stringResource(Res.string.move_down),
                                    )
                                }
                                ExpressiveSwitch(
                                    checked = source.enabled,
                                    enabled = canToggle,
                                    onCheckedChange = {
                                        onAction(
                                            SettingsPageAction.OnToggleLyricsSource(
                                                source.provider,
                                                it,
                                            )
                                        )
                                    },
                                )
                            }
                        },
                        modifier = Modifier.animateItem(),
                        content = { Text(text = source.provider.displayName) },
                    )
                }
            }
        }
    }

@PreviewWrapper(RushPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    LyricsSourcesPage(
        state =
            SettingsPageState(
                lyricsSources =
                    LyricsProviderId.entries.mapIndexed { index, provider ->
                        LyricsSourceSetting(provider, index != 2)
                    }
            ),
        onAction = {},
        onNavigateBack = {},
    )
}
