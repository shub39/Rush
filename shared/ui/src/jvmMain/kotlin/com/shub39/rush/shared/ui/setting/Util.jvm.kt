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
package com.shub39.rush.shared.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.materialkolor.rememberDynamicColorScheme
import com.shub39.rush.shared.core.dataclasses.Theme
import com.shub39.rush.shared.core.enums.AppTheme
import com.shub39.rush.shared.core.enums.PaletteStyle
import com.shub39.rush.shared.ui.component.ListSelect
import com.shub39.rush.shared.ui.endItemShape
import com.shub39.rush.shared.ui.listItemColors
import com.shub39.rush.shared.ui.toMPaletteStyle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.Res
import rush.shared.ui.generated.resources.palette
import rush.shared.ui.generated.resources.palette_style

@Composable
actual fun ColumnScope.MaterialYouToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier,
) {}

@Composable
actual fun ColumnScope.PaletteStylePicker(
    modifier: Modifier,
    theme: Theme,
    onChange: (PaletteStyle) -> Unit,
) {
    Column(modifier = modifier.clip(endItemShape())) {
        ListItem(
            headlineContent = { Text(text = stringResource(Res.string.palette_style)) },
            supportingContent = {
                Text(text = theme.style.toString().lowercase().replaceFirstChar { it.titlecase() })
            },
            colors = listItemColors(),
            leadingContent = {
                Icon(painter = painterResource(Res.drawable.palette), contentDescription = null)
            },
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier =
                Modifier.fillMaxWidth()
                    .background(listItemColors().containerColor)
                    .padding(start = 52.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ListSelect(
                title = null,
                options = PaletteStyle.entries.toList(),
                selected = theme.style,
                onSelectedChange = onChange,
                labelProvider = { style ->
                    val scheme =
                        rememberDynamicColorScheme(
                            primary = Color(theme.seedColor),
                            isDark =
                                when (theme.appTheme) {
                                    AppTheme.SYSTEM -> isSystemInDarkTheme()
                                    AppTheme.DARK -> true
                                    AppTheme.LIGHT -> false
                                },
                            isAmoled = theme.withAmoled,
                            style = style.toMPaletteStyle(),
                        )

                    Row(
                        modifier =
                            Modifier.size(width = 56.dp, height = 24.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                    ) {
                        listOf(
                                scheme.primary,
                                scheme.primaryContainer,
                                scheme.onPrimary,
                                scheme.secondary,
                                scheme.secondaryContainer,
                                scheme.onSecondary,
                                scheme.tertiary,
                                scheme.tertiaryContainer,
                                scheme.onTertiary,
                                scheme.surface,
                                scheme.onSurface,
                            )
                            .forEach { color ->
                                Box(
                                    modifier = Modifier.weight(1f).fillMaxHeight().background(color)
                                )
                            }
                    }
                },
            )
        }
    }
}

actual fun LazyListScope.notificationToggle(
    notificationAccess: Boolean,
    onUpdateNotificationAccess: () -> Unit,
) {}

actual fun LazyListScope.appLanguagePicker(onClick: () -> Unit) {}
