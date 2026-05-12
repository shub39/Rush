/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
fun RootContent(globalViewModel: GlobalVM = koinViewModel()) {
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
                            globalViewModel.onAction(GlobalAction.DismissChangelog)
                        },
                    )
                }

                is GlobalOverlay.Changelog -> {
                    ChangelogSheet(
                        currentLog = overlay.changelog,
                        onDismissRequest = {
                            globalViewModel.onAction(GlobalAction.DismissChangelog)
                        },
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
