package com.shub39.rush.domain.dataclasses

import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.enums.Fonts
import com.shub39.rush.domain.enums.PaletteStyle

/**
 * Represents the user's selected theme preferences
 *
 * @property seedColor The primary color used to generate the color palette, stored as an ARGB integer. Defaults to white.
 * @property appTheme The user's preferred theme mode (Light, Dark, or System default).
 * @property withAmoled A flag to indicate if an AMOLED-friendly (pure black) dark theme should be used.
 * @property style The [PaletteStyle] from Material Kolor library, defining the color generation algorithm.
 * @property materialTheme A flag to indicate if Material You (dynamic theming) is enabled.
 * @property font The selected [Fonts] enum for the application's typography.
 */
data class Theme(
    val seedColor: Int = 0xFFFFFF,
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val withAmoled: Boolean = false,
    val style: PaletteStyle = PaletteStyle.TONALSPOT,
    val materialTheme: Boolean = false,
    val font: Fonts = Fonts.FIGTREE
)