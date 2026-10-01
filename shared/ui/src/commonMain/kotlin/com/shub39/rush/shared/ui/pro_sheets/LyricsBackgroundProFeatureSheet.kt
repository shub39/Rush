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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.dataclasses.WaveColors
import com.shub39.rush.shared.core.enums.LyricsBackground
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.lyrics.ApplyLyricsBackground
import com.shub39.rush.shared.ui.lyrics.TextPrefs
import com.shub39.rush.shared.ui.lyrics.component.PlainLyric
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun LyricsBackgroundProFeatureSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState =
        rememberBottomSheetState(
            initialValue = SheetValue.Hidden,
            enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
        ),
    onDismissRequest: () -> Unit,
    onNavigateToPaywall: () -> Unit
) {
    BaseProSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        onNavigateToPaywall = onNavigateToPaywall,
        sheetState = sheetState,
        title = {
            Text(
                text = "Customize Backgrounds",
                style = MaterialTheme.typography.titleLarge
            )
        }
    ) {
        BackgroundPreview(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
    }
}

@Composable
private fun BackgroundPreview(
    modifier: Modifier = Modifier
) {
    val backgrounds = remember { LyricsBackground.entries }
    var selectedIndex by remember { mutableIntStateOf(0) }

    var waveData by remember {
        mutableStateOf(
            listOf<Byte>(
                15, 30, 60, 90, 120, 80, 50, 70, 100, 110, 85, 45, 65, 95, 75, 40,
                20, 55, 85, 105, 90, 60, 35, 70, 100, 80, 50, 30, 65, 90, 45, 25
            )
        )
    }

    LaunchedEffect(backgrounds.size) {
        if (backgrounds.isNotEmpty()) {
            while (true) {
                delay(3.seconds)
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

    val cardBackground = Color(0xFF160E2A)
    val contentColor = Color.White
    val hypnoticColor1 = Color(0xFFFF2A85)
    val hypnoticColor2 = Color(0xFF00E5FF)
    val waveAccentColor = Color(0xFF00F5D4)

    val waveColors = remember {
        WaveColors(
            cardBackground = cardBackground.toArgb(),
            cardWaveBackground = waveAccentColor.toArgb()
        )
    }

    val sampleLyrics = remember {
        listOf(
            1 to "When the night has come",
            2 to "And the land is dark",
            3 to "And the moon is the only light we'll see",
            4 to "No I won't be afraid"
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().height(280.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = currentBackground,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier.fillMaxSize(),
                    label = "BackgroundTransition"
                ) { bg ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        ApplyLyricsBackground(
                            background = bg,
                            artUrl = null,
                            cardBackground = cardBackground,
                            waveData = waveData,
                            waveColors = waveColors,
                            hypnoticColor1 = hypnoticColor1,
                            hypnoticColor2 = hypnoticColor2
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sampleLyrics.forEach { lyric ->
                        PlainLyric(
                            textPrefs = TextPrefs(fontSize = 18f, lineHeight = 22f),
                            entry = lyric,
                            romanizedText = null,
                            onClick = {},
                            containerColor = Color.Transparent,
                            cardContent = contentColor
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
    BackgroundPreview(Modifier.heightIn(max = 700.dp).fillMaxWidth())
}
