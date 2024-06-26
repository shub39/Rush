package com.shub39.rush.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.shub39.rush.database.SearchResult

@Composable
fun SearchResultCard(
    result: SearchResult,
    onClick: () -> Unit,
    imageLoader: ImageLoader
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        onClick = { onClick() }
    ) {
        Row {
            ArtFromUrl(
                imageUrl = result.artUrl,
                contentDescription = result.title,
                modifier = Modifier
                    .size(150.dp)
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.small),
                imageLoader = imageLoader
            )
            Column(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = result.artist,
                    style = MaterialTheme.typography.bodyMedium
                )
                result.album?.let {
                    Text(text = result.album, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}