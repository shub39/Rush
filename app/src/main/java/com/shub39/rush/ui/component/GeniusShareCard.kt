package com.shub39.rush.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.rush.database.Song

@Composable
fun GeniusShareCard(
    modifier: Modifier,
    song: Song,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
) {
    Box {
        Box(
            modifier = modifier
                .width(350.dp)
                .wrapContentHeight()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                ),
        ) {
            ArtFromUrl(
                imageUrl = song.artUrl,
                modifier = Modifier
                    .matchParentSize()
                    .blur(5.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                cardColors.containerColor
                            )
                        )
                    )
                    .matchParentSize()
                    .align(Alignment.BottomCenter)
            )

            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.BottomStart),
            ) {
                ArtFromUrl(
                    imageUrl = song.artUrl,
                    modifier = Modifier.size(70.dp)
                )

                Spacer(modifier = Modifier.padding(5.dp))

                Column {
                    Text(
                        text = song.title,
                        fontWeight = FontWeight.ExtraBold,
                        color = cardColors.contentColor,
                    )
                    Text(
                        text = song.artists,
                        style = MaterialTheme.typography.bodySmall,
                        color = cardColors.contentColor
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .padding(
                        start = 32.dp,
                        end = 32.dp,
                        bottom = 120.dp,
                        top = 32.dp
                    )
                    .wrapContentHeight()
            ) {
                items(sortedLines.values.toList()) {
                    Card(
                        modifier = Modifier.padding(bottom = 10.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = cardColors
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(
                                start = 6.dp,
                                end = 6.dp,
                                top = 4.dp,
                                bottom = 4.dp
                            ),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        CardEditRow(
            modifier = Modifier.align(Alignment.BottomEnd),
            colors = true,
//            large = true
        )
    }
}