package com.shub39.rush.lyrics.presentation.share.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.core.data.SongDetails
import com.shub39.rush.core.domain.CardFit
import com.shub39.rush.core.presentation.ArtFromUrl
import kotlin.random.Random
import kotlin.random.nextInt

private data class Word(
    val text: String,
    val fontWeight: FontWeight,
    val fontSize: Int,
    val rotate: Int
)

@Composable
fun MessyCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    fit: CardFit
) {
    val firstLine = sortedLines.values.firstOrNull() ?: "Woah..."
    val words = remember {
        firstLine.split(" ").map {
            Word(
                text = if (Random.nextBoolean()) it.uppercase() else it.lowercase(),
                fontWeight = if (Random.nextBoolean()) FontWeight.Bold else FontWeight.ExtraBold,
                fontSize = Random.nextInt(30, 60),
                rotate = Random.nextInt(-10..10)
            )
        }
    }

    Card(
        modifier = modifier,
        colors = cardColors,
        shape = cardCorners
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .let {
                    if (fit == CardFit.STANDARD) {
                        it.weight(1f)
                    } else it
                },
            verticalArrangement = Arrangement.Center
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                words.forEach { word ->
                    Text(
                        modifier = Modifier.rotate(word.rotate.toFloat()),
                        text = word.text,
                        fontWeight = word.fontWeight,
                        fontSize = with(LocalDensity.current) { word.fontSize.sp }
                    )
                }
            }

            Spacer(modifier = Modifier.padding(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = with(LocalDensity.current) { 40.toSp() },
                        maxLines = 1,
                        textAlign = TextAlign.End,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = with(LocalDensity.current) { 35.toSp() },
                        maxLines = 1,
                        textAlign = TextAlign.End,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                ArtFromUrl(
                    imageUrl = song.artUrl,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(MaterialTheme.shapes.small)
                )
            }
        }
    }
}