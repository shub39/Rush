package com.shub39.rush.ui.page.share.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.ui.page.component.ArtFromUrl
import com.shub39.rush.ui.page.share.SongDetails

@Composable
fun SpotifyShareCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
) {
    var variant by remember { mutableStateOf(true) }

    val innerContentColor by animateColorAsState(
        targetValue = when (variant) {
            true -> cardColors.contentColor
            else -> cardColors.containerColor
        }
    )
    val innerContainerColor by animateColorAsState(
        targetValue = when (variant) {
            true -> cardColors.containerColor
            else -> cardColors.contentColor
        }
    )

    Box(contentAlignment = Alignment.Center) {
        Card(
            modifier = modifier,
            colors = cardColors,
            shape = cardCorners
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(46.dp),
                    onClick = { variant = !variant },
                    colors = CardDefaults.cardColors(
                        contentColor = innerContentColor,
                        containerColor = innerContainerColor
                    )
                ) {
                    Column (modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ArtFromUrl(
                                imageUrl = song.artUrl,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(MaterialTheme.shapes.small)
                            )

                            Column(
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                            ) {
                                Text(
                                    text = song.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = song.artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.padding(8.dp))

                        LazyColumn {
                            sortedLines.forEach {
                                item {
                                    Text(
                                        text = it.value,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }

        CardEditRow(
            modifier = Modifier.align(Alignment.BottomEnd),
            colors = true,
            corners = true,
            tint = cardColors.contentColor
        )
    }
}