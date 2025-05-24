package com.shub39.rush.lyrics.presentation.share.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.core.domain.CardFit
import com.shub39.rush.core.domain.SongDetails
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.rotateVertically

@Composable
fun VerticalShareCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    fit: CardFit
) {
    Card(
        modifier = modifier,
        shape = cardCorners
    ) {
        Card(
            colors = cardColors
        ) {
            Row(
                modifier = Modifier
                    .padding(32.dp)
                    .let {
                        if (fit == CardFit.STANDARD) {
                            it.fillMaxHeight()
                        } else it
                    }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ArtFromUrl(
                        imageUrl = song.artUrl,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(MaterialTheme.shapes.small)
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Row {
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = with(LocalDensity.current) { 35.toSp() },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.rotateVertically()
                        )

                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = with(LocalDensity.current) { 40.toSp() },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.rotateVertically()
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    sortedLines.forEach {
                        item {
                            Text(
                                text = it.value,
                                fontStyle = FontStyle.Italic,
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