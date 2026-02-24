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

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.tooling.preview.Preview
import com.shub39.rush.R

val TYPOGRAPHY = Typography()

@OptIn(ExperimentalTextApi::class)
@Composable
fun provideTypography(font: Int? = R.font.poppins_regular): Typography {
    val flexFontDisplay =
        FontFamily(
            Font(
                resId = R.font.google_sans_flex,
                variationSettings =
                    FontVariation.Settings(
                        FontVariation.weight(900),
                        FontVariation.slant(-6f),
                        FontVariation.width(120f),
                    ),
            )
        )
    val flexFontHeadline =
        FontFamily(
            Font(
                resId = R.font.google_sans_flex,
                variationSettings =
                    FontVariation.Settings(
                        FontVariation.weight(800),
                        FontVariation.slant(-6f),
                        FontVariation.width(110f),
                    ),
            )
        )
    val flexFontTitle =
        FontFamily(
            Font(
                resId = R.font.google_sans_flex,
                variationSettings =
                    FontVariation.Settings(
                        FontVariation.weight(500),
                        FontVariation.Setting("ROND", 100f),
                    ),
            )
        )
    val selectedFont = font?.let { FontFamily(Font(it)) } ?: FontFamily.Default

    return Typography(
        displayLarge = TYPOGRAPHY.displayLarge.copy(fontFamily = flexFontDisplay),
        displayMedium = TYPOGRAPHY.displayMedium.copy(fontFamily = flexFontDisplay),
        displaySmall = TYPOGRAPHY.displaySmall.copy(fontFamily = flexFontDisplay),
        headlineLarge = TYPOGRAPHY.headlineLarge.copy(fontFamily = flexFontHeadline),
        headlineMedium = TYPOGRAPHY.headlineMedium.copy(fontFamily = flexFontHeadline),
        headlineSmall = TYPOGRAPHY.headlineSmall.copy(fontFamily = flexFontHeadline),
        titleLarge = TYPOGRAPHY.titleLarge.copy(fontFamily = flexFontTitle),
        titleMedium = TYPOGRAPHY.titleMedium.copy(fontFamily = flexFontTitle),
        titleSmall = TYPOGRAPHY.titleSmall.copy(fontFamily = flexFontTitle),
        bodyLarge = TYPOGRAPHY.bodyLarge.copy(fontFamily = selectedFont),
        bodyMedium = TYPOGRAPHY.bodyMedium.copy(fontFamily = selectedFont),
        bodySmall = TYPOGRAPHY.bodySmall.copy(fontFamily = selectedFont),
        labelLarge = TYPOGRAPHY.labelLarge.copy(fontFamily = selectedFont),
        labelMedium = TYPOGRAPHY.labelMedium.copy(fontFamily = selectedFont),
        labelSmall = TYPOGRAPHY.labelSmall.copy(fontFamily = selectedFont),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun TypographyPreview() {
    val typography = provideTypography()
    Column {
        Text("Display Large", style = typography.displayLarge)
        Text("Display Medium", style = typography.displayMedium)
        Text("Display Small", style = typography.displaySmall)
        Text("Headline Large", style = typography.headlineLarge)
        Text("Headline Medium", style = typography.headlineMedium)
        Text("Headline Small", style = typography.headlineSmall)
        Text("Title Large", style = typography.titleLarge)
        Text("Title Medium", style = typography.titleMedium)
        Text("Title Small", style = typography.titleSmall)
        Text("Body Large", style = typography.bodyLarge)
        Text("Body Medium", style = typography.bodyMedium)
        Text("Body Small", style = typography.bodySmall)
        Text("Label Large", style = typography.labelLarge)
        Text("Label Medium", style = typography.labelMedium)
        Text("Label Small", style = typography.labelSmall)
    }
}
