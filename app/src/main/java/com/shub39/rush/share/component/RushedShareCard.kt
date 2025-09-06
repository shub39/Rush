package com.shub39.rush.share.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.shub39.rush.core.domain.data_classes.SongDetails
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.presentation.ArtFromUrl
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
    selectedImage: PlatformFile?
) {
    Box(modifier = modifier.clip(cardCorners)) {
        ArtFromUrl(
            imageUrl = selectedImage?.toString() ?: song.artUrl,
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to cardColors.containerColor.copy(0.3f),
                        1f to cardColors.containerColor
                    )
                )
                .matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(pxToDp(48))
                .align(Alignment.BottomStart)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(pxToDp(16))) {
                items(sortedLines.values.toList()) {
                    Card(
                        colors = cardColors,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = it,
                            color = cardColors.contentColor,
                            style = MaterialTheme.typography.bodyMedium.fromPx(
                                fontSize = 40,
                                letterSpacing = 0,
                                lineHeight = 40,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(pxToDp(12))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(pxToDp(20)))

            Row(verticalAlignment = Alignment.CenterVertically) {
                ArtFromUrl(
                    imageUrl = song.artUrl,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
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
                            fontSize = 30,
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
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                RushedShareCard(
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
                    selectedImage = null
                )
            }
        }
    }
}