package com.shub39.rush.lyrics.presentation.share.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shub39.rush.core.domain.data_classes.SongDetails
import com.shub39.rush.core.domain.enums.CardFit
import com.shub39.rush.core.presentation.ArtFromUrl

@Composable
fun CoupletShareCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    fit: CardFit
) {
    Box(
        modifier = modifier.clip(cardCorners)
    ) {
        ArtFromUrl(
            imageUrl = song.artUrl,
            modifier = Modifier
                .matchParentSize()
                .blur(12.dp)
                .clip(MaterialTheme.shapes.small)
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(color = cardColors.containerColor.copy(alpha = 0.7f))
        )

        Card(
            colors = cardColors.copy(containerColor = Color.Transparent)
        ) {
            Row(
                modifier = Modifier
                    .padding(32.dp)
                    .let {
                        if (fit == CardFit.STANDARD) {
                            it.weight(1f)
                        } else it
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = sortedLines.values.firstOrNull() ?: "Woah...",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = with(LocalDensity.current) { 80.toSp() },
                        fontSize = with(LocalDensity.current) { 60.toSp() }
                    )

                    Text(
                        text = sortedLines.values.elementAtOrNull(1) ?: "...",
                        style = MaterialTheme.typography.displaySmall,
                        lineHeight = with(LocalDensity.current) { 60.toSp() },
                        fontSize = with(LocalDensity.current) { 40.toSp() }
                    )

                    Spacer(
                        modifier = Modifier.padding(32.dp)
                    )

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
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = song.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = with(LocalDensity.current) { 40.toSp() },
                                maxLines = 1,
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
                }
            }
        }
    }
}