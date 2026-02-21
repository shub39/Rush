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
package com.shub39.rush.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import com.shub39.rush.R
import com.shub39.rush.domain.enums.AlbumArtShape
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.enums.CardColors
import com.shub39.rush.domain.enums.CardFit
import com.shub39.rush.domain.enums.CardTheme
import com.shub39.rush.domain.enums.CornerRadius
import com.shub39.rush.domain.enums.Fonts
import com.shub39.rush.domain.enums.LyricsAlignment
import com.shub39.rush.domain.enums.LyricsBackground
import com.shub39.rush.domain.enums.PaletteStyle
import com.shub39.rush.domain.enums.SortOrder

fun SortOrder.toStringRes(): Int {
    return when (this) {
        SortOrder.DATE_ADDED -> R.string.sort_date_added
        SortOrder.TITLE_ASC -> R.string.sort_title_asc
        SortOrder.TITLE_DESC -> R.string.sort_title_desc
    }
}

fun LyricsBackground.toStringRes(): Int {
    return when (this) {
        LyricsBackground.HYPNOTIC -> R.string.hypnotic
        LyricsBackground.ALBUM_ART -> R.string.blurred_art
        LyricsBackground.SOLID_COLOR -> R.string.solid_color
        LyricsBackground.WAVE -> R.string.wave
        LyricsBackground.GRADIENT -> R.string.gradient
        LyricsBackground.CURVE -> R.string.curve
    }
}

val allBackgrounds =
    if (hypnoticAvailable()) {
        listOf(
            LyricsBackground.SOLID_COLOR,
            LyricsBackground.ALBUM_ART,
            LyricsBackground.HYPNOTIC,
            LyricsBackground.WAVE,
            LyricsBackground.GRADIENT,
            LyricsBackground.CURVE,
        )
    } else if (blurAvailable()) {
        listOf(
            LyricsBackground.SOLID_COLOR,
            LyricsBackground.ALBUM_ART,
            LyricsBackground.WAVE,
            LyricsBackground.GRADIENT,
        )
    } else {
        listOf(LyricsBackground.SOLID_COLOR, LyricsBackground.WAVE, LyricsBackground.GRADIENT)
    }

val premiumCards =
    listOf(LyricsBackground.HYPNOTIC, CardTheme.MESSY, CardTheme.QUOTE, CardTheme.CHAT)

val audioDependentBackgrounds =
    listOf(LyricsBackground.WAVE, LyricsBackground.GRADIENT, LyricsBackground.CURVE)

fun PaletteStyle.toMPaletteStyle(): com.materialkolor.PaletteStyle {
    return when (this) {
        PaletteStyle.TONALSPOT -> com.materialkolor.PaletteStyle.TonalSpot
        PaletteStyle.NEUTRAL -> com.materialkolor.PaletteStyle.Neutral
        PaletteStyle.VIBRANT -> com.materialkolor.PaletteStyle.Vibrant
        PaletteStyle.EXPRESSIVE -> com.materialkolor.PaletteStyle.Expressive
        PaletteStyle.RAINBOW -> com.materialkolor.PaletteStyle.Rainbow
        PaletteStyle.FRUITSALAD -> com.materialkolor.PaletteStyle.FruitSalad
        PaletteStyle.MONOCHROME -> com.materialkolor.PaletteStyle.Monochrome
        PaletteStyle.FIDELITY -> com.materialkolor.PaletteStyle.Fidelity
        PaletteStyle.CONTENT -> com.materialkolor.PaletteStyle.Content
    }
}

fun LyricsAlignment.toArrangement(): Arrangement.Horizontal {
    return when (this) {
        LyricsAlignment.CENTER -> Arrangement.Center
        LyricsAlignment.END -> Arrangement.End
        LyricsAlignment.START -> Arrangement.Start
    }
}

fun LyricsAlignment.toTextAlignment(): TextAlign {
    return when (this) {
        LyricsAlignment.CENTER -> TextAlign.Center
        LyricsAlignment.END -> TextAlign.End
        LyricsAlignment.START -> TextAlign.Start
    }
}

fun LyricsAlignment.toAlignment(): Alignment.Horizontal {
    return when (this) {
        LyricsAlignment.CENTER -> Alignment.CenterHorizontally
        LyricsAlignment.END -> Alignment.End
        LyricsAlignment.START -> Alignment.Start
    }
}

fun AppTheme.toStringRes(): Int {
    return when (this) {
        AppTheme.SYSTEM -> R.string.system
        AppTheme.LIGHT -> R.string.light
        AppTheme.DARK -> R.string.dark
    }
}

fun CardColors.toStringRes(): Int {
    return when (this) {
        CardColors.MUTED -> R.string.muted
        CardColors.VIBRANT -> R.string.vibrant
        CardColors.CUSTOM -> R.string.custom
    }
}

fun CardFit.toStringRes(): Int {
    return when (this) {
        CardFit.FIT -> R.string.fit
        CardFit.STANDARD -> R.string.standard
    }
}

fun CardTheme.toStringRes(): Int {
    return when (this) {
        CardTheme.SPOTIFY -> R.string.spotify
        CardTheme.RUSHED -> R.string.rushed
        CardTheme.VERTICAL -> R.string.vertical
        CardTheme.COUPLET -> R.string.couplet
        CardTheme.ALBUM_ART -> R.string.album_art
        CardTheme.HYPNOTIC -> R.string.hypnotic
        CardTheme.QUOTE -> R.string.quote
        CardTheme.MESSY -> R.string.messy
        CardTheme.CHAT -> R.string.chat
    }
}

fun CornerRadius.toStringRes(): Int {
    return when (this) {
        CornerRadius.DEFAULT -> R.string.default_
        CornerRadius.ROUNDED -> R.string.rounded
    }
}

fun Fonts.toFullName(): String {
    return when (this) {
        Fonts.SYSTEM_DEFAULT -> "System Default"
        Fonts.FIGTREE -> "Figtree"
        Fonts.INTER -> "Inter"
        Fonts.MANROPE -> "Manrope"
        Fonts.MONTSERRAT -> "Montserrat"
        Fonts.OPEN_SANS -> "Open Sans"
        Fonts.OUTFIT -> "Outfit"
        Fonts.POPPINS -> "Poppins"
        Fonts.DM_SANS -> "DM Sans"
        Fonts.QUICKSAND -> "QuickSand"
        Fonts.JOSH -> "Josh"
        Fonts.GOOGLE_SANS -> "Google Sans"
    }
}

fun Fonts.toFontRes(): Int? {
    return when (this) {
        Fonts.SYSTEM_DEFAULT -> null
        Fonts.POPPINS -> R.font.poppins_regular
        Fonts.DM_SANS -> R.font.dm_sans
        Fonts.FIGTREE -> R.font.figtree
        Fonts.INTER -> R.font.inter
        Fonts.MANROPE -> R.font.manrope
        Fonts.MONTSERRAT -> R.font.montserrat
        Fonts.OPEN_SANS -> R.font.open_sans
        Fonts.OUTFIT -> R.font.outfit
        Fonts.QUICKSAND -> R.font.quicksand
        Fonts.JOSH -> R.font.jost
        Fonts.GOOGLE_SANS -> R.font.google_sans
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AlbumArtShape.toShape(): Shape {
    return when (this) {
        AlbumArtShape.CIRCLE -> MaterialShapes.Circle.toShape()
        AlbumArtShape.SUNNY -> MaterialShapes.Sunny.toShape()
        AlbumArtShape.VERY_SUNNY -> MaterialShapes.VerySunny.toShape()
        AlbumArtShape.SQUARE -> MaterialShapes.Square.toShape()
        AlbumArtShape.SLANTED -> MaterialShapes.Slanted.toShape()
        AlbumArtShape.ARCH -> MaterialShapes.Arch.toShape()
        AlbumArtShape.PILL -> MaterialShapes.Pill.toShape()
        AlbumArtShape.PENTAGON -> MaterialShapes.Pentagon.toShape()
        AlbumArtShape.GEM -> MaterialShapes.Gem.toShape()
        AlbumArtShape.COOKIE_4 -> MaterialShapes.Cookie4Sided.toShape()
        AlbumArtShape.COOKIE_6 -> MaterialShapes.Cookie6Sided.toShape()
        AlbumArtShape.COOKIE_9 -> MaterialShapes.Cookie9Sided.toShape()
        AlbumArtShape.COOKIE_12 -> MaterialShapes.Cookie12Sided.toShape()
        AlbumArtShape.CLOVER_4 -> MaterialShapes.Clover4Leaf.toShape()
        AlbumArtShape.CLOVER_8 -> MaterialShapes.Clover8Leaf.toShape()
        AlbumArtShape.GHOSTISH -> MaterialShapes.Ghostish.toShape()
        AlbumArtShape.FLOWER -> MaterialShapes.Flower.toShape()
        AlbumArtShape.SOFT_BURST -> MaterialShapes.SoftBurst.toShape()
        AlbumArtShape.PUFFY_DIAMOND -> MaterialShapes.PuffyDiamond.toShape()
        AlbumArtShape.BUN -> MaterialShapes.Bun.toShape()
        AlbumArtShape.HEART -> MaterialShapes.Heart.toShape()
    }
}
