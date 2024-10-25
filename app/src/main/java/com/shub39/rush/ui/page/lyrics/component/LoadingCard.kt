package com.shub39.rush.ui.page.lyrics.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.rush.R

@Composable
fun LoadingCard(
    fetching: Pair<Boolean, String>,
    searching: Pair<Boolean, String>,
    colors: Pair<Color, Color>
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (fetching.first || searching.first) {
            Text(
                text = if (fetching.first) {
                    "${stringResource(R.string.fetching)} \n${fetching.second}"
                } else {
                    "${stringResource(R.string.searching)} \n${searching.second}"
                },
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CircularProgressIndicator(
            strokeCap = StrokeCap.Round,
            color = colors.first
        )
    }
}