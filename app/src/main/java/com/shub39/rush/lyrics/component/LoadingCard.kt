package com.shub39.rush.lyrics.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Save
import compose.icons.fontawesomeicons.solid.Search

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingCard(
    fetching: Pair<Boolean, String>,
    searching: Pair<Boolean, String>,
    colors: Pair<Color, Color>
) {
    AnimatedVisibility (
        visible = searching.first,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Search,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = colors.first
            )

            Spacer(modifier = Modifier.padding(16.dp))

            LinearWavyProgressIndicator(
                color = colors.first,
                trackColor = Color.Transparent
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = searching.second,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }

    AnimatedVisibility (
        visible = fetching.first,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Save,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = colors.first
            )

            Spacer(modifier = Modifier.padding(16.dp))

            LinearWavyProgressIndicator(
                color = colors.first,
                trackColor = Color.Transparent
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = fetching.second,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}