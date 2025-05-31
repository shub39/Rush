package com.shub39.rush

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.core.domain.Route
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.lyrics.presentation.LyricsGraph
import com.shub39.rush.lyrics.presentation.SettingsGraph
import com.shub39.rush.lyrics.presentation.saved.SavedPage
import com.shub39.rush.lyrics.presentation.search_sheet.SearchSheet
import com.shub39.rush.lyrics.presentation.viewmodels.LyricsVM
import com.shub39.rush.lyrics.presentation.viewmodels.SavedVM
import com.shub39.rush.lyrics.presentation.viewmodels.SearchSheetVM
import org.koin.compose.viewmodel.koinViewModel

// Not Completed yet
@Composable
fun RushApp(
    lyricsVM: LyricsVM = koinViewModel(),
    searchSheetVM: SearchSheetVM = koinViewModel(),
    savedVM: SavedVM = koinViewModel()
) {
    val lyricsState by lyricsVM.state.collectAsStateWithLifecycle()
    val searchState by searchSheetVM.state.collectAsStateWithLifecycle()
    val savedState by savedVM.state.collectAsStateWithLifecycle()

    val navController = rememberNavController()

    RushTheme {
        NavHost(
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
            navController = navController,
            startDestination = Route.SavedPage,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            composable<Route.SavedPage> {
                SavedPage(
                    state = savedState,
                    action = savedVM::onAction,
                    currentSong = lyricsState.song,
                    autoChange = lyricsState.autoChange,
                    notificationAccess = true,
                    navigator = {
                        navController.navigate(it) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable<Route.LyricsGraph> {
                LyricsGraph(
                    lyricsState = lyricsState,
                    lyricsAction = lyricsVM::onAction,
                )
            }

            composable<Route.SettingsGraph> {
                SettingsGraph { navController.navigateUp() }
            }
        }

        if (searchState.visible) {
            SearchSheet(
                state = searchState,
                action = searchSheetVM::onAction,
                onClick = {
                    navController.navigate(Route.LyricsGraph) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}