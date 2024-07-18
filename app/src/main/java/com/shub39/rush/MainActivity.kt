package com.shub39.rush

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.database.SettingsDataStore
import com.shub39.rush.listener.MediaListener
import com.shub39.rush.page.RushApp
import com.shub39.rush.ui.theme.RushTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {

            MediaListener.init(this)

            val theme by SettingsDataStore.getToggleThemeFlow(this)
                .collectAsState(initial = "Gruvbox")

            RushTheme(
                theme = theme
            ) {
                val navController = rememberNavController()
                RushApp(
                    navController = navController
                )
            }

            splashScreen.setKeepOnScreenCondition {
                theme == "Gruvbox"
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