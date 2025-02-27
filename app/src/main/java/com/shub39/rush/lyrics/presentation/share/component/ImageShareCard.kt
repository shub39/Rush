package com.shub39.rush.lyrics.presentation.share.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.core.data.SongDetails
import com.shub39.rush.core.presentation.ArtFromUrl

@Composable
fun ImageShareCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    selectedUri: Uri?
) {
    var blur by remember { mutableStateOf(false) }

    Box(modifier = Modifier.clickable { blur = !blur }) {
        Box(modifier = modifier.clip(cardCorners)) {
            ArtFromUrl(
                imageUrl = selectedUri ?: song.artUrl,
                modifier = Modifier
                    .matchParentSize()
                    .blur(if (blur) 5.dp else 0.dp)
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
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp)
                    .align(Alignment.BottomStart)
            ) {
                LazyColumn(
                    modifier = Modifier.wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(sortedLines.values.toList()) {
                        var variant by remember { mutableStateOf(false) }

                        Card(
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
                                fontSize = with(LocalDensity.current) { 35.toSp() },
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
                            .size(50.dp)
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = song.title,
                            fontWeight = FontWeight.ExtraBold,
                            color = cardColors.contentColor,
                            fontSize = with(LocalDensity.current) { 40.toSp() },
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodySmall,
                            color = cardColors.contentColor,
                            fontSize = with(LocalDensity.current) { 30.toSp() },
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}