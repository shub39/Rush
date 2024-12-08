package com.shub39.rush.app

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.lyrics.data.listener.NotificationListener
import com.shub39.rush.lyrics.presentation.search_sheet.SearchSheet
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPage
import com.shub39.rush.lyrics.presentation.saved.SavedPage
import com.shub39.rush.lyrics.presentation.setting.SettingPage
import com.shub39.rush.lyrics.presentation.share.SharePage
import com.shub39.rush.lyrics.presentation.RushViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RushApp(
    rushViewModel: RushViewModel = koinViewModel()
) {
    val lyricsState by rushViewModel.lyricsState.collectAsStateWithLifecycle()
    val savedState by rushViewModel.savedState.collectAsStateWithLifecycle()
    val shareState by rushViewModel.shareState.collectAsStateWithLifecycle()
    val settingsState by rushViewModel.settingsState.collectAsStateWithLifecycle()
    val searchSheetState by rushViewModel.searchState.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val context = LocalContext.current

    var currentRoute: Route by remember { mutableStateOf(Route.SavedPage) }

    if (searchSheetState.visible) {
        SearchSheet(
            state = searchSheetState,
            action = rushViewModel::onSearchSheetAction,
            onClick = { navController.navigate(Route.LyricsPage) }
        )
    }

    NavHost(
        navController = navController,
        startDestination = Route.RushGraph,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() }
    ) {
        navigation<Route.RushGraph>(
            startDestination = Route.SavedPage
        ) {
            composable<Route.SavedPage> {
                currentRoute = Route.SavedPage

                Scaffold(
                    topBar = {
                        TopBar(
                            navController = navController,
                            currentRoute = currentRoute
                        )
                    }
                ) { paddingValues ->
                    SavedPage(
                        state = savedState,
                        action = rushViewModel::onSavedPageAction,
                        onSongClick = {
                            navController.navigate(Route.LyricsGraph)
                        },
                        paddingValues = paddingValues
                    )
                }
            }

            composable<Route.SettingPage> {
                currentRoute = Route.SettingPage

                Scaffold(
                    topBar = {
                        TopBar(
                            navController = navController,
                            currentRoute = currentRoute
                        )
                    }
                ) { paddingValues ->
                    SettingPage(
                        state = settingsState,
                        action = rushViewModel::onSettingsPageAction,
                        notificationAccess = NotificationListener.canAccessNotifications(context),
                        paddingValues = paddingValues
                    )
                }
            }
        }

        navigation<Route.LyricsGraph>(
            startDestination = Route.LyricsPage
        ) {
            composable<Route.SharePage> {
                currentRoute = Route.SharePage

                Scaffold { paddingValues ->
                    SharePage(
                        onDismiss = {
                            navController.navigateUp()
                        },
                        state = shareState,
                        paddingValues = paddingValues,
                        action = rushViewModel::onSharePageAction
                    )
                }
            }

            composable<Route.LyricsPage> {
                currentRoute = Route.LyricsPage

                LyricsPage(
                    state = lyricsState,
                    action = rushViewModel::onLyricsPageAction,
                    onShare = {
                        navController.navigate(Route.SharePage)
                    }
                )
            }
        }
    }
}