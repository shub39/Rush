package com.shub39.rush.lyrics.presentation.saved.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.lyrics.domain.Song

@Composable
fun SongCard(
    result: Song,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = 15.dp, bottom = 7.5.dp, top = 7.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            ArtFromUrl(
                imageUrl = result.artUrl,
                modifier = Modifier
                    .size(70.dp)
                    .clip(MaterialTheme.shapes.small),
            )
            Column(
                modifier = Modifier
                    .padding(start = 10.dp)
            ) {
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = result.artists,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row {
            IconButton(onClick = { onDelete() }) {
                Icon(
                    painter = painterResource(id = R.drawable.round_delete_forever_24),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
        }
    }
}