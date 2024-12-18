package com.shub39.rush.share.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.core.data.SongDetails
import com.shub39.rush.core.presentation.ArtFromUrl

@Composable
fun RushedShareCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape
) {
    Box {
        Box(
            modifier = modifier
                .clip(cardCorners),
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
                                cardColors.containerColor.copy(0.3f),
                                cardColors.containerColor
                            )
                        )
                    )
                    .matchParentSize()
                    .align(Alignment.BottomCenter)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                cardColors.containerColor,
                                cardColors.containerColor.copy(0.3f),
                                Color.Transparent
                            )
                        )
                    )
                    .matchParentSize()
                    .align(Alignment.TopCenter)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp)
                    .align(Alignment.Center)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .wrapContentHeight()
                ) {
                    items(sortedLines.values.toList()) {
                        var variant by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier.padding(bottom = 8.dp),
                            shape = MaterialTheme.shapes.small,
                            colors = when (variant) {
                                true -> CardDefaults.cardColors(
                                    containerColor = cardColors.contentColor,
                                    contentColor = cardColors.containerColor
                                )
                                else -> cardColors
                            },
                            onClick = { variant = !variant }
                        ) {
                            Text(
                                text = it,
                                modifier = Modifier.padding(
                                    start = 6.dp,
                                    end = 6.dp,
                                    top = 4.dp,
                                    bottom = 4.dp
                                ),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                ) {
                    ArtFromUrl(
                        imageUrl = song.artUrl,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .size(70.dp)
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = song.title,
                            fontWeight = FontWeight.ExtraBold,
                            color = cardColors.contentColor,
                            fontSize = 17.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodySmall,
                            color = cardColors.contentColor,
                            fontSize = 15.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

    }
}