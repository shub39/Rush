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
package com.shub39.rush.presentation.setting.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.BuildConfig
import com.shub39.rush.R
import com.shub39.rush.presentation.flexFontEmphasis

@Composable
fun AboutApp() {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column {
                Text(
                    text = "Rush",
                    style =
                        MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = flexFontEmphasis()
                        ),
                )
                Text(text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            }

            Spacer(modifier = Modifier.weight(1f))

            Row {
                IconButton(onClick = { uriHandler.openUri("https://discord.gg/nxA2hgtEKf") }) {
                    Icon(
                        painter = painterResource(R.drawable.discord),
                        contentDescription = "Discord",
                        modifier = Modifier.size(24.dp),
                    )
                }

                IconButton(onClick = { uriHandler.openUri("https://github.com/shub39/Rush") }) {
                    Icon(
                        painter = painterResource(R.drawable.github),
                        contentDescription = "Github",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }

        FlowRow(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(onClick = { uriHandler.openUri("https://buymeacoffee.com/shub39") }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.buymeacoffee),
                        contentDescription = "Buy me a coffee",
                        modifier = Modifier.size(24.dp),
                    )

                    Text(text = stringResource(R.string.bmc))
                }
            }

            Button(onClick = { uriHandler.openUri("https://hosted.weblate.org/engage/rush/") }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.language),
                        contentDescription = "Translate",
                        modifier = Modifier.size(24.dp),
                    )

                    Text(text = stringResource(R.string.translate))
                }
            }

            Button(
                onClick = {
                    uriHandler.openUri(
                        "https://play.google.com/store/apps/details?id=com.shub39.rush.play"
                    )
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.play_store),
                        contentDescription = "Rate On Google Play",
                        modifier = Modifier.size(20.dp),
                    )

                    Text(text = stringResource(R.string.rate_on_play))
                }
            }
        }
    }
}
