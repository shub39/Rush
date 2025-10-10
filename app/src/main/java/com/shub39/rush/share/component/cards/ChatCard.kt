package com.shub39.rush.share.component.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Shape
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
import com.shub39.rush.share.getFormattedTime
import com.shub39.rush.share.pxToDp
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ChatCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    fit: CardFit,
    albumArtShape: Shape = CircleShape
) {
    Card(
        modifier = modifier,
        shape = cardCorners,
        colors = CardDefaults.cardColors(
            containerColor = cardColors.containerColor,
            contentColor = cardColors.contentColor
        )
    ) {
        Column(
            modifier = Modifier
                .padding(pxToDp(48))
                .let {
                    if (fit == CardFit.STANDARD) {
                        it.fillMaxHeight()
                    } else it
                },
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(pxToDp(8))
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = pxToDp(24))
                    ) {
                        ArtFromUrl(
                            imageUrl = song.artUrl,
                            modifier = Modifier
                                .size(pxToDp(100))
                                .clip(albumArtShape)
                        )

                        Column(
                            modifier = Modifier.padding(horizontal = pxToDp(16))
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
                                    fontSize = 24,
                                    letterSpacing = 0,
                                    lineHeight = 0,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                sortedLines.forEach {
                    item {
                        Card(
                            shape = RoundedCornerShape(pxToDp(16)),
                            colors = cardColors.copy(
                                containerColor = cardColors.contentColor,
                                contentColor = cardColors.containerColor
                            )
                        ) {
                            Text(
                                text = it.value,
                                style = MaterialTheme.typography.bodyMedium.fromPx(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32,
                                    letterSpacing = 0,
                                    lineHeight = 36,
                                ),
                                modifier = Modifier.padding(
                                    horizontal = pxToDp(16),
                                    vertical = pxToDp(8)
                                ),
                            )
                        }
                    }
                }
            }

            Text(
                text = getFormattedTime(),
                style = MaterialTheme.typography.labelSmall.fromPx(
                    fontSize = 24,
                    letterSpacing = 0,
                    lineHeight = 0,
                ),
                modifier = Modifier.padding(top = pxToDp(16))
            )
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
        ChatCard(
            modifier = Modifier
                .width(pxToDp(720))
                .heightIn(max = pxToDp(1280)),
            song = SongDetails(
                title = "Test Song",
                artist = "Eminem",
            ),
            sortedLines = (0..5).associateWith { "This is a simple line $it" }.plus(
                6 to "Hello this is a very very very very very the quick browm fox jumps over the lazy dog"
            ),
            cardColors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary
            ),
            cardCorners = RoundedCornerShape(pxToDp(48)),
            fit = CardFit.FIT
        )
    }
}