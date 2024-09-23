package com.shub39.rush.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R

@Composable
fun DownloaderCard(
    title: String,
    artist: String,
    state: Boolean?
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            when (state) {
                true -> Image(
                    painter = painterResource(R.drawable.round_check_circle_outline_24),
                    contentDescription = null
                )

                null -> CircularProgressIndicator(
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.size(20.dp)
                )

                else -> Image(
                    painter = painterResource(R.drawable.round_error_outline_24),
                    contentDescription = null
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = artist,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}