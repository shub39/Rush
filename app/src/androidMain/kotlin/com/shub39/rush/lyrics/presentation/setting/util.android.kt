package com.shub39.rush.lyrics.presentation.setting

import android.R.color.system_accent1_200
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.materialkolor.ktx.from
import com.materialkolor.palettes.TonalPalette
import com.materialkolor.rememberDynamicColorScheme
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.lyrics.presentation.setting.component.SelectableMiniPalette
import org.jetbrains.compose.resources.stringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.grant_permission
import rush.app.generated.resources.material_theme
import rush.app.generated.resources.material_theme_desc
import rush.app.generated.resources.notification_permission
import rush.app.generated.resources.palette_style

actual fun LazyListScope.materialYouToggle(
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(Res.string.material_theme)
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(Res.string.material_theme_desc)
                    )
                },
                trailingContent = {
                    Switch(
                        checked = state.theme.materialTheme,
                        onCheckedChange = {
                            action(
                                SettingsPageAction.OnMaterialThemeToggle(it)
                            )
                        }
                    )
                }
            )
        }
    }
}

actual fun LazyListScope.paletteStyles(
    state: SettingsPageState,
    action: (SettingsPageAction) -> Unit
) {
    item {
        Column {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(Res.string.palette_style)
                    )
                }
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(PaletteStyle.entries.toList(), key = { it.name }) { style ->
                    val scheme = rememberDynamicColorScheme(
                        primary = if (state.theme.materialTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            colorResource(system_accent1_200)
                        } else {
                            Color(state.theme.seedColor)
                        },
                        isDark = when (state.theme.appTheme) {
                            AppTheme.SYSTEM -> isSystemInDarkTheme()
                            AppTheme.LIGHT -> false
                            AppTheme.DARK -> true
                        },
                        isAmoled = state.theme.withAmoled,
                        style = style
                    )

                    SelectableMiniPalette(
                        selected = state.theme.style == style,
                        onClick = {
                            action(
                                SettingsPageAction.OnPaletteChange(style = style)
                            )
                        },
                        contentDescription = { style.name },
                        accents = listOf(
                            TonalPalette.from(scheme.primary),
                            TonalPalette.from(scheme.tertiary),
                            TonalPalette.from(scheme.secondary)
                        )
                    )
                }
            }
        }
    }
}

actual fun LazyListScope.notificationAccessReminder(
    notificationAccess: Boolean
) {
    if (!notificationAccess) {
        item {
            val context = LocalContext.current
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")

            ListItem(
                headlineContent = { Text(text = stringResource(Res.string.grant_permission)) },
                supportingContent = { Text(text = stringResource(Res.string.notification_permission)) },
                trailingContent = {
                    FilledTonalIconButton(
                        onClick = { context.startActivity(intent) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}