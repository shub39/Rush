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
import com.shub39.rush.lyrics.presentation.lyrics.LyricsCustomisationsPage
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPage
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState
import com.shub39.rush.lyrics.presentation.share.SharePage
import com.shub39.rush.lyrics.presentation.share.SharePageAction
import com.shub39.rush.lyrics.presentation.share.SharePageState

@Composable
actual fun LyricsGraph(
    notificationAccess: Boolean,
    lyricsState: LyricsPageState,
    shareState: SharePageState,
    lyricsAction: (LyricsPageAction) -> Unit,
    shareAction: (SharePageAction) -> Unit
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
                state = lyricsState,
                onShare = {
                    navController.navigate(LyricsRoutes.SharePage) {
                        launchSingleTop = true
                    }
                },
                notificationAccess = notificationAccess
            )
        }

        composable<LyricsRoutes.LyricsCustomisations> {
            LyricsCustomisationsPage(
                onNavigateBack = { navController.navigateUp() },
                showFullscreen = false,
                state = lyricsState,
                action = lyricsAction,
                modifier = Modifier.widthIn(max = 1000.dp).fillMaxSize()
            )
        }

        composable<LyricsRoutes.SharePage> {
            SharePage(
                onDismiss = { navController.navigateUp() },
                state = shareState,
                action = shareAction,
                share = false,
                zoomEnabled = false
            )
        }
    }
}