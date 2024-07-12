package com.shub39.rush

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.component.provideImageLoader
import com.shub39.rush.database.SettingsDataStore
import com.shub39.rush.listener.MediaNotificationListener
import com.shub39.rush.page.RushApp
import com.shub39.rush.ui.theme.RushTheme
import com.shub39.rush.viewmodel.RushViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val rushViewModel = RushViewModel(application)
        val imageLoader = provideImageLoader(this)

        enableEdgeToEdge()
        setContent {

            if (!MediaNotificationListener.canAccessNotifications(this)) {
                Toast.makeText(this, stringResource(id = R.string.toast), Toast.LENGTH_LONG).show()
                val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                startActivity(intent)
            }

            val theme by SettingsDataStore.getToggleThemeFlow(this)
                .collectAsState(initial = "Gruvbox")

            RushTheme(
                theme = theme
            ) {
                val navController = rememberNavController()
                RushApp(
                    navController = navController,
                    rushViewModel = rushViewModel,
                    imageLoader = imageLoader
                )
            }

            splashScreen.setKeepOnScreenCondition {
                theme == "Gruvbox"
            }

        }

    }
}