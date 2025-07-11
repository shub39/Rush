package com.shub39.rush.onboarding

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.blend
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.fixIfDisliked
import com.materialkolor.ktx.isLight
import com.materialkolor.ktx.lighten
import com.shub39.rush.core.domain.enums.CardTheme
import com.shub39.rush.lyrics.data.listener.NotificationListener
import com.shub39.rush.onboarding.component.AnimatedAppIcon
import com.shub39.rush.onboarding.component.AnimatedShareCardsDemo
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Meteor
import compose.icons.fontawesomeicons.solid.SyncAlt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
private sealed interface OnboardingRoutes {
    @Serializable
    data object Welcome : OnboardingRoutes

    @Serializable
    data object ShareDemo : OnboardingRoutes

    @Serializable
    data object Permissions : OnboardingRoutes

    companion object {
        val routes = listOf(
            Welcome,
            ShareDemo,
            Permissions
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Onboarding(
    onDone: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val pagerState = rememberPagerState { OnboardingRoutes.routes.size }

    BackHandler {}

    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                pageSpacing = 16.dp,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (it) {
                    0 -> {
                        // welcome section
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AnimatedAppIcon()

                            Text(
                                text = "Welcome to Rush!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Search, Save and Share your favorite lyrics",
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                },
                                shapes = ButtonShapes(
                                    shape = CircleShape,
                                    pressedShape = RoundedCornerShape(10.dp)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next"
                                )
                            }
                        }
                    }

                    1 -> {
                        // Share cards demo
                        var cardStyle by remember { mutableStateOf(CardTheme.entries.random()) }
                        var newCardProgress by remember { mutableFloatStateOf(0f) }
                        var containerColor by remember {
                            mutableStateOf(
                                Color(
                                    Random.nextInt(256),
                                    Random.nextInt(256),
                                    Random.nextInt(256)
                                )
                            )
                        }

                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(1000)
                                newCardProgress = newCardProgress + 0.33f

                                if (newCardProgress >= 1f) {
                                    newCardProgress = 0f
                                    var newStyle = CardTheme.entries.random()
                                    while (newStyle == cardStyle) newStyle =
                                        CardTheme.entries.random()
                                    cardStyle = newStyle
                                    containerColor = Color(
                                        Random.nextInt(256),
                                        Random.nextInt(256),
                                        Random.nextInt(256)
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Share the most notable lines",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "So many Card Styles!",
                                textAlign = TextAlign.Center
                            )

                            Box(
                                modifier = Modifier.fillMaxHeight(0.6f),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedShareCardsDemo(
                                    cardStyle = cardStyle,
                                    containerColor = containerColor
                                        .darken(2f)
                                        .fixIfDisliked(),
                                    contentColor = containerColor
                                        .lighten(2f)
                                        .blend(
                                            if (containerColor.darken(2f)
                                                    .isLight()
                                            ) Color.Black else Color.White,
                                            0.9f
                                        )
                                        .fixIfDisliked()
                                )
                            }

                            Box(contentAlignment = Alignment.Center) {
                                CircularWavyProgressIndicator(
                                    progress = { newCardProgress }
                                )

                                Text(
                                    ((1f - newCardProgress).times(3) + 1).toInt().toString()
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                },
                                contentPadding = ButtonDefaults.MediumContentPadding,
                                shapes = ButtonShapes(
                                    shape = CircleShape,
                                    pressedShape = RoundedCornerShape(10.dp)
                                )
                            ) {
                                Text("Next")
                            }
                        }
                    }

                    2 -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            var notificationAccess by remember {
                                mutableStateOf(NotificationListener.canAccessNotifications(context))
                            }

                            LaunchedEffect(Unit) {
                                while (!notificationAccess) {
                                    delay(500)
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

                            Spacer(Modifier.height(16.dp))

                            Text(
                                text = "Some features require Notification Access",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Column {
                                ListItem(
                                    headlineContent = {
                                        Text(text = "Rush Mode")
                                    },
                                    supportingContent = {
                                        Text(text = "Fetches songs automatically with your music player")
                                    },
                                    leadingContent = {
                                        Icon(
                                            imageVector = FontAwesomeIcons.Solid.Meteor,
                                            contentDescription = "Rush Mode",
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                )

                                ListItem(
                                    headlineContent = {
                                        Text(text = "Synced Lyrics")
                                    },
                                    supportingContent = {
                                        Text(text = "Syncs with your music player if timed lyrics is available")
                                    },
                                    leadingContent = {
                                        Icon(
                                            imageVector = FontAwesomeIcons.Solid.SyncAlt,
                                            contentDescription = "Synced",
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (!notificationAccess) {
                                        val intent =
                                            Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                                        context.startActivity(intent)
                                    } else {
                                        onDone()
                                    }
                                },
                                contentPadding = ButtonDefaults.MediumContentPadding,
                                shapes = ButtonShapes(
                                    shape = CircleShape,
                                    pressedShape = RoundedCornerShape(10.dp)
                                )
                            ) {
                                Text(
                                    text = if (!notificationAccess) "Grant Access" else "Done"
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            if (!notificationAccess) {
                                TextButton(
                                    onClick = onDone
                                ) {
                                    Text(
                                        text = "No Thanks"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
