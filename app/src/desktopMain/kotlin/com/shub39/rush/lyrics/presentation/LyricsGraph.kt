package com.shub39.rush.lyrics.presentation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.lyrics.presentation.lyrics.LyricsCustomisationsPage
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState
import kotlinx.serialization.Serializable

private sealed interface LyricsRoutes {
    @Serializable
    data object LyricsPage : LyricsRoutes

    @Serializable
    data object LyricsCustomisations : LyricsRoutes
}

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
                state = lyricsState,
                action = lyricsAction
            )
        }
    }
}