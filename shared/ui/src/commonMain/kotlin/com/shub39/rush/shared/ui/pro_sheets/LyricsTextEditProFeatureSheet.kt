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
package com.shub39.rush.shared.ui.pro_sheets

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.shared.core.enums.LyricsAlignment
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

@Composable
fun LyricsTextEditProFeatureSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState =
        rememberBottomSheetState(
            initialValue = SheetValue.Hidden,
            enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
        ),
    onDismissRequest: () -> Unit,
    onNavigateToPaywall: () -> Unit,
) {
    BaseProSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        onNavigateToPaywall = onNavigateToPaywall,
        sheetState = sheetState,
        title = {
            Text(
                text = "Customize Text",
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        fontFamily = flexFontEmphasis(),
                        textAlign = TextAlign.Center,
                    ),
            )
        },
    ) {
        TextPreview(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
    }
}

private data class TextPreviewStep(
    val alignment: LyricsAlignment,
    val fontSize: Float,
    val letterSpacing: Float,
    val lineHeight: Float,
)

@Composable
private fun TextPreview(modifier: Modifier = Modifier) {
    val steps = remember {
        listOf(
            TextPreviewStep(
                alignment = LyricsAlignment.START,
                fontSize = 20f,
                letterSpacing = 0f,
                lineHeight = 28f,
            ),
            TextPreviewStep(
                alignment = LyricsAlignment.CENTER,
                fontSize = 26f,
                letterSpacing = 1.5f,
                lineHeight = 36f,
            ),
            TextPreviewStep(
                alignment = LyricsAlignment.END,
                fontSize = 30f,
                letterSpacing = -0.5f,
                lineHeight = 42f,
            ),
            TextPreviewStep(
                alignment = LyricsAlignment.CENTER,
                fontSize = 22f,
                letterSpacing = 2.5f,
                lineHeight = 32f,
            ),
            TextPreviewStep(
                alignment = LyricsAlignment.START,
                fontSize = 28f,
                letterSpacing = 0.5f,
                lineHeight = 40f,
            ),
        )
    }

    var selectedIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(steps.size) {
        if (steps.isNotEmpty()) {
            while (true) {
                delay(1.seconds)
                selectedIndex = (selectedIndex + 1) % steps.size
            }
        }
    }

    val currentStep = steps[selectedIndex]

    val targetBias =
        when (currentStep.alignment) {
            LyricsAlignment.START -> -1f
            LyricsAlignment.CENTER -> 0f
            LyricsAlignment.END -> 1f
        }

    val animatedBias by
        animateFloatAsState(
            targetValue = targetBias,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "alignmentBias",
        )

    val animatedFontSize by
        animateFloatAsState(
            targetValue = currentStep.fontSize,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "fontSize",
        )

    val animatedLetterSpacing by
        animateFloatAsState(
            targetValue = currentStep.letterSpacing,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "letterSpacing",
        )

    val animatedLineHeight by
        animateFloatAsState(
            targetValue = currentStep.lineHeight,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "lineHeight",
        )

    val textAlign =
        when {
            animatedBias < -0.33f -> TextAlign.Start
            animatedBias > 0.33f -> TextAlign.End
            else -> TextAlign.Center
        }

    val cardBackground = Color(0xFF0068B4)
    val cardContent = Color(0xFFB1D1F3)

    val sampleLyrics = remember {
        listOf("This is an example", "of custom lyrics text", "styled your way")
    }

    val density = LocalDensity.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().height(280.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                    horizontalAlignment = BiasAlignment.Horizontal(animatedBias),
                    verticalArrangement =
                        Arrangement.spacedBy(with(density) { (animatedLineHeight / 2).sp.toDp() }),
                ) {
                    sampleLyrics.forEach { line ->
                        Text(
                            text = line,
                            color = Color.White,
                            textAlign = textAlign,
                            style =
                                MaterialTheme.typography.headlineSmall.copy(
                                    fontSize = animatedFontSize.sp,
                                    letterSpacing = animatedLetterSpacing.sp,
                                    lineHeight = animatedLineHeight.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                        )
                    }
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.align(Alignment.BottomCenter),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val alignText =
                            when (currentStep.alignment) {
                                LyricsAlignment.START -> "Start"
                                LyricsAlignment.CENTER -> "Center"
                                LyricsAlignment.END -> "End"
                            }
                        Text(
                            text =
                                "$alignText • ${animatedFontSize.toInt()} • ${animatedLineHeight.toInt()} • ${((animatedLetterSpacing * 10).toInt() / 10f)}",
                            color = cardContent,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@PreviewWrapper(RushPreviewWrapper::class)
@Composable
private fun Preview() {
    TextPreview(Modifier.heightIn(max = 700.dp).fillMaxWidth())
}
