package com.shub39.rush.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import com.shub39.rush.app.state.GlobalOverlay
import com.shub39.rush.presentation.component.ChangelogSheet
import com.shub39.rush.presentation.theme.RushTheme
import com.shub39.rush.viewmodels.GlobalVM
import com.shub39.rush.warning.WarningDialog
import com.skydoves.landscapist.coil3.LocalCoilImageLoader
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RootContent(
    globalViewModel: GlobalVM = koinViewModel()
) {
    val globalState by globalViewModel.state.collectAsStateWithLifecycle()
    val overlayState by globalViewModel.overlay.collectAsStateWithLifecycle()

    val imageLoader: ImageLoader = koinInject()

    CompositionLocalProvider(LocalCoilImageLoader provides imageLoader) {

        RushTheme(theme = globalState.theme) {

            when (val overlay = overlayState) {
                is GlobalOverlay.FossWarning -> {
                    WarningDialog(
                        daysLeft = overlay.daysLeft,
                        onDismissRequest = {
                            globalViewModel.onAction(
                                GlobalAction.DismissChangelog
                            )
                        }
                    )
                }

                is GlobalOverlay.Changelog -> {
                    ChangelogSheet(
                        currentLog = overlay.changelog,
                        onDismissRequest = { globalViewModel.onAction(GlobalAction.DismissChangelog) },
                    )
                }
                GlobalOverlay.None -> Unit
            }

            App(
                globalState = globalState,
                globalEvents = globalViewModel.globalEvents,
                onGlobalAction = { globalViewModel.onAction(it) },
            )
        }

    }
}