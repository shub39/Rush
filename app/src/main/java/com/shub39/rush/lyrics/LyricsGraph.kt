package com.shub39.rush.lyrics

import androidx.activity.compose.BackHandler
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
import kotlinx.serialization.Serializable

@Serializable
sealed interface LyricsRoutes {
    @Serializable
    data object LyricsPage : LyricsRoutes

    @Serializable
    data object LyricsCustomisations : LyricsRoutes
}

@Composable
fun LyricsGraph(
    notificationAccess: Boolean,
    lyricsState: LyricsPageState,
    lyricsAction: (LyricsPageAction) -> Unit,
    onDismiss : () -> Unit,
    onShare: () -> Unit
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    BackHandler {
        updateSystemBars(context, true)
        onDismiss()
    }

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
                onShare = onShare,
                action = lyricsAction,
                state = lyricsState,
                notificationAccess = notificationAccess
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
                onAction = lyricsAction,
                modifier = Modifier.widthIn(max = 700.dp),
            )
        }
    }
}