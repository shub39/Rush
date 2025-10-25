package com.shub39.rush.share.component.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.shub39.rush.core.domain.data_classes.SongDetails
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.RushBranding
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.share.fromPx
import com.shub39.rush.share.pxToDp
import io.github.vinceglb.filekit.PlatformFile

@Composable
fun RushedShareCard(
    modifier: Modifier,
    song: SongDetails,
    sortedLines: Map<Int, String>,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    selectedImage: PlatformFile?,
    albumArtShape: Shape = CircleShape,
    rushBranding: Boolean
) {
    Box(modifier = modifier.clip(cardCorners)) {
        ArtFromUrl(
            imageUrl = selectedImage?.toString() ?: song.artUrl,
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColors.containerColor.copy(0.8f))
                .matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(pxToDp(48))
                .align(Alignment.BottomStart)
        ) {
            AnimatedVisibility(
                visible = rushBranding
            ) {
                RushBranding(
                    color = cardColors.contentColor,
                    modifier = Modifier.padding(bottom = pxToDp(42))
                )
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(pxToDp(16))) {
                items(sortedLines.values.toList()) {
                    Card(
                        colors = cardColors,
                        shape = RoundedCornerShape(pxToDp(16))
                    ) {
                        Text(
                            text = it,
                            color = cardColors.contentColor,
                            style = MaterialTheme.typography.bodyMedium.fromPx(
                                fontSize = 42,
                                letterSpacing = 0,
                                lineHeight = 48,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(
                                vertical = pxToDp(8),
                                horizontal = pxToDp(16)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(pxToDp(20)))

            Row(verticalAlignment = Alignment.CenterVertically) {
                ArtFromUrl(
                    imageUrl = song.artUrl,
                    modifier = Modifier
                        .clip(albumArtShape)
                        .size(pxToDp(100))
                )

                Spacer(modifier = Modifier.padding(pxToDp(8)))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = song.title,
                        color = cardColors.contentColor,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium.fromPx(
                            fontSize = 32,
                            letterSpacing = 0,
                            lineHeight = 0,
                            fontWeight = FontWeight.Bold
                        ),
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodySmall.fromPx(
                            fontSize = 24,
                            letterSpacing = 0,
                            lineHeight = 0
                        ),
                        color = cardColors.contentColor,
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
        RushedShareCard(
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
            selectedImage = null,
            rushBranding = true
        )
    }
}