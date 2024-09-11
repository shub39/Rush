package com.shub39.rush.page

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.shub39.rush.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    pagerState: PagerState
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    TopAppBar(
        title = {
            if (currentDestination?.route == "settings") {
                Text(
                    text = stringResource(id = R.string.settings),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (pagerState.currentPage == 1) {
                Text(
                    text = stringResource(id = R.string.saved),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            BackHandler(
                enabled = currentDestination?.route == "settings"
            ) {
                navController.navigateUp()
            }

            IconButton(
                onClick = {
                    if (currentDestination?.route != "settings") {
                        navController.navigate("settings") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigateUp()
                    }
                }
            ) {
                if (currentDestination?.route != "settings") {
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
        },
    )
}