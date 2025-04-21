package com.shub39.rush.onboarding

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.MeshGradient
import com.shub39.rush.R
import com.shub39.rush.core.domain.LyricsPagePreferences
import com.shub39.rush.core.domain.OtherPreferences
import com.shub39.rush.core.presentation.RushDialog
import com.shub39.rush.core.presentation.generateGradientColors
import com.shub39.rush.lyrics.data.listener.NotificationListener
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun OnboardingDialog(
    otherDatastore: OtherPreferences = koinInject(),
    lyricsDatastore: LyricsPagePreferences = koinInject()
) {
    val hypnoticSupport = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    val context = LocalContext.current
    val pagerState = rememberPagerState { if (hypnoticSupport) 3 else 2 }
    val coroutineScope = rememberCoroutineScope()

    val hypnoticCanvas by lyricsDatastore.getHypnoticCanvasFlow().collectAsState(true)

    LaunchedEffect(hypnoticSupport) {
        lyricsDatastore.updateHypnoticCanvas(hypnoticSupport)
    }

    val cardColors = CardDefaults.cardColors()
    val listItemColors = ListItemDefaults.colors(
        containerColor = cardColors.containerColor,
        supportingColor = cardColors.contentColor,
        headlineColor = cardColors.contentColor,
        leadingIconColor = cardColors.contentColor,
        trailingIconColor = cardColors.contentColor
    )

    RushDialog(
        onDismissRequest = {}
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxHeight(0.7f)
                .fillMaxWidth(),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.rush_transparent),
                            contentDescription = "Rush Icon",
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )

                        Text(
                            text = "Welcome to Rush!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Search, Save and Share Lyrics your favorite lyrics",
                            textAlign = TextAlign.Center
                        )

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next"
                            )
                        }
                    }

                    1 -> Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var notificationAccess by remember {
                            mutableStateOf(NotificationListener.canAccessNotifications(context))
                        }

                        LaunchedEffect(Unit) {
                            while (!notificationAccess) {
                                kotlinx.coroutines.delay(500)
                                notificationAccess =
                                    NotificationListener.canAccessNotifications(context)
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notification",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(80.dp)
                        )

                        Text(
                            text = "Some features require Notification Access",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Column {
                            ListItem(
                                colors = listItemColors,
                                headlineContent = {
                                    Text(
                                        text = "Rush Mode",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        text = "Fetches songs automatically with your music player",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                leadingContent = {
                                    Icon(
                                        painter = painterResource(R.drawable.rush_transparent),
                                        contentDescription = "Rush Icon",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            )

                            ListItem(
                                colors = listItemColors,
                                headlineContent = {
                                    Text(
                                        text = "Synced Lyrics",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        text = "Syncs with your music player if timed lyrics is available",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                leadingContent = {
                                    Icon(
                                        painter = painterResource(R.drawable.round_sync_24),
                                        contentDescription = "Synced",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            )
                        }

                        Button(
                            onClick = {
                                if (!notificationAccess) {
                                    val intent =
                                        Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                                    context.startActivity(intent)
                                } else {
                                    coroutineScope.launch {
                                        if (hypnoticSupport) {
                                            pagerState.animateScrollToPage(2)
                                        } else {
                                            otherDatastore.updateOnboardingDone(true)
                                        }
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = if (!notificationAccess) "Grant Access" else "Done"
                            )
                        }

                        if (!notificationAccess) {
                            TextButton(
                                onClick = {
                                    coroutineScope.launch {
                                        if (hypnoticSupport) {
                                            pagerState.animateScrollToPage(2)
                                        } else {
                                            otherDatastore.updateOnboardingDone(true)
                                        }
                                    }
                                }
                            ) {
                                Text(
                                    text = "No Thanks"
                                )
                            }
                        }
                    }

                    else -> Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(MaterialTheme.shapes.large),
                            contentAlignment = Alignment.Center
                        ) {
                            if (hypnoticCanvas) {
                                val colors = generateGradientColors(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.surfaceContainer,
                                    steps = 6
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .shaderBackground(
                                            MeshGradient(
                                                colors = colors.toTypedArray()
                                            ),
                                            fallback = {
                                                Brush.horizontalGradient(colors)
                                            }
                                        )
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                )
                            }

                            Text(
                                text = "This is how Lyrics will appear",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        }

                        ListItem(
                            colors = listItemColors,
                            headlineContent = {
                                Text(
                                    text = "Hypnotic Canvas",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = "Enable animated background for lyrics. May affect performance.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            trailingContent = {
                                Switch(
                                    checked = hypnoticCanvas,
                                    onCheckedChange = {
                                        coroutineScope.launch {
                                            lyricsDatastore.updateHypnoticCanvas(it)
                                        }
                                    }
                                )
                            }
                        )

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    otherDatastore.updateOnboardingDone(true)
                                }
                            }
                        ) {
                            Text(text = "Done")
                        }
                    }
                }
            }
        }
    }
}