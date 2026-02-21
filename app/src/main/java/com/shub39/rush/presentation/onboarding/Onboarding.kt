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
package com.shub39.rush.presentation.onboarding

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes.Companion.VerySunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.presentation.components.RushTheme
import com.shub39.rush.presentation.onboarding.component.AnimatedAppIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
private sealed interface OnboardingRoutes {
    @Serializable data object Welcome : OnboardingRoutes

    @Serializable data object Permissions : OnboardingRoutes

    companion object {
        val routes = listOf(Welcome, Permissions)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Onboarding(
    onDone: () -> Unit,
    notificationAccess: Boolean,
    onUpdateNotificationAccess: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val pagerState = rememberPagerState { OnboardingRoutes.routes.size }

    LaunchedEffect(Unit) {
        while (!notificationAccess) {
            onUpdateNotificationAccess()
            delay(1000)
        }
    }

    BackHandler {}

    Surface {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            AnimatedAppIcon()

                            Text(
                                text = "Welcome to Rush!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )

                            Text(
                                text = "Search, Save and Share your favorite lyrics",
                                textAlign = TextAlign.Center,
                            )

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                },
                                shapes =
                                    ButtonShapes(
                                        shape = CircleShape,
                                        pressedShape = RoundedCornerShape(10.dp),
                                    ),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_forward),
                                    contentDescription = "Next",
                                )
                            }
                        }
                    }

                    1 -> {
                        // notification access screen
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier =
                                    Modifier.size(100.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = VerySunny.toShape(),
                                        ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.notifications),
                                    contentDescription = "Notification",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(60.dp),
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            Text(
                                text = "Some features require Notification Access",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )

                            Spacer(Modifier.height(16.dp))

                            Column(
                                modifier = Modifier.widthIn(max = 350.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                listOf(
                                        Triple(
                                            first = painterResource(R.drawable.meteor),
                                            second = "Rush Mode",
                                            third =
                                                "Fetches songs automatically with your music player",
                                        ),
                                        Triple(
                                            first = painterResource(R.drawable.sync),
                                            second = "Synced Lyrics",
                                            third =
                                                "Syncs with your music player if timed lyrics is available",
                                        ),
                                    )
                                    .forEach { feature ->
                                        Card(
                                            shape = MaterialTheme.shapes.extraLarge,
                                            colors =
                                                CardDefaults.cardColors(
                                                    containerColor =
                                                        MaterialTheme.colorScheme
                                                            .secondaryContainer,
                                                    contentColor =
                                                        MaterialTheme.colorScheme
                                                            .onSecondaryContainer,
                                                ),
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Icon(
                                                    painter = feature.first,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(42.dp),
                                                )

                                                Column {
                                                    Text(
                                                        text = feature.second,
                                                        style =
                                                            MaterialTheme.typography.titleLarge
                                                                .copy(fontWeight = FontWeight.Bold),
                                                    )
                                                    Text(
                                                        text = feature.third,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                    )
                                                }
                                            }
                                        }
                                    }
                            }

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (!notificationAccess) {
                                        val intent =
                                            Intent(
                                                "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
                                            )
                                        context.startActivity(intent)
                                    } else {
                                        onDone()
                                    }
                                },
                                contentPadding = ButtonDefaults.MediumContentPadding,
                            ) {
                                Text(text = if (!notificationAccess) "Grant Access" else "Done")
                            }

                            Spacer(Modifier.height(6.dp))

                            if (!notificationAccess) {
                                TextButton(onClick = onDone) { Text(text = "No Thanks") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RushTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        Onboarding(onDone = {}, notificationAccess = false, onUpdateNotificationAccess = {})
    }
}
