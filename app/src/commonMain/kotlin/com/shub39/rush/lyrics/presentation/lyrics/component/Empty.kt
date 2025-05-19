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
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Music
import org.jetbrains.compose.resources.stringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.empty
import rush.app.generated.resources.suggestion

@Composable
fun Empty(
    suggestion: Boolean = true
) {
    val color = Color.LightGray

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = FontAwesomeIcons.Solid.Music,
            contentDescription = "Empty Library",
            modifier = Modifier.size(128.dp).padding(16.dp),
            tint = color
        )

        Text(
            text = stringResource(Res.string.empty),
            color = color
        )

        if (suggestion) {
            Text(
                text = stringResource(Res.string.suggestion),
                color = color
            )
        }
    }
}