package com.shub39.rush.presentation.saved.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.domain.dataclasses.Song
import com.shub39.rush.presentation.components.ArtFromUrl

@Composable
fun SongCard(
    song: Song,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        leadingContent = {
            ArtFromUrl(
                imageUrl = song.artUrl,
                modifier = Modifier
                    .size(70.dp)
                    .clip(MaterialTheme.shapes.small),
            )
        },
        headlineContent = {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = song.artists,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        trailingContent = {
            IconButton(onClick = { onDelete() }) {
                Icon(
                    painter = painterResource(R.drawable.delete),
                    contentDescription = "Delete"
                )
            }
        }
    )
}