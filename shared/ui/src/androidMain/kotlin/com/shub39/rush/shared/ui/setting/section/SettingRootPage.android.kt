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
package com.shub39.rush.shared.ui.setting.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.ui.detachedItemShape
import com.shub39.rush.shared.ui.listItemColors
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.Res
import rush.shared.ui.generated.resources.app_icon
import rush.shared.ui.generated.resources.arrow_forward_ios
import rush.shared.ui.generated.resources.rush_pro

actual fun LazyListScope.rushProItem(onShowPaywall: () -> Unit) {
    item {
        ListItem(
            headlineContent = { Text(text = stringResource(Res.string.rush_pro)) },
            colors = listItemColors(),
            modifier = Modifier.clip(detachedItemShape()).clickable { onShowPaywall() },
            trailingContent = {
                Icon(
                    painter = painterResource(Res.drawable.arrow_forward_ios),
                    contentDescription = "Rush Pro",
                )
            },
            leadingContent = {
                Icon(
                    painter = painterResource(Res.drawable.app_icon),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
        )
    }
}
