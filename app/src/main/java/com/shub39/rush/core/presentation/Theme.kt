package com.shub39.rush.core.presentation

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.shub39.rush.core.domain.AppTheme

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

        darkTheme && theme == AppTheme.YELLOW.type -> YellowDarkScheme

        darkTheme && theme == AppTheme.LIME.type -> LimeDarkScheme

        !darkTheme && theme == AppTheme.LIME.type -> LimeLightScheme

        !darkTheme && theme == AppTheme.YELLOW.type -> YellowLightScheme

        else -> {
            if (darkTheme) YellowDarkScheme else YellowLightScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )

}
