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
package com.shub39.rush.shared.ui.setting

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.materialkolor.rememberDynamicColorScheme
import com.shub39.rush.shared.core.dataclasses.Theme
import com.shub39.rush.shared.core.enums.AppTheme
import com.shub39.rush.shared.core.enums.PaletteStyle
import com.shub39.rush.shared.ui.component.ExpressiveSwitch
import com.shub39.rush.shared.ui.detachedItemShape
import com.shub39.rush.shared.ui.endItemShape
import com.shub39.rush.shared.ui.listItemColors
import com.shub39.rush.shared.ui.toMPaletteStyle
import kotlin.compareTo
import kotlin.text.compareTo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.Res
import rush.shared.ui.generated.resources.arrow_forward_ios
import rush.shared.ui.generated.resources.check
import rush.shared.ui.generated.resources.grant_permission
import rush.shared.ui.generated.resources.language
import rush.shared.ui.generated.resources.material_theme
import rush.shared.ui.generated.resources.material_theme_desc
import rush.shared.ui.generated.resources.notification_permission
import rush.shared.ui.generated.resources.notifications
import rush.shared.ui.generated.resources.palette
import rush.shared.ui.generated.resources.palette_style

@Composable
actual fun ColumnScope.MaterialYouToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        ListItem(
            headlineContent = { Text(text = stringResource(Res.string.material_theme)) },
            supportingContent = { Text(text = stringResource(Res.string.material_theme_desc)) },
            trailingContent = {
                ExpressiveSwitch(checked = checked, onCheckedChange = onCheckedChange)
            },
            colors = listItemColors(),
            modifier = modifier,
        )
    }
}

@Composable
actual fun ColumnScope.PaletteStylePicker(
    modifier: Modifier,
    enabled: Boolean,
    theme: Theme,
    onChange: (PaletteStyle) -> Unit,
) {
    Column(modifier = modifier.clip(endItemShape())) {
        ListItem(
            headlineContent = { Text(text = stringResource(Res.string.palette_style)) },
            supportingContent = {
                Text(text = theme.style.toString().lowercase().replaceFirstChar { it.titlecase() })
            },
            colors = listItemColors(),
            leadingContent = {
                Icon(painter = painterResource(Res.drawable.palette), contentDescription = null)
            },
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier =
                Modifier.fillMaxWidth()
                    .background(listItemColors().containerColor)
                    .padding(start = 52.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PaletteStyle.entries.toList().forEach { style ->
                val scheme =
                    rememberDynamicColorScheme(
                        primary =
                            if (
                                theme.materialTheme &&
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                            ) {
                                colorResource(android.R.color.system_accent1_900)
                            } else Color(theme.seedColor),
                        isDark =
                            when (theme.appTheme) {
                                AppTheme.SYSTEM -> isSystemInDarkTheme()
                                AppTheme.DARK -> true
                                AppTheme.LIGHT -> false
                            },
                        isAmoled = theme.withAmoled,
                        style = style.toMPaletteStyle(),
                    )
                val selected = theme.style == style

                Box(
                    modifier =
                        Modifier.size(50.dp)
                            .clip(if (selected) MaterialShapes.VerySunny.toShape() else CircleShape)
                            .clickable(enabled = enabled) { onChange(style) },
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val colors =
                            listOf(
                                scheme.primary,
                                scheme.primaryContainer,
                                scheme.secondary,
                                scheme.secondaryContainer,
                                scheme.tertiary,
                                scheme.tertiaryContainer,
                            )
                        val sweepAngle = 360f / colors.size
                        colors.forEachIndexed { index, color ->
                            drawArc(
                                color = color,
                                startAngle = index * sweepAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                            )
                        }
                    }

                    Box(
                        modifier =
                            Modifier.matchParentSize()
                                .background(
                                    color = scheme.primary.copy(alpha = if (selected) 0.7f else 0f)
                                )
                    )

                    if (selected) {
                        Icon(
                            painter = painterResource(Res.drawable.check),
                            contentDescription = null,
                            tint = scheme.onPrimary,
                        )
                    }
                }
            }
        }
    }
}

actual fun LazyListScope.notificationToggle(notificationAccess: Boolean) {
    if (!notificationAccess) {
        item {
            val context = LocalContext.current
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")

            ListItem(
                headlineContent = { Text(text = stringResource(Res.string.grant_permission)) },
                supportingContent = {
                    Text(
                        text = stringResource(Res.string.notification_permission),
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(),
                    )
                },
                colors = listItemColors(),
                modifier =
                    Modifier.clip(detachedItemShape()).clickable { context.startActivity(intent) },
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.notifications),
                        contentDescription = null,
                    )
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_forward_ios),
                        contentDescription = null,
                    )
                },
            )
        }
    }
}

actual fun LazyListScope.appLanguagePicker(onClick: () -> Unit) {
    if (Build.VERSION.SDK_INT >= 33) {
        item {
            ListItem(
                colors = listItemColors(),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.language),
                        contentDescription = null,
                    )
                },
                headlineContent = { Text(text = stringResource(Res.string.language)) },
                supportingContent = {
                    Text(text = LocalLocale.current.platformLocale.displayLanguage)
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_forward_ios),
                        contentDescription = "Navigate",
                    )
                },
                modifier = Modifier.clip(detachedItemShape()).clickable { onClick() },
            )
        }
    }
}
