package com.shub39.rush.page

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@Composable
fun RushApp(
    navController: NavHostController,
    rushViewModel: RushViewModel,
    imageLoader: ImageLoader
) {
    val screens = listOf("LyricsPage", "SearchPage", "SavedPage")
    val pagerState = rememberPagerState(initialPage = 2) { screens.size }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopBar(navController = navController) },
        bottomBar = {
            BottomBar(pagerState = pagerState) { page ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(page)
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.LyricsPage.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(200)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            composable(Screens.LyricsPage.route) {
                RushPager(
                    rushViewModel = rushViewModel,
                    imageLoader = imageLoader,
                    pagerState = pagerState,
                    onSwipe = { page ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page)
                        }
                    }
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

@Composable
fun RushPager(
    rushViewModel: RushViewModel,
    imageLoader: ImageLoader,
    pagerState: PagerState,
    onSwipe: (Int) -> Unit
) {
    HorizontalPager(
        state = pagerState,
    ) { page ->
        when (page) {
            0 -> LyricsPage(
                rushViewModel,
                imageLoader
            )

            1 -> SearchPage(
                rushViewModel,
                imageLoader,
                onClick = { onSwipe(0) }
            )

            2 -> SavedPage(
                rushViewModel,
                imageLoader,
                onClick = { onSwipe(0) }
            )
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
                        navController.navigate(Screens.SettingsPage.route) {
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
fun BottomBar(
    pagerState: PagerState,
    onChange: (Int) -> Unit
) {
    val screens = listOf(
        Screens.LyricsPage,
        Screens.SearchPage,
        Screens.SavedPage
    )
    val currentRoute = pagerState.currentPage

    NavigationBar {
        screens.forEachIndexed { index, screen ->
            NavigationBarItem(
                selected = currentRoute == index,
                onClick = {
                    if (currentRoute != index) {
                        onChange(index)
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