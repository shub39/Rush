package com.shub39.rush.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.database.Song

@Composable
fun MaterialShareCard(
    modifier: Modifier,
    song: Song,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
) {
    Box(contentAlignment = Alignment.Center) {
        Card(
            modifier = modifier
                .width(350.dp),
            colors = cardColors,
            shape = cardCorners
        ) {
            Column(
                modifier = Modifier.padding(32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ArtFromUrl(
                        imageUrl = song.artUrl,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(cardCorners),
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
                            text = song.artists,
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
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))

            }
        }

        CardEditRow(
            modifier = Modifier.align(Alignment.BottomEnd),
            colors = true,
            corners = true,
//            large = true
        )
    }
}