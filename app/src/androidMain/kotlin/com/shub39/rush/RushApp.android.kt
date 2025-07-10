package com.shub39.rush

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.core.presentation.updateSystemBars
import com.shub39.rush.lyrics.data.listener.NotificationListener
import com.shub39.rush.lyrics.presentation.LyricsGraph
import com.shub39.rush.lyrics.presentation.SettingsGraph
import com.shub39.rush.lyrics.presentation.saved.SavedPage
import com.shub39.rush.lyrics.presentation.search_sheet.SearchSheet
import com.shub39.rush.lyrics.presentation.viewmodels.LyricsVM
import com.shub39.rush.lyrics.presentation.viewmodels.SavedVM
import com.shub39.rush.lyrics.presentation.viewmodels.SearchSheetVM
import com.shub39.rush.lyrics.presentation.viewmodels.SettingsVM
import com.shub39.rush.lyrics.presentation.viewmodels.ShareVM
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun RushApp() {
    val settingsVM: SettingsVM = koinViewModel()
    val lyricsVM: LyricsVM = koinViewModel()
    val shareVM: ShareVM = koinViewModel()
    val searchSheetVM: SearchSheetVM = koinViewModel()
    val savedVM: SavedVM = koinViewModel()

    val lyricsState by lyricsVM.state.collectAsStateWithLifecycle()
    val settingsState by settingsVM.state.collectAsStateWithLifecycle()
    val shareState by shareVM.state.collectAsStateWithLifecycle()
    val searchState by searchSheetVM.state.collectAsStateWithLifecycle()
    val savedState by savedVM.state.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val context = LocalContext.current

    RushTheme(
        state = settingsState.theme
    ) {
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
                SideEffect {
                    if (lyricsState.fullscreen) {
                        updateSystemBars(context, true)
                    }
                }

                SavedPage(
                    state = savedState,
                    extractedColors = lyricsState.extractedColors,
                    currentSong = lyricsState.song,
                    notificationAccess = NotificationListener.canAccessNotifications(context),
                    action = savedVM::onAction,
                    autoChange = lyricsState.autoChange,
                    onNavigateToLyrics = { navController.navigate(Route.LyricsGraph) },
                    onNavigateToSettings = { navController.navigate(Route.SettingsGraph) },
                    modifier = Modifier.widthIn(max = 700.dp),
                )
            }

            composable<Route.LyricsGraph> {
                LyricsGraph(
                    notificationAccess = NotificationListener.canAccessNotifications(context),
                    lyricsState = lyricsState,
                    shareState = shareState,
                    lyricsAction = lyricsVM::onAction,
                    shareAction = shareVM::onAction
                )
            }

            composable<Route.SettingsGraph> {
                SettingsGraph(
                    notificationAccess = NotificationListener.canAccessNotifications(context),
                    state = settingsState,
                    action = settingsVM::onAction,
                    onNavigateBack = { navController.navigateUp() }
                )
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