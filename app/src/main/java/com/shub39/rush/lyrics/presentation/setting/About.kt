package com.shub39.rush.lyrics.presentation.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.domain.Route
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.core.presentation.openLinkInBrowser
import com.shub39.rush.lyrics.presentation.setting.component.BetterIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About(
    navigator: (Route) -> Unit
) = PageFill {
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.widthIn(max = 700.dp),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Icon(
                        painter = painterResource(R.drawable.rush_transparent),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(vertical = 60.dp)
                            .size(150.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    ListItem(
                        headlineContent = {
                            Text(stringResource(R.string.app_name))
                        },
                        supportingContent = {
                            Text(context.packageName)
                        },
                        trailingContent = {
                            Row {
                                BetterIconButton(
                                    onClick = {
                                        openLinkInBrowser(
                                            context,
                                            "https://discord.gg/https://discord.gg/nxA2hgtEKf"
                                        )
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.discord_svgrepo_com),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                BetterIconButton(
                                    onClick = {
                                        openLinkInBrowser(context, "https://github.com/shub39/Rush")
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.github_mark),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    )
                }

                item {
                    ListItem(
                        headlineContent = {
                            Text("Made By shub39")
                        },
                        supportingContent = {
                            Text(getRandomLine())
                        },
                        trailingContent = {
                            Row {
                                BetterIconButton(
                                    onClick = {
                                        openLinkInBrowser(context, "https://github.com/shub39")
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.github_mark),
                                        contentDescription = "Github",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    )
                }

                item {
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.about_libraries)) },
                        trailingContent = {
                            BetterIconButton(
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
}

@Preview
@Composable
private fun Preview() {
    About { }
}