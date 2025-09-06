package com.shub39.rush.share.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.shub39.rush.core.domain.data_classes.SongDetails
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.domain.enums.CardFit
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.share.fromPx
import com.shub39.rush.share.pxToDp
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
                fontSize = Random.nextInt(50, 150),
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
                .padding(pxToDp(48))
                .let {
                    if (fit == CardFit.STANDARD) {
                        it.weight(1f)
                    } else it
                },
            verticalArrangement = Arrangement.Center
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(pxToDp(16)),
                verticalArrangement = Arrangement.spacedBy(pxToDp(16))
            ) {
                words.forEach { word ->
                    Text(
                        modifier = Modifier.rotate(word.rotate.toFloat()),
                        text = word.text,
                        style = MaterialTheme.typography.titleMedium.fromPx(
                            fontSize = word.fontSize,
                            letterSpacing = 0,
                            lineHeight = word.fontSize,
                            fontWeight = word.fontWeight
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.padding(pxToDp(64)))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = pxToDp(32)),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium.fromPx(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28,
                            letterSpacing = 0,
                            lineHeight = 0,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodySmall.fromPx(
                            fontSize = 26,
                            letterSpacing = 0,
                            lineHeight = 0,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                ArtFromUrl(
                    imageUrl = song.artUrl,
                    modifier = Modifier
                        .size(pxToDp(100))
                        .clip(RoundedCornerShape(pxToDp(32)))
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RushTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                MessyCard(
                    modifier = Modifier
                        .width(pxToDp(720))
                        .heightIn(max = pxToDp(1280)),
                    song = SongDetails(
                        title = "Test Song",
                        artist = "Eminem",
                    ),
                    sortedLines = (0..5).associateWith { "This is a simple line $it" },
                    cardColors = CardDefaults.cardColors(),
                    cardCorners = RoundedCornerShape(pxToDp(48)),
                    fit = CardFit.FIT
                )
            }
        }
    }
}