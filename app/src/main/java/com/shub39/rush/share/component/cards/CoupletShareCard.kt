package com.shub39.rush.share.component.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.shub39.rush.core.domain.data_classes.SongDetails
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.domain.enums.CardFit
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.RushBranding
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.share.fromPx
import com.shub39.rush.share.pxToDp

@Composable
fun CoupletShareCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    fit: CardFit,
    albumArtShape: Shape = CircleShape
) {
    Box(
        modifier = modifier.clip(cardCorners)
    ) {
        ArtFromUrl(
            imageUrl = song.artUrl,
            modifier = Modifier
                .matchParentSize()
                .blur(pxToDp(12))
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
                    .padding(pxToDp(48))
                    .let {
                        if (fit == CardFit.STANDARD) {
                            it.weight(1f)
                        } else it
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    RushBranding(
                        color = cardColors.contentColor,
                        modifier = Modifier.padding(bottom = pxToDp(48))
                    )

                    Text(
                        text = sortedLines.values.firstOrNull() ?: "Woah...",
                        style = MaterialTheme.typography.displayMedium.fromPx(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 48,
                            letterSpacing = 0,
                            lineHeight = 48,
                        ),
                    )

                    Text(
                        text = sortedLines.values.elementAtOrNull(1) ?: "...",
                        style = MaterialTheme.typography.displaySmall.fromPx(
                            letterSpacing = 0,
                            lineHeight = 38,
                            fontSize = 36
                        ),
                    )

                    Spacer(
                        modifier = Modifier.padding(pxToDp(64))
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
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
        CoupletShareCard(
            modifier = Modifier
                .width(pxToDp(720))
                .heightIn(max = pxToDp(1280)),
            song = SongDetails(
                title = "Test Song",
                artist = "Eminem",
            ),
            sortedLines = mapOf(
                0 to "This is a simple line"
            ).plus(
                2 to "Hello this is a very very very very very the quick browm fox jumps over the lazy dog"
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