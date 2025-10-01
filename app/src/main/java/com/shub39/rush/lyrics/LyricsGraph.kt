package com.shub39.rush.lyrics

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.shub39.rush.core.domain.data_classes.SongUi
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.core.presentation.updateSystemBars
import com.shub39.rush.lyrics.section.LyricsCustomisationsPage
import com.shub39.rush.lyrics.section.LyricsPage
import io.gitlab.bpavuk.viz.VisualizerState
import io.gitlab.bpavuk.viz.rememberVisualizerState
import kotlinx.serialization.Serializable

@Serializable
sealed interface LyricsRoutes {
    @Serializable
    data object LyricsPage : LyricsRoutes

    @Serializable
    data object LyricsCustomisations : LyricsRoutes
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LyricsGraph(
    notificationAccess: Boolean,
    lyricsState: LyricsPageState,
    lyricsAction: (LyricsPageAction) -> Unit,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val microphonePermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val waveData = rememberVisualizerState(microphonePermission.status.isGranted).let { state ->
        if (state !is VisualizerState.Ready) return@let null

        state.fft
    }

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
                notificationAccess = notificationAccess,
                waveData = waveData
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
                notificationAccess = notificationAccess,
                microphonePermission = microphonePermission.status.isGranted,
                requestMicrophonePermission = { microphonePermission.launchPermissionRequest() },
                waveData = waveData
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            LyricsPageState(
                song = SongUi(
                    id = 0,
                    title = "Random Song",
                    artists = "shub39",
                    artUrl = "",
                    lyrics = (0..100).associateWith { "Line no $it" }.entries.toList()
                ),
            )
        )
    }

    val waveData = rememberVisualizerState().let {
        if (it is VisualizerState.Ready) it.fft else null
    }

    RushTheme(
        theme = Theme()
    ) { 
        LyricsGraph(
            notificationAccess = true,
            lyricsState = state,
            lyricsAction = {},
            onDismiss = {},
            onShare = {}
        )
    }
}