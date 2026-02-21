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
package com.shub39.rush.presentation.saved.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.presentation.saved.SavedPageAction
import com.shub39.rush.presentation.saved.SavedPageState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SavedPageActions(
    state: SavedPageState,
    notificationAccess: Boolean,
    onAction: (SavedPageAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (notificationAccess) {
            FloatingActionButton(
                onClick = {
                    onAction(SavedPageAction.OnToggleAutoChange)
                    if (!state.autoChange) {
                        onNavigateToLyrics()
                    }
                },
                shape = MaterialShapes.Sunny.toShape(),
                containerColor =
                    if (state.autoChange) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.onSecondary,
                contentColor =
                    if (state.autoChange) MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.secondary,
            ) {
                Icon(
                    painter = painterResource(R.drawable.meteor),
                    contentDescription = "Rush Mode",
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        MediumFloatingActionButton(onClick = { onAction(SavedPageAction.OnToggleSearchSheet) }) {
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = "Search",
                modifier = Modifier.size(40.dp),
            )
        }
    }
}
