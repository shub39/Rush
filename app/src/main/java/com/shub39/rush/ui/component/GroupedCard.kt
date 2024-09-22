package com.shub39.rush.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.shub39.rush.R
import com.shub39.rush.database.Song
import org.koin.compose.koinInject

@Composable
fun GroupedCard(
    map: Map.Entry<String?, List<Song>>,
    isExpanded: Boolean = false,
    onClick: (Song) -> Unit,
    onCardClick: () -> Unit,
    imageLoader: ImageLoader = koinInject()
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 4.dp, top = 4.dp)
            .clickable { onCardClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArtFromUrl(
                imageUrl = map.value.first().artUrl,
                imageLoader = imageLoader,
                modifier = Modifier
                    .size(60.dp)
                    .clip(MaterialTheme.shapes.small)
            )
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = map.key ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = map.value.size.toString() + " " + stringResource(id = R.string.downloaded),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 4.dp)
            ) {
                map.value.forEachIndexed { index, song ->
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable { onClick(song) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = index.plus(1).toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(4.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(text = song.title)
                    }
                }
            }
        }
    }
}