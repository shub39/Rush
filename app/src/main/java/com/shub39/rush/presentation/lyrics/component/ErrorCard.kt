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
package com.shub39.rush.presentation.lyrics.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.presentation.copyToClipboard
import kotlinx.coroutines.launch

@Composable
fun ErrorCard(error: Int, debugMessage: String?, colors: Pair<Color, Color>) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.warning),
            contentDescription = "Error",
            modifier = Modifier.size(128.dp).padding(16.dp),
            tint = colors.first,
        )

        Text(text = stringResource(error), color = colors.first)

        AnimatedVisibility(visible = debugMessage != null) {
            TextButton(
                onClick = {
                    scope.launch { clipboard.copyToClipboard(text = debugMessage ?: "i am dumb") }
                },
                modifier = Modifier.padding(top = 4.dp),
                colors =
                    ButtonDefaults.textButtonColors(
                        contentColor = colors.first,
                        containerColor = colors.second,
                    ),
            ) {
                Text(text = stringResource(R.string.copy_error))
            }
        }
    }
}
