package com.shub39.rush.share.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.shub39.rush.core.domain.data_classes.SongDetails
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.domain.enums.CardFit
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.core.presentation.rotateVertically
import com.shub39.rush.share.fromPx
import com.shub39.rush.share.pxToDp

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
                    .padding(pxToDp(48))
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
                            .size(pxToDp(100))
                            .clip(RoundedCornerShape(pxToDp(16)))
                    )

                    Spacer(modifier = Modifier.padding(pxToDp(16)))

                    Row {
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodySmall.fromPx(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28,
                                letterSpacing = 0,
                                lineHeight = 0,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.rotateVertically()
                        )

                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleMedium.fromPx(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 26,
                                letterSpacing = 0,
                                lineHeight = 0,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.rotateVertically()
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(pxToDp(16)))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(pxToDp(10))
                ) {
                    sortedLines.forEach {
                        item {
                            Text(
                                text = it.value,
                                fontStyle = FontStyle.Italic,
                                style = MaterialTheme.typography.bodyMedium.fromPx(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32,
                                    letterSpacing = 0,
                                    lineHeight = 32,
                                ),
                            )
                        }
                    }
                }
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
                VerticalShareCard(
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