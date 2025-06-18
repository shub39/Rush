package com.shub39.rush.lyrics.presentation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.LyricsRoutes
import com.shub39.rush.lyrics.presentation.lyrics.LyricsCustomisationsPage
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPage
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState

@Composable
fun LyricsGraph(
    lyricsState: LyricsPageState,
    lyricsAction: (LyricsPageAction) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LyricsRoutes.LyricsPage,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() }
    ) {
        composable<LyricsRoutes.LyricsPage> {
            LyricsPage(
                onEdit = {
                    navController.navigate(LyricsRoutes.LyricsCustomisations) {
                        launchSingleTop = true
                    }
                },
                action = lyricsAction,
                state = lyricsState
            )
        }

        composable<LyricsRoutes.LyricsCustomisations> {
            LyricsCustomisationsPage(
                onNavigateBack = { navController.navigateUp() },
                showFullscreenAndLines = false,
                state = lyricsState,
                action = lyricsAction,
                modifier = Modifier.widthIn(max = 1000.dp).fillMaxSize()
            )
        }
    }
}