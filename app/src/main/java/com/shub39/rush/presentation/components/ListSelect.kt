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
package com.shub39.rush.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * A composable that displays a title and a list of options as toggleable buttons, allowing the user
 * to select one option from the list. It's laid out in a [FlowRow] to accommodate a variable number
 * of options.
 *
 * @param T The type of the options in the list.
 * @param title The title text to be displayed above the selection options.
 * @param options A list of all available options of type [T] to be displayed.
 * @param selected The currently selected option of type [T].
 * @param onSelectedChange A callback that is invoked when a new option is selected.
 * @param labelProvider A composable lambda that defines how to display the label for each option.
 *   It receives an option of type [T] and is expected to render its UI representation.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> ListSelect(
    title: String,
    options: List<T>,
    selected: T,
    onSelectedChange: (T) -> Unit,
    labelProvider: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        FlowRow(horizontalArrangement = Arrangement.Center) {
            options.forEach { option ->
                ToggleButton(
                    checked = option == selected,
                    onCheckedChange = { onSelectedChange(option) },
                    content = { labelProvider(option) },
                    colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }
    }
}
