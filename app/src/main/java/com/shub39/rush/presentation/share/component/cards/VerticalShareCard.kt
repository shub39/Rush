package com.shub39.rush.presentation.share.component.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.shub39.rush.domain.dataclasses.SongDetails
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.enums.CardFit
import com.shub39.rush.presentation.components.ArtFromUrl
import com.shub39.rush.presentation.components.RushBranding
import com.shub39.rush.presentation.components.RushTheme
import com.shub39.rush.presentation.rotateVertically
import com.shub39.rush.presentation.share.fromPx
import com.shub39.rush.presentation.share.pxToDp

@Composable
fun VerticalShareCard(
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
                            .clip(albumArtShape)
                    )

                    Spacer(modifier = Modifier.padding(pxToDp(8)))

                    Row {
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodySmall.fromPx(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24,
                                letterSpacing = 0,
                                lineHeight = 24,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.rotateVertically()
                        )

                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleMedium.fromPx(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28,
                                letterSpacing = 0,
                                lineHeight = 28,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.rotateVertically()
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(pxToDp(8)))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(pxToDp(10))
                ) {
                    items(sortedLines.entries.toList()) {
                        Text(
                            text = it.value,
                            fontStyle = FontStyle.Italic,
                            style = MaterialTheme.typography.bodyMedium.fromPx(
                                fontWeight = FontWeight.Bold,
                                fontSize = 42,
                                letterSpacing = 0,
                                lineHeight = 44,
                            ),
                        )
                    }

                    item {
                        AnimatedVisibility(
                            visible = rushBranding
                        ) {
                            RushBranding(
                                color = cardColors.contentColor,
                                modifier = Modifier.padding(top = pxToDp(42))
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
        VerticalShareCard(
            modifier = Modifier
                .width(pxToDp(720))
                .heightIn(max = pxToDp(1280)),
            song = SongDetails(
                title = "Test Song",
                artist = "Eminem",
                null, ""
            ),
            sortedLines = (0..5).associateWith { "This is a simple line $it" }.plus(
                6 to "Hello this is a very very very very very the quick browm fox jumps over the lazy dog"
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