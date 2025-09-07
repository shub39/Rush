package com.shub39.rush.onboarding.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.shub39.rush.core.domain.data_classes.SongDetails
import com.shub39.rush.core.domain.enums.CardFit
import com.shub39.rush.core.domain.enums.CardTheme
import com.shub39.rush.share.component.cards.AlbumArt
import com.shub39.rush.share.component.cards.ChatCard
import com.shub39.rush.share.component.cards.CoupletShareCard
import com.shub39.rush.share.component.cards.HypnoticShareCard
import com.shub39.rush.share.component.cards.MessyCard
import com.shub39.rush.share.component.cards.QuoteShareCard
import com.shub39.rush.share.component.cards.RushedShareCard
import com.shub39.rush.share.component.cards.SpotifyShareCard
import com.shub39.rush.share.component.cards.VerticalShareCard
import com.shub39.rush.share.pxToDp

@Composable
fun AnimatedShareCardsDemo(
    cardStyle: CardTheme,
    contentColor: Color,
    containerColor: Color
) {
    val animatedContentColor by animateColorAsState(
        targetValue = contentColor
    )
    val animatedContainerColor by animateColorAsState(
        targetValue = containerColor
    )

    val cardColors = CardDefaults.cardColors(
        containerColor = animatedContainerColor,
        contentColor = animatedContentColor
    )

    val song = SongDetails(
        title = "Rush",
        artist = "shub39",
        album = "",
        artUrl = "https://raw.githubusercontent.com/shub39/Rush/refs/heads/master/fastlane/metadata/android/en-US/images/icon.png"
    )
    val sortedLines = mapOf(
        0 to "This is how cards look",
        1 to "Endless combinations!!",
        2  to "Share cards with Rush",
        3 to "Exactly the way you want"
    )
    val cardCorners = RoundedCornerShape(pxToDp(16))
    val fit = CardFit.FIT
    val modifier = Modifier
        .width(pxToDp(720))
        .padding(pxToDp(32))

    AnimatedContent(
        targetState = cardStyle
    ) {
        when (it) {
            CardTheme.SPOTIFY -> SpotifyShareCard(
                song = song,
                modifier = modifier,
                sortedLines = sortedLines,
                cardColors = cardColors,
                cardCorners = cardCorners,
                fit = fit,
            )

            CardTheme.RUSHED -> RushedShareCard(
                song = song,
                modifier = modifier,
                sortedLines = sortedLines,
                cardColors = cardColors,
                cardCorners = cardCorners,
                selectedImage = null,
            )

            CardTheme.HYPNOTIC -> HypnoticShareCard(
                song = song,
                modifier = modifier,
                sortedLines = sortedLines,
                cardColors = cardColors,
                cardCorners = cardCorners,
                fit = fit
            )

            CardTheme.VERTICAL -> VerticalShareCard(
                song = song,
                modifier = modifier,
                sortedLines = sortedLines,
                cardColors = cardColors,
                cardCorners = cardCorners,
                fit = fit
            )

            CardTheme.QUOTE -> QuoteShareCard(
                song = song,
                modifier = modifier,
                sortedLines = sortedLines,
                cardColors = cardColors,
                cardCorners = cardCorners,
                fit = fit
            )

            CardTheme.COUPLET -> CoupletShareCard(
                song = song,
                modifier = modifier,
                sortedLines = sortedLines,
                cardColors = cardColors,
                cardCorners = cardCorners,
                fit = fit
            )

            CardTheme.MESSY -> MessyCard(
                song = song,
                modifier = modifier,
                sortedLines = sortedLines,
                cardColors = cardColors,
                cardCorners = cardCorners,
                fit = fit
            )

            CardTheme.CHAT -> ChatCard(
                song = song,
                modifier = modifier,
                sortedLines = sortedLines,
                cardColors = cardColors,
                cardCorners = cardCorners,
                fit = fit
            )

            CardTheme.ALBUM_ART -> AlbumArt(
                song = song,
                modifier = modifier,
                cardColors = cardColors,
                cardCorners = cardCorners,
                fit = fit
            )
        }
    }
}