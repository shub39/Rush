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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.dataclasses.WaveColors
import com.shub39.rush.shared.core.enums.LyricsBackground
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.lyrics.ApplyLyricsBackground
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import com.shub39.rush.shared.ui.theme.flexFontRounded
import com.shub39.rush.shared.ui.toStringRes
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
fun LyricsBackgroundProFeatureSheet(
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
                text = "Customize Backgrounds",
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        fontFamily = flexFontEmphasis(),
                        textAlign = TextAlign.Center,
                    ),
            )
        },
    ) {
        BackgroundPreview(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
    }
}

@Composable
private fun BackgroundPreview(modifier: Modifier = Modifier) {
    val backgrounds = remember { LyricsBackground.entries }
    var selectedIndex by remember { mutableIntStateOf(0) }

    var waveData by remember {
        mutableStateOf(
            listOf<Byte>(
                15,
                30,
                60,
                90,
                120,
                80,
                50,
                70,
                100,
                110,
                85,
                45,
                65,
                95,
                75,
                40,
                20,
                55,
                85,
                105,
                90,
                60,
                35,
                70,
                100,
                80,
                50,
                30,
                65,
                90,
                45,
                25,
            )
        )
    }

    LaunchedEffect(backgrounds.size) {
        if (backgrounds.isNotEmpty()) {
            while (true) {
                delay(1.seconds)
                selectedIndex = (selectedIndex + 1) % backgrounds.size
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(100.milliseconds)
            waveData = waveData.map { (it + ((-12..12).random())).coerceIn(10, 120).toByte() }
        }
    }

    val currentBackground = backgrounds.getOrElse(selectedIndex) { LyricsBackground.SOLID_COLOR }

    val cardBackground = Color(0xFF0068B4)
    val cardContent = Color(0xFFB1D1F3)

    val waveColors = remember {
        WaveColors(
            cardBackground = cardBackground.toArgb(),
            cardWaveBackground = cardContent.toArgb(),
        )
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().height(280.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = currentBackground,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier.fillMaxSize(),
                    label = "BackgroundTransition",
                ) { bg ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        ApplyLyricsBackground(
                            background = bg,
                            artUrl = null,
                            cardBackground = cardBackground,
                            waveData = waveData,
                            waveColors = waveColors,
                            hypnoticColor1 = cardBackground,
                            hypnoticColor2 = cardContent,
                        )
                    }
                }

                Text(
                    text = stringResource(currentBackground.toStringRes()),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                    style =
                        MaterialTheme.typography.headlineMedium.copy(fontFamily = flexFontRounded()),
                )
            }
        }
    }
}

@Preview
@PreviewWrapper(RushPreviewWrapper::class)
@Composable
private fun Preview() {
    BackgroundPreview(Modifier.heightIn(max = 700.dp).fillMaxWidth())
}
