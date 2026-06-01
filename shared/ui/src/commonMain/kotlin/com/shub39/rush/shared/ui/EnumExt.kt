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
package com.shub39.rush.shared.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import com.shub39.rush.shared.core.enums.AlbumArtShape
import com.shub39.rush.shared.core.enums.AppTheme
import com.shub39.rush.shared.core.enums.CardColors
import com.shub39.rush.shared.core.enums.CardFit
import com.shub39.rush.shared.core.enums.CardTheme
import com.shub39.rush.shared.core.enums.CornerRadius
import com.shub39.rush.shared.core.enums.Fonts
import com.shub39.rush.shared.core.enums.LyricsAlignment
import com.shub39.rush.shared.core.enums.LyricsBackground
import com.shub39.rush.shared.core.enums.PaletteStyle
import com.shub39.rush.shared.core.enums.SortOrder
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.StringResource
import rush.shared.ui.generated.resources.*

fun SortOrder.toStringRes(): StringResource {
    return when (this) {
        SortOrder.DATE_ADDED -> Res.string.sort_date_added
        SortOrder.TITLE_ASC -> Res.string.sort_title_asc
        SortOrder.TITLE_DESC -> Res.string.sort_title_desc
    }
}

fun LyricsBackground.toStringRes(): StringResource {
    return when (this) {
        LyricsBackground.HYPNOTIC -> Res.string.hypnotic
        LyricsBackground.ALBUM_ART -> Res.string.blurred_art
        LyricsBackground.SOLID_COLOR -> Res.string.solid_color
        LyricsBackground.WAVE -> Res.string.wave
        LyricsBackground.GRADIENT -> Res.string.gradient
        LyricsBackground.CURVE -> Res.string.curve
    }
}

expect val allBackgrounds: List<LyricsBackground>

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

fun AppTheme.toStringRes(): StringResource {
    return when (this) {
        AppTheme.SYSTEM -> Res.string.system
        AppTheme.LIGHT -> Res.string.light
        AppTheme.DARK -> Res.string.dark
    }
}

fun CardColors.toStringRes(): StringResource {
    return when (this) {
        CardColors.MUTED -> Res.string.muted
        CardColors.VIBRANT -> Res.string.vibrant
        CardColors.CUSTOM -> Res.string.custom
    }
}

fun CardFit.toStringRes(): StringResource {
    return when (this) {
        CardFit.FIT -> Res.string.fit
        CardFit.STANDARD -> Res.string.standard
    }
}

fun CardTheme.toStringRes(): StringResource {
    return when (this) {
        CardTheme.SPOTIFY -> Res.string.spotify
        CardTheme.RUSHED -> Res.string.rushed
        CardTheme.VERTICAL -> Res.string.vertical
        CardTheme.COUPLET -> Res.string.couplet
        CardTheme.ALBUM_ART -> Res.string.album_art
        CardTheme.HYPNOTIC -> Res.string.hypnotic
        CardTheme.QUOTE -> Res.string.quote
        CardTheme.MESSY -> Res.string.messy
        CardTheme.CHAT -> Res.string.chat
    }
}

fun CornerRadius.toStringRes(): StringResource {
    return when (this) {
        CornerRadius.DEFAULT -> Res.string.default_
        CornerRadius.ROUNDED -> Res.string.rounded
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

fun Fonts.toFontRes(): FontResource? {
    return when (this) {
        Fonts.SYSTEM_DEFAULT -> null
        Fonts.POPPINS -> Res.font.poppins_regular
        Fonts.DM_SANS -> Res.font.dm_sans
        Fonts.FIGTREE -> Res.font.figtree
        Fonts.INTER -> Res.font.inter
        Fonts.MANROPE -> Res.font.manrope
        Fonts.MONTSERRAT -> Res.font.montserrat
        Fonts.OPEN_SANS -> Res.font.open_sans
        Fonts.OUTFIT -> Res.font.outfit
        Fonts.QUICKSAND -> Res.font.quicksand
        Fonts.JOSH -> Res.font.jost
        Fonts.GOOGLE_SANS -> Res.font.google_sans_flex
    }
}

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
