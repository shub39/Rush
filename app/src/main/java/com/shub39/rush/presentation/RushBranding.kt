package com.shub39.rush.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.shub39.rush.R
import com.shub39.rush.presentation.share.fromPx

@Composable
fun RushBranding(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.app_icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .scale(2.5f)
                .size(_root_ide_package_.com.shub39.rush.presentation.share.pxToDp(60))
        )

        Text(
            text = "Rush",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = color
            ).fromPx(
                fontSize = 36,
                letterSpacing = 0,
                lineHeight = 0,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Preview
@Composable
private fun Preview() {
    RushBranding()
}