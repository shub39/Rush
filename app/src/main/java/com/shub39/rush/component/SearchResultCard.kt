package com.shub39.rush.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.shub39.rush.R
import com.shub39.rush.database.SearchResult
import org.koin.compose.koinInject

@Composable
fun SearchResultCard(
    result: SearchResult,
    onClick: () -> Unit,
    downloaded: Boolean = false,
    imageLoader: ImageLoader = koinInject(),
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            .clickable {
                onClick()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArtFromUrl(
                imageUrl = result.artUrl,
                contentDescription = result.title,
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.small),
                imageLoader = imageLoader
            )

            Column(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = result.artist,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (downloaded) {
            Icon(
                painter = painterResource(id = R.drawable.round_download_done_24),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd).padding(end =16.dp),
            )
        }
    }
}