package com.shub39.rush.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


@Composable
fun RushTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    theme: String,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && theme == "Material" -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme && theme == "Yellow" -> GruvboxDarkScheme

        !darkTheme && theme == "Lime" -> LimeLightScheme

        !darkTheme && theme == "Yellow" -> GruvboxLightScheme

        else -> LimeDarkScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )

}
