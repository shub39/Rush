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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.ui.component.ExpressiveSwitch
import com.shub39.rush.shared.ui.component.ListItemCard
import com.shub39.rush.shared.ui.component.SettingSlider
import com.shub39.rush.shared.ui.endItemShape
import com.shub39.rush.shared.ui.leadingItemShape
import com.shub39.rush.shared.ui.listItemColors
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import com.shub39.rush.shared.ui.lyrics.LyricsPageState
import com.shub39.rush.shared.ui.middleItemShape
import com.shub39.rush.shared.ui.theme.flexFontRounded
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.Res
import rush.shared.ui.generated.resources.fullscreen
import rush.shared.ui.generated.resources.fullscreen_desc
import rush.shared.ui.generated.resources.max_lines
import rush.shared.ui.generated.resources.others
import rush.shared.ui.generated.resources.romanization
import rush.shared.ui.generated.resources.romanization_desc

actual fun LazyListScope.otherOptions(
    state: LyricsPageState,
    onAction: (LyricsPageAction) -> Unit,
) {
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
