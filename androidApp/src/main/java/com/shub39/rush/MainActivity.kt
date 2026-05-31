package com.shub39.rush

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shub39.rush.app.App
import com.shub39.rush.shared.core.listener.MediaListener
import com.shub39.rush.shared.ui.LocalWindowSizeClass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        MediaListener.startListening(this)

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) { App() }
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