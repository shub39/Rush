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
        DATE_ADDED -> Res.string.sort_date_added
        TITLE_ASC -> Res.string.sort_title_asc
        TITLE_DESC -> Res.string.sort_title_desc
    }
}

fun LyricsBackground.toStringRes(): StringResource {
    return when (this) {
        HYPNOTIC -> Res.string.hypnotic
        ALBUM_ART -> Res.string.blurred_art
        SOLID_COLOR -> Res.string.solid_color
        WAVE -> Res.string.wave
        GRADIENT -> Res.string.gradient
        CURVE -> Res.string.curve
    }
}

expect val allBackgrounds: List<LyricsBackground>

val premiumCards: List<CardTheme> = listOf(HYPNOTIC, MESSY, QUOTE, CHAT)

val audioDependentBackgrounds: List<LyricsBackground> = listOf(WAVE, GRADIENT, CURVE)

fun PaletteStyle.toMPaletteStyle(): com.materialkolor.PaletteStyle {
    return when (this) {
        TONALSPOT -> TonalSpot
        NEUTRAL -> Neutral
        VIBRANT -> Vibrant
        EXPRESSIVE -> Expressive
        RAINBOW -> Rainbow
        FRUITSALAD -> FruitSalad
        MONOCHROME -> Monochrome
        FIDELITY -> Fidelity
        CONTENT -> Content
    }
}

fun LyricsAlignment.toArrangement(): Arrangement.Horizontal {
    return when (this) {
        CENTER -> Arrangement.Center
        END -> Arrangement.End
        START -> Arrangement.Start
    }
}

fun LyricsAlignment.toTextAlignment(): TextAlign {
    return when (this) {
        CENTER -> TextAlign.Center
        END -> TextAlign.End
        START -> TextAlign.Start
    }
}

fun LyricsAlignment.toAlignment(): Alignment.Horizontal {
    return when (this) {
        CENTER -> Alignment.CenterHorizontally
        END -> Alignment.End
        START -> Alignment.Start
    }
}

fun AppTheme.toStringRes(): StringResource {
    return when (this) {
        SYSTEM -> Res.string.system
        LIGHT -> Res.string.light
        DARK -> Res.string.dark
    }
}

fun CardColors.toStringRes(): StringResource {
    return when (this) {
        MUTED -> Res.string.muted
        VIBRANT -> Res.string.vibrant
        CUSTOM -> Res.string.custom
    }
}

fun CardFit.toStringRes(): StringResource {
    return when (this) {
        FIT -> Res.string.fit
        STANDARD -> Res.string.standard
    }
}

fun CardTheme.toStringRes(): StringResource {
    return when (this) {
        SPOTIFY -> Res.string.spotify
        RUSHED -> Res.string.rushed
        VERTICAL -> Res.string.vertical
        COUPLET -> Res.string.couplet
        ALBUM_ART -> Res.string.album_art
        HYPNOTIC -> Res.string.hypnotic
        QUOTE -> Res.string.quote
        MESSY -> Res.string.messy
        CHAT -> Res.string.chat
    }
}

fun CornerRadius.toStringRes(): StringResource {
    return when (this) {
        DEFAULT -> Res.string.default_
        ROUNDED -> Res.string.rounded
    }
}

fun Fonts.toFullName(): String {
    return when (this) {
        SYSTEM_DEFAULT -> "System Default"
        FIGTREE -> "Figtree"
        INTER -> "Inter"
        MANROPE -> "Manrope"
        MONTSERRAT -> "Montserrat"
        OPEN_SANS -> "Open Sans"
        OUTFIT -> "Outfit"
        POPPINS -> "Poppins"
        DM_SANS -> "DM Sans"
        QUICKSAND -> "QuickSand"
        JOSH -> "Josh"
        GOOGLE_SANS -> "Google Sans"
    }
}

fun Fonts.toFontRes(): FontResource? {
    return when (this) {
        SYSTEM_DEFAULT -> null
        POPPINS -> Res.font.poppins_regular
        DM_SANS -> Res.font.dm_sans
        FIGTREE -> Res.font.figtree
        INTER -> Res.font.inter
        MANROPE -> Res.font.manrope
        MONTSERRAT -> Res.font.montserrat
        OPEN_SANS -> Res.font.open_sans
        OUTFIT -> Res.font.outfit
        QUICKSAND -> Res.font.quicksand
        JOSH -> Res.font.jost
        GOOGLE_SANS -> Res.font.google_sans_flex
    }
}

@Composable
fun AlbumArtShape.toMaterialShape(): Shape {
    return when (this) {
        CIRCLE -> MaterialShapes.Circle
        SUNNY -> MaterialShapes.Sunny
        VERY_SUNNY -> MaterialShapes.VerySunny
        SQUARE -> MaterialShapes.Square
        SLANTED -> MaterialShapes.Slanted
        ARCH -> MaterialShapes.Arch
        PILL -> MaterialShapes.Pill
        PENTAGON -> MaterialShapes.Pentagon
        GEM -> MaterialShapes.Gem
        COOKIE_4 -> MaterialShapes.Cookie4Sided
        COOKIE_6 -> MaterialShapes.Cookie6Sided
        COOKIE_9 -> MaterialShapes.Cookie9Sided
        COOKIE_12 -> MaterialShapes.Cookie12Sided
        CLOVER_4 -> MaterialShapes.Clover4Leaf
        CLOVER_8 -> MaterialShapes.Clover8Leaf
        GHOSTISH -> MaterialShapes.Ghostish
        FLOWER -> MaterialShapes.Flower
        SOFT_BURST -> MaterialShapes.SoftBurst
        PUFFY_DIAMOND -> MaterialShapes.PuffyDiamond
        BUN -> MaterialShapes.Bun
        HEART -> MaterialShapes.Heart
    }.toShape()
}
