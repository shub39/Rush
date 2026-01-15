package com.shub39.rush.presentation.searchsheet.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.domain.dataclasses.SearchResult
import com.shub39.rush.presentation.components.ArtFromUrl

@Composable
fun SearchResultCard(
    result: SearchResult,
    onClick: () -> Unit,
    downloaded: Boolean = false
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.clickable { onClick() },
        leadingContent = {
            ArtFromUrl(
                imageUrl = result.artUrl,
                modifier = Modifier
                    .size(70.dp)
                    .clip(MaterialTheme.shapes.small)
            )
        },
        headlineContent = {
            Text(
                text = result.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )
        },
        supportingContent = {
            Text(
                text = result.artist,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        trailingContent = {
            if (downloaded) {
                Icon(
                    painter = painterResource(R.drawable.download),
                    contentDescription = "Downloaded",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}