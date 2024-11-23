package com.shub39.rush.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.shub39.rush.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    currentRoute: Route
) {
    AnimatedVisibility(visible = currentRoute != Route.SharePage) {
        TopAppBar(
            title = {
                if (currentRoute == Route.SettingPage) {
                    Text(
                        text = stringResource(id = R.string.settings),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.saved),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            actions = {
                BackHandler(
                    enabled = currentRoute == Route.SettingPage
                ) {
                    navController.navigateUp()
                }

                IconButton(
                    onClick = {
                        if (currentRoute != Route.SettingPage) {
                            navController.navigate(Route.SettingPage) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            navController.navigateUp()
                        }
                    }
                ) {
                    if (currentRoute != Route.SettingPage) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_settings_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.round_arrow_back_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        )
    }
}