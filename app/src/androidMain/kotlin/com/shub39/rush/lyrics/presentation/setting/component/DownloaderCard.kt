package com.shub39.rush.lyrics.presentation.setting.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.lyrics.domain.AudioFile
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.CheckCircle
import compose.icons.fontawesomeicons.solid.SyncAlt

@Composable
fun DownloaderCard(
    audioFile: AudioFile,
    state: Boolean?,
    listItemColors: ListItemColors
) {
    ListItem(
        headlineContent = {
            Column {
                Text(
                    text = audioFile.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = audioFile.artist,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        trailingContent = {
            when (state) {
                true -> Icon(
                    imageVector = FontAwesomeIcons.Solid.CheckCircle,
                    contentDescription = "Done",
                    modifier = Modifier.size(20.dp)
                )

                null -> Icon(
                    imageVector = FontAwesomeIcons.Solid.SyncAlt,
                    contentDescription = "Sync",
                    modifier = Modifier.size(20.dp)
                )

                else -> Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        colors = listItemColors,
        modifier = Modifier.clip(MaterialTheme.shapes.large)
    )
}