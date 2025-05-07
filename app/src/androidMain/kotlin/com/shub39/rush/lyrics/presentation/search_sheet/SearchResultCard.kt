package com.shub39.rush.lyrics.presentation.search_sheet

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.lyrics.domain.SearchResult
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Download

@Composable
fun SearchResultCard(
    result: SearchResult,
    onClick: () -> Unit,
    downloaded: Boolean = false
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
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.small),
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
                imageVector = FontAwesomeIcons.Solid.Download,
                contentDescription = "Downloaded",
                modifier = Modifier.size(20.dp).align(Alignment.CenterEnd).padding(end =16.dp),
            )
        }
    }
}