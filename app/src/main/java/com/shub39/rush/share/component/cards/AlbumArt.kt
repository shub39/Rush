package com.shub39.rush.share.component.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes.Companion.VerySunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import io.github.vinceglb.filekit.PlatformFile

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AlbumArt(
    song: SongDetails,
    cardColors: CardColors,
    cardCorners: RoundedCornerShape,
    fit: CardFit,
    modifier: Modifier = Modifier,
    albumArtShape: Shape = CircleShape,
    selectedImage: PlatformFile? = null,
    rushBranding: Boolean
) {
    Card(
        modifier = modifier,
        shape = cardCorners,
        colors = cardColors
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(pxToDp(30))
                .let {
                    if (fit == CardFit.STANDARD) {
                        it.fillMaxHeight()
                    } else it
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ArtFromUrl(
                imageUrl = selectedImage?.toString() ?: song.artUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(albumArtShape)
            )

            Spacer(modifier = Modifier.height(pxToDp(28)))

            Text(
                text = song.title,
                style = MaterialTheme.typography.headlineLarge.fromPx(
                    fontWeight = FontWeight.Bold,
                    fontSize = 60,
                    letterSpacing = 0,
                    lineHeight = 60
                ).copy(textAlign = TextAlign.Center),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.headlineLarge.fromPx(
                    fontSize = 30,
                    letterSpacing = 0,
                    lineHeight = 0
                ).copy(textAlign = TextAlign.Center),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            AnimatedVisibility(
                visible = rushBranding
            ) {
                RushBranding(
                    color = cardColors.contentColor,
                    modifier = Modifier.padding(top = pxToDp(48))
                )
            }

            Spacer(modifier = Modifier.height(pxToDp(28)))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun Preview() {
    RushTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        AlbumArt(
            modifier = Modifier
                .width(pxToDp(720))
                .heightIn(max = pxToDp(1280)),
            song = SongDetails(
                title = "Test Song",
                artist = "Eminem",
            ),
            cardColors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary
            ),
            cardCorners = RoundedCornerShape(pxToDp(32)),
            fit = CardFit.FIT,
            albumArtShape = VerySunny.toShape(),
            rushBranding = true
        )
    }
}