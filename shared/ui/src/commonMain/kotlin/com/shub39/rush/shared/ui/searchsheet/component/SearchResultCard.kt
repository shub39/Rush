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
package com.shub39.rush.shared.ui.searchsheet.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.dataclasses.SearchResult
import com.shub39.rush.shared.ui.component.ArtFromUrl
import com.shub39.rush.shared.ui.listItemColors
import org.jetbrains.compose.resources.painterResource
import rush.shared.ui.generated.resources.Res
import rush.shared.ui.generated.resources.download

@Composable
fun SearchResultCard(
    result: SearchResult,
    modifier: Modifier = Modifier,
    downloaded: Boolean = false,
) {
    ListItem(
        colors = listItemColors(),
        modifier = modifier,
        leadingContent = {
            ArtFromUrl(
                imageUrl = result.artUrl,
                modifier = Modifier.size(50.dp).clip(MaterialTheme.shapes.small),
            )
        },
        headlineContent = {
            Text(
                text = result.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
            )
        },
        supportingContent = {
            Text(text = result.artist, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        trailingContent = {
            if (downloaded) {
                Icon(
                    painter = painterResource(Res.drawable.download),
                    contentDescription = "Downloaded",
                    modifier = Modifier.size(20.dp),
                )
            }
        },
    )
}
