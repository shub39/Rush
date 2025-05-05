package com.shub39.rush.lyrics.presentation.share.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
    Box(
        modifier = modifier
            .background(
                color = cardColors.containerColor,
                shape = cardCorners
            )
            .clip(cardCorners)
    ) {
        ArtFromUrl(
            imageUrl = song.artUrl,
            modifier = Modifier
                .matchParentSize()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.7f to cardColors.containerColor,
                        1f to cardColors.containerColor
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .align(Alignment.BottomCenter)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(sortedLines.values.toList()) {
                    Card(
                        colors = cardColors,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = it,
                            fontSize = with(LocalDensity.current) { 40.toSp() },
                            fontWeight = FontWeight.Bold,
                            color = cardColors.contentColor,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(12.dp))

            Row {
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
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = cardColors.contentColor,
                        fontSize = with(LocalDensity.current) { 40.toSp() },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}