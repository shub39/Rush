package com.shub39.rush.lyrics.presentation.share.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.MeshGradient
import com.shub39.rush.core.data.SongDetails
import com.shub39.rush.core.domain.CardFit
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.generateGradientColors

@Composable
fun HypnoticShareCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    fit: CardFit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.clip(cardCorners),
    ) {
        if (fit == CardFit.FIT) {
            Card(
                modifier = Modifier
                    .shaderBackground(
                        MeshGradient(
                            colors = generateGradientColors(
                                cardColors.containerColor
                            ).toTypedArray()
                        ),
                        fallback = {
                            Brush.horizontalGradient(
                                generateGradientColors(
                                    cardColors.containerColor
                                )
                            )
                        }
                    ),
                colors = CardDefaults.cardColors(
                    contentColor = cardColors.contentColor,
                    containerColor = Color.Transparent
                ),
                shape = cardCorners
            ) {
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        colors = CardDefaults.cardColors(
                            contentColor = cardColors.contentColor,
                            containerColor = Color.Transparent
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
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
                                    modifier = Modifier
                                        .padding(start = 16.dp, end = 16.dp)
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = song.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = song.artist,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
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
                                            fontSize = 15.sp,
                                            modifier = Modifier.padding(bottom = 10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .shaderBackground(
                        MeshGradient(
                            colors = generateGradientColors(
                                cardColors.containerColor
                            ).toTypedArray()
                        ),
                        fallback = {
                            Brush.horizontalGradient(
                                generateGradientColors(
                                    cardColors.containerColor
                                )
                            )
                        }
                    ),
                shape = cardCorners,
                colors = CardDefaults.cardColors(
                    contentColor = cardColors.contentColor,
                    containerColor = Color.Transparent
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ArtFromUrl(
                            imageUrl = song.artUrl,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(MaterialTheme.shapes.small)
                        )

                        Column(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                        ) {
                            Text(
                                text = song.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = with(LocalDensity.current) { 40.toSp() },
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = song.artist,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = with(LocalDensity.current) { 35.toSp() },
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
                                    fontSize = with(LocalDensity.current) { 40.toSp() },
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )
                            }
                        }
                    }

                }

            }
        }
    }
}