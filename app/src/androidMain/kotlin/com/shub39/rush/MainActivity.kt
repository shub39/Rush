package com.shub39.rush

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shub39.rush.lyrics.data.listener.MediaListener
import org.koin.compose.KoinContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        MediaListener.init(this)

        enableEdgeToEdge()
        setContent {
            KoinContext {
                RushApp()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaListener.destroy()
    }

    override fun onPause() {
        super.onPause()
        MediaListener.destroy()
    }

    override fun onResume() {
        super.onResume()
        MediaListener.init(this)
    }

}