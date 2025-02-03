package com.shub39.rush.lyrics.presentation.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.presentation.openLinkInBrowser
import com.shub39.rush.lyrics.presentation.setting.component.BetterIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About() {
    val context = LocalContext.current

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 500.dp)
                    .fillMaxSize()
            ) {
                TopAppBar(
                    title = { Text(stringResource(R.string.about)) }
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Icon(
                            painter = painterResource(R.drawable.rush_transparent),
                            contentDescription = null,
                            modifier = Modifier.padding(vertical = 60.dp).size(150.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    item {
                        ListItem(
                            headlineContent = {
                                Text(stringResource(R.string.app_name))
                            },
                            supportingContent = {
                                Column {
                                    Text(context.packageName)
                                    Text(
                                        context.packageManager.getPackageInfo(
                                            context.packageName,
                                            0
                                        ).versionName ?: ""
                                    )
                                }
                            },
                            trailingContent = {
                                Row {
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

                                    Spacer(modifier = Modifier.padding(4.dp))

                                    BetterIconButton(
                                        onClick = {
                                            openLinkInBrowser(context, "https://discord.gg/https://discord.gg/nxA2hgtEKf")
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.discord_svgrepo_com),
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
                                Text(
                                    text = getRandomLine(),
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Thin
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}