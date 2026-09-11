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
package com.shub39.rush

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.ComposeRuntimeFlags
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import coil3.ImageLoader
import com.shub39.rush.app.App
import com.shub39.rush.shared.core.listener.MediaListener
import com.shub39.rush.shared.ui.LocalWindowSizeClass
import com.skydoves.landscapist.coil3.LocalCoilImageLoader
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        MediaListener.startListening(this)
        ComposeRuntimeFlags.isLinkBufferComposerEnabled = true

        setContent {
            val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
            val imageLoader = koinInject<ImageLoader>()

            CompositionLocalProvider(
                LocalWindowSizeClass provides windowSizeClass,
                LocalCoilImageLoader provides imageLoader,
            ) {
                App()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        MediaListener.startListening(this)
    }

    override fun onRestart() {
        super.onRestart()

        MediaListener.startListening(this)
    }
}
