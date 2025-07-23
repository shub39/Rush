package com.shub39.rush.lyrics.presentation.setting.component

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.rush.BuildConfig
import com.shub39.rush.R
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.brands.Discord
import compose.icons.fontawesomeicons.brands.Github
import compose.icons.fontawesomeicons.brands.GooglePlay
import compose.icons.fontawesomeicons.brands.Twitter
import compose.icons.fontawesomeicons.solid.Coffee
import compose.icons.fontawesomeicons.solid.Language

@Composable
fun AboutApp() {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column {
                Text(
                    text = "Rush",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            }

            Spacer(modifier = Modifier.weight(1f))

            Row {
                IconButton(
                    onClick = { uriHandler.openUri("https://discord.gg/nxA2hgtEKf") }
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Brands.Discord,
                        contentDescription = "Discord",
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = { uriHandler.openUri("https://github.com/shub39/Rush") }
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Brands.Github,
                        contentDescription = "Github",
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = { uriHandler.openUri("https://x.com/RushedLyrics") }
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Brands.Twitter,
                        contentDescription = "Twitter",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        FlowRow(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { uriHandler.openUri("https://buymeacoffee.com/shub39") }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Coffee,
                        contentDescription = "Buy me a coffee",
                        modifier = Modifier.size(24.dp)
                    )

                    Text(text = stringResource(R.string.bmc))
                }
            }

            Button(
                onClick = { uriHandler.openUri("https://hosted.weblate.org/engage/rush/") }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Language,
                        contentDescription = "Translate",
                        modifier = Modifier.size(24.dp)
                    )

                    Text(text = stringResource(R.string.translate))
                }
            }

            Button(
                onClick = { uriHandler.openUri("https://play.google.com/store/apps/details?id=com.shub39.rush.play") }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Brands.GooglePlay,
                        contentDescription = "Rate On Google Play",
                        modifier = Modifier.size(24.dp)
                    )

                    Text(text = stringResource(R.string.rate_on_play))
                }
            }
        }
    }
}