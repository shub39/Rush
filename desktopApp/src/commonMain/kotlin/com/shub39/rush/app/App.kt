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
package com.shub39.rush.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.ui.component.FossPaywall
import com.shub39.rush.shared.ui.component.PageFill
import com.shub39.rush.shared.ui.navigation.RushNavDisplay

@Composable
fun App() {
    RushNavDisplay(
        paywall = { _, _ ->
            PageFill(Modifier.background(MaterialTheme.colors.surface)) {
                FossPaywall(Modifier.widthIn(max = 600.dp))
            }
        }
    )
}
