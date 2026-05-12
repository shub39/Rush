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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.shub39.rush.data.listener.MediaListenerImpl
import com.shub39.rush.viewmodels.GlobalVM
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val globalViewModel: GlobalVM by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        globalViewModel.onAction(GlobalAction.OnCheckNotificationAccess)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                globalViewModel.state
                    .map { it.notificationAccess }
                    .distinctUntilChanged()
                    .collect { hasAccess ->
                        if (hasAccess) {
                            MediaListenerImpl.startListening(applicationContext)
                        }
                    }
            }
        }

        setContent { RootContent() }
    }

    override fun onResume() {
        super.onResume()
        globalViewModel.onAction(GlobalAction.OnCheckNotificationAccess)
    }
}
