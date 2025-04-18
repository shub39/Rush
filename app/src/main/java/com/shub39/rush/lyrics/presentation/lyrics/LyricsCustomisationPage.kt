package com.shub39.rush.lyrics.presentation.lyrics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.MeshGradient
import com.shub39.rush.R
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.core.presentation.generateGradientColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsCustomisationsPage(
    state: LyricsPageState,
    action: (LyricsPageAction) -> Unit
) = PageFill {

    val (cardBackground, cardContent) = getCardColors(state)
    val (hypnoticColor1, hypnoticColor2) = getHypnoticColors(state)
    val hypnoticSpeed by animateFloatAsState(targetValue = state.meshSpeed)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.customisations))
                }
            )
        },
        modifier = Modifier.widthIn(max = 500.dp)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .let {
                            if (state.hypnoticCanvas) {
                                it.shaderBackground(
                                    shader = MeshGradient(
                                        colors = generateGradientColors(
                                            color1 = hypnoticColor1,
                                            color2 = hypnoticColor2,
                                            steps = 6
                                        ).toTypedArray()
                                    ),
                                    speed = hypnoticSpeed,
                                    fallback = {
                                        Brush.horizontalGradient(
                                            generateGradientColors(
                                                color1 = hypnoticColor1,
                                                color2 = hypnoticColor2,
                                                steps = 6
                                            )
                                        )
                                    }
                                )
                            } else {
                                it
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.hypnoticCanvas) Color.Transparent else cardBackground,
                        contentColor = cardContent
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "This is a very very long text depicting how lyrics should appear based on these settings",
                            textAlign = state.textAlign,
                            letterSpacing = 13.sp,
                            lineHeight = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview () {
    RushTheme {
        LyricsCustomisationsPage(
            state = LyricsPageState(
                textAlign = TextAlign.Center
            ),
            action = {}
        )
    }
}