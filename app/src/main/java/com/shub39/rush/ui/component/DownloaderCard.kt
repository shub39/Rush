package com.shub39.rush.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.R

@Composable
fun DownloaderCard(
    title: String,
    artist: String,
    state: Boolean?,
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            when (state) {
                true -> Icon(
                    painter = painterResource(R.drawable.round_check_circle_outline_24),
                    contentDescription = null
                )

                null -> Icon(
                    painter = painterResource(R.drawable.round_sync_24),
                    contentDescription = null
                )

                else -> Icon(
                    painter = painterResource(R.drawable.round_error_outline_24),
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.padding(4.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = artist,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}