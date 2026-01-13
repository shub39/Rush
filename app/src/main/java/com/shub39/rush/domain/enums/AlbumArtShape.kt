package com.shub39.rush.domain.enums

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape

enum class AlbumArtShape {
    CIRCLE,
    SUNNY,
    VERY_SUNNY,
    SQUARE,
    SLANTED,
    ARCH,
    PILL,
    PENTAGON,
    GEM,
    COOKIE_4,
    COOKIE_6,
    COOKIE_9,
    COOKIE_12,
    CLOVER_4,
    CLOVER_8,
    GHOSTISH,
    FLOWER,
    SOFT_BURST,
    PUFFY_DIAMOND,
    BUN,
    HEART;

    companion object {
        @OptIn(ExperimentalMaterial3ExpressiveApi::class)
        @Composable
        fun AlbumArtShape.toShape(): Shape {
            return when(this) {
                CIRCLE -> MaterialShapes.Circle.toShape()
                SUNNY -> MaterialShapes.Sunny.toShape()
                VERY_SUNNY -> MaterialShapes.VerySunny.toShape()
                SQUARE -> MaterialShapes.Square.toShape()
                SLANTED -> MaterialShapes.Slanted.toShape()
                ARCH -> MaterialShapes.Arch.toShape()
                PILL -> MaterialShapes.Pill.toShape()
                PENTAGON -> MaterialShapes.Pentagon.toShape()
                GEM -> MaterialShapes.Gem.toShape()
                COOKIE_4 -> MaterialShapes.Cookie4Sided.toShape()
                COOKIE_6 -> MaterialShapes.Cookie6Sided.toShape()
                COOKIE_9 -> MaterialShapes.Cookie9Sided.toShape()
                COOKIE_12 -> MaterialShapes.Cookie12Sided.toShape()
                CLOVER_4 -> MaterialShapes.Clover4Leaf.toShape()
                CLOVER_8 -> MaterialShapes.Clover8Leaf.toShape()
                GHOSTISH -> MaterialShapes.Ghostish.toShape()
                FLOWER -> MaterialShapes.Flower.toShape()
                SOFT_BURST -> MaterialShapes.SoftBurst.toShape()
                PUFFY_DIAMOND -> MaterialShapes.PuffyDiamond.toShape()
                BUN -> MaterialShapes.Bun.toShape()
                HEART -> MaterialShapes.Heart.toShape()
            }
        }
    }
}