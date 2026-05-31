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

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shub39.rush.shared.core.dataclasses.Theme
import com.shub39.rush.shared.core.enums.PaletteStyle

@Composable
expect fun ColumnScope.MaterialYouToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
)

@Composable
expect fun ColumnScope.PaletteStylePicker(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    theme: Theme,
    onChange: (PaletteStyle) -> Unit,
)

expect fun LazyListScope.notificationToggle(
    notificationAccess: Boolean,
    onUpdateNotificationAccess: () -> Unit
)

expect fun LazyListScope.appLanguagePicker(onClick: () -> Unit)
