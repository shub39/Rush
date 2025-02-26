package com.shub39.rush.lyrics.presentation.lyrics.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R

@Composable
fun Empty(
    suggestion: Boolean = true
) {
    val color = Color.LightGray.copy(0.5f)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_library_music_24),
            contentDescription = null,
            modifier = Modifier.size(128.dp).padding(16.dp),
            tint = color
        )

        Text(
            text = stringResource(id = R.string.empty),
            color = color
        )

        if (suggestion) {
            Text(
                text = stringResource(id = R.string.suggestion),
                color = color
            )
        }
    }
}