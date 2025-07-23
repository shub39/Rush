package com.shub39.rush.core.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.BoxOpen

@Composable
fun Empty(
    imageVector: ImageVector = FontAwesomeIcons.Solid.BoxOpen,
    suggestion: Boolean = true,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Empty Library",
            modifier = Modifier.size(128.dp).padding(16.dp),
            tint = color
        )

        Text(
            text = stringResource(R.string.empty),
            color = color
        )

        if (suggestion) {
            Text(
                text = stringResource(R.string.suggestion),
                color = color
            )
        }
    }
}