package com.shub39.rush.lyrics.presentation.setting.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.shub39.rush.R

@Composable
fun DownloaderCard(
    title: String,
    artist: String,
    state: Boolean?,
    listItemColors: ListItemColors
) {
    ListItem(
        headlineContent = {
            Column {
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = artist,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        trailingContent = {
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
        },
        colors = listItemColors,
        modifier = Modifier.clip(MaterialTheme.shapes.large)
    )
}