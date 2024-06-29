package com.shub39.rush.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.shub39.rush.database.SettingsDataStore


@Composable
fun RushTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val theme = SettingsDataStore.getToggleThemeFlow(context).collectAsState(initial = "Gruvbox")

    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && theme.value == "Material" -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme && theme.value == "Yellow" -> GruvboxDarkScheme

        darkTheme && theme.value == "Lime" -> LimeDarkScheme

        !darkTheme && theme.value == "Yellow" -> GruvboxLightScheme

        else -> LimeLightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
