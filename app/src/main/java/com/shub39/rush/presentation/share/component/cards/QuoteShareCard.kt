package com.shub39.rush.presentation.share.component.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.shub39.rush.domain.dataclasses.SongDetails
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.enums.CardFit
import com.shub39.rush.presentation.ArtFromUrl
import com.shub39.rush.presentation.RushBranding
import com.shub39.rush.presentation.RushTheme
import com.shub39.rush.presentation.share.fromPx
import com.shub39.rush.presentation.share.pxToDp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.QuoteLeft

@Composable
fun QuoteShareCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    fit: CardFit,
    albumArtShape: Shape = CircleShape,
    rushBranding: Boolean
) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.QuoteLeft,
                    contentDescription = "Quote",
                    modifier = Modifier.size(pxToDp(60))
                )

                AnimatedVisibility(
                    visible = rushBranding
                ) {
                    RushBranding(
                        color = cardColors.contentColor
                    )
                }
            }

            Spacer(modifier = Modifier.padding(pxToDp(16)))

            Text(
                text = sortedLines.values.firstOrNull() ?: "Woah...",
                style = MaterialTheme.typography.displayMedium.fromPx(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 50,
                    letterSpacing = 0,
                    lineHeight = 52,
                ),
            )

            Spacer(modifier = Modifier.padding(pxToDp(64)))

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
                    modifier = Modifier.padding(horizontal = pxToDp(32))
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
        QuoteShareCard(
            modifier = Modifier
                .width(pxToDp(720))
                .heightIn(max = pxToDp(1280)),
            song = SongDetails(
                title = "Test Song",
                artist = "Eminem",
                null, ""
            ),
            sortedLines = mapOf(
                0 to "This is a simple line"
            ).plus(
                0 to "Hello this is a very very very very very the quick browm fox jumps over the lazy dog"
            ),
            cardColors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary
            ),
            cardCorners = RoundedCornerShape(pxToDp(48)),
            fit = CardFit.FIT,
            rushBranding = true
        )
    }
}