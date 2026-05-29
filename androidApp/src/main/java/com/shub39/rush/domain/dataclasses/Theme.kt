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
package com.shub39.rush.domain.dataclasses

import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.enums.Fonts
import com.shub39.rush.domain.enums.PaletteStyle

/**
 * Represents the user's selected theme preferences
 *
 * @property seedColor The primary color used to generate the color palette, stored as an ARGB
 *   integer. Defaults to white.
 * @property appTheme The user's preferred theme mode (Light, Dark, or System default).
 * @property withAmoled A flag to indicate if an AMOLED-friendly (pure black) dark theme should be
 *   used.
 * @property style The [PaletteStyle] from Material Kolor library, defining the color generation
 *   algorithm.
 * @property materialTheme A flag to indicate if Material You (dynamic theming) is enabled.
 * @property font The selected [Fonts] enum for the application's typography.
 */
data class Theme(
    val seedColor: Int = 0xFFFFFF,
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val withAmoled: Boolean = false,
    val style: PaletteStyle = PaletteStyle.TONALSPOT,
    val materialTheme: Boolean = false,
    val font: Fonts = Fonts.FIGTREE,
)
