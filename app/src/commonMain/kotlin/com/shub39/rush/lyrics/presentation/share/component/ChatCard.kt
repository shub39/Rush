package com.shub39.rush.lyrics.presentation.share.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.darken
import com.shub39.rush.core.data.SongDetails
import com.shub39.rush.core.domain.CardFit
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.lyrics.presentation.share.getFormattedTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ChatCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    fit: CardFit
) {
    Card(
        modifier = modifier,
        shape = cardCorners,
        colors = CardDefaults.cardColors(
            containerColor = cardColors.containerColor.darken(2f),
            contentColor = cardColors.contentColor
        )
    ) {
        ListItem(
            leadingContent = {
                ArtFromUrl(
                    imageUrl = song.artUrl,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = cardColors.containerColor,
                headlineColor = cardColors.contentColor,
                supportingColor = cardColors.contentColor
            ),
            headlineContent = {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = with(LocalDensity.current) { 40.toSp() },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            supportingContent = {
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = with(LocalDensity.current) { 35.toSp() },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )

        Column(
            modifier = Modifier
                .padding(32.dp)
                .let {
                    if (fit == CardFit.STANDARD) {
                        it.fillMaxHeight()
                    } else it
                },
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                sortedLines.forEach {
                    item {
                        Card(
                            shape = MaterialTheme.shapes.small,
                            colors = cardColors.copy(
                                containerColor = cardColors.containerColor
                            )
                        ) {
                            Text(
                                text = it.value,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp),
                                fontSize = with(LocalDensity.current) { 35.toSp() },
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = getFormattedTime(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}