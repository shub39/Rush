package com.shub39.rush.page

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.ImageLoader
import com.shub39.rush.R
import com.shub39.rush.viewmodel.RushViewModel

@Composable
fun RushApp(
    navController: NavHostController,
    rushViewModel: RushViewModel,
    imageLoader: ImageLoader
) {
    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.SavedPage.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(200)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            composable(Screens.LyricsPage.route) {
                LyricsPage(
                    rushViewModel = rushViewModel,
                    imageLoader = imageLoader
                )
            }
            composable(Screens.SearchPage.route) {
                SearchPage(
                    rushViewModel = rushViewModel,
                    navController = navController,
                    imageLoader = imageLoader
                )
            }
            composable(Screens.SavedPage.route) {
                SavedPage(
                    rushViewModel = rushViewModel,
                    navController = navController,
                    imageLoader = imageLoader
                )
            }
            composable(Screens.SettingsPage.route) {
                SettingPage(
                    rushViewModel = rushViewModel,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    TopAppBar(
        title = {
            if (currentDestination?.route == Screens.SettingsPage.route) {
                Text(
                    text = stringResource(id = R.string.settings),
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
                enabled = currentDestination?.route == Screens.SettingsPage.route
            ) {
                navController.navigateUp()
            }

            IconButton(
                onClick = {
                    if (currentDestination?.route != Screens.SettingsPage.route) {
                        navController.navigate(Screens.SettingsPage.route){
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigateUp()
                    }
                }
            ) {
                if (currentDestination?.route != Screens.SettingsPage.route) {
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

@Composable
fun BottomBar(navController: NavController) {
    val screens = listOf(
        Screens.LyricsPage,
        Screens.SearchPage,
        Screens.SavedPage
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != Screens.SettingsPage.route) {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = screen.imageId),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(id = screen.labelId)) },
                alwaysShowLabel = false
            )
        }
    }
}

fun openLinkInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

sealed class Screens(
    val route: String,
    val imageId: Int,
    val labelId: Int
) {
    data object LyricsPage : Screens("lyrics", R.drawable.round_lyrics_24, R.string.lyrics)
    data object SearchPage : Screens("search", R.drawable.round_search_24, R.string.search)
    data object SavedPage : Screens("saved", R.drawable.round_download_24, R.string.saved)
    data object SettingsPage : Screens("settings", R.drawable.round_settings_24, R.string.settings)
}