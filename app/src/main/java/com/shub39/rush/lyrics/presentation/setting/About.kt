package com.shub39.rush.lyrics.presentation.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.data.Theme
import com.shub39.rush.core.domain.Route
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.core.presentation.RushTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.brands.Discord
import compose.icons.fontawesomeicons.brands.Github
import compose.icons.fontawesomeicons.brands.Twitter
import compose.icons.fontawesomeicons.solid.Coffee
import compose.icons.fontawesomeicons.solid.Language

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About(
    navigator: (Route) -> Unit
) = PageFill {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = Modifier.widthIn(max = 500.dp),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about)) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // github, discord
            item {
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.rush_transparent),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = context.packageName,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            IconButton(
                                onClick = {
                                    uriHandler.openUri("https://discord.gg/https://discord.gg/nxA2hgtEKf")
                                }
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Brands.Discord,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    uriHandler.openUri("https://github.com/shub39/Rush")
                                }
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Brands.Github,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    uriHandler.openUri("https://x.com/RushedLyrics")
                                }
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Brands.Twitter,
                                    contentDescription = "Twitter",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    FilledTonalButton(
                        onClick = {
                            uriHandler.openUri("https://buymeacoffee.com/shub39")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Language,
                            contentDescription = "Github",
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Translate")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    FilledTonalButton(
                        onClick = {
                            uriHandler.openUri("https://buymeacoffee.com/shub39")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Coffee,
                            contentDescription = "Github",
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Sponsor")
                    }
                }
            }

            // navigate to about libraries
            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.about_libraries)) },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = { navigator(Route.AboutLibrariesPage) }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_arrow_forward_ios_24),
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    RushTheme(
        state = Theme()
    ) {
        About { }
    }
}