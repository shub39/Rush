package com.shub39.rush.lyrics.presentation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.core.presentation.updateSystemBars
import com.shub39.rush.lyrics.presentation.lyrics.LyricsCustomisationsPage
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPage
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState
import com.shub39.rush.lyrics.presentation.share.SharePage
import com.shub39.rush.lyrics.presentation.share.SharePageAction
import com.shub39.rush.lyrics.presentation.share.SharePageState
import kotlinx.serialization.Serializable

@Serializable
sealed interface LyricsRoutes {
    @Serializable
    data object LyricsPage : LyricsRoutes

    @Serializable
    data object LyricsCustomisations : LyricsRoutes

    @Serializable
    data object SharePage : LyricsRoutes
}

@Composable
fun LyricsGraph(
    notificationAccess: Boolean,
    lyricsState: LyricsPageState,
    shareState: SharePageState,
    lyricsAction: (LyricsPageAction) -> Unit,
    shareAction: (SharePageAction) -> Unit
) {
    val context = LocalContext.current
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
            SideEffect {
                if (lyricsState.fullscreen) {
                    updateSystemBars(context, false)
                }
            }

            LyricsPage(
                onEdit = {
                    navController.navigate(LyricsRoutes.LyricsCustomisations) {
                        launchSingleTop = true
                    }
                },
                onShare = {
                    navController.navigate(LyricsRoutes.SharePage) {
                        launchSingleTop = true
                    }
                },
                action = lyricsAction,
                state = lyricsState,
                notificationAccess = notificationAccess
            )
        }

        composable<LyricsRoutes.SharePage> {
            SideEffect {
                if (lyricsState.fullscreen) {
                    updateSystemBars(context, true)
                }
            }

            SharePage(
                onDismiss = { navController.navigateUp() },
                state = shareState,
                action = shareAction,
                share = true
            )
        }

        composable<LyricsRoutes.LyricsCustomisations> {
            SideEffect {
                if (lyricsState.fullscreen) {
                    updateSystemBars(context, true)
                }
            }

            LyricsCustomisationsPage(
                state = lyricsState,
                onNavigateBack = { navController.navigateUp() },
                action = lyricsAction,
                modifier = Modifier.widthIn(max = 700.dp)
            )
        }
    }
}