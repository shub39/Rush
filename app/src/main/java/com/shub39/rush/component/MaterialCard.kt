package com.shub39.rush.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.shub39.rush.R
import com.shub39.rush.database.SettingsDataStore
import com.shub39.rush.database.Song
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun MaterialCard(
    modifier: Modifier,
    logo: String,
    song: Song,
    sortedLines: Map<Int, String>,
    imageLoader: ImageLoader = koinInject()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cardColorType by SettingsDataStore.getCardColorFlow(context)
        .collectAsState(initial = "Default")
    val cardCornersType by SettingsDataStore.getCardRoundnessFlow(context)
        .collectAsState(initial = "Rounded")
    var cardBackgroundDominant by remember { mutableStateOf(Color.DarkGray) }
    var cardContentDominant by remember { mutableStateOf(Color.White) }
    var cardBackgroundMuted by remember { mutableStateOf(Color.DarkGray) }
    var cardContentMuted by remember { mutableStateOf(Color.LightGray) }

    LaunchedEffect(song) {
        val request = ImageRequest.Builder(context)
            .data(song.artUrl)
            .allowHardware(false)
            .build()

        val result = (imageLoader.execute(request) as? SuccessResult)?.drawable
        result.let { drawable ->
            if (drawable != null) {
                Palette.from(drawable.toBitmap()).generate { palette ->
                    palette?.let {
                        cardBackgroundDominant =
                            Color(
                                it.vibrantSwatch?.rgb ?: it.lightVibrantSwatch?.rgb
                                ?: it.darkVibrantSwatch?.rgb ?: it.dominantSwatch?.rgb
                                ?: Color.DarkGray.toArgb()
                            )
                        cardContentDominant =
                            Color(
                                it.vibrantSwatch?.bodyTextColor
                                    ?: it.lightVibrantSwatch?.bodyTextColor
                                    ?: it.darkVibrantSwatch?.bodyTextColor
                                    ?: it.dominantSwatch?.bodyTextColor
                                    ?: Color.White.toArgb()
                            )
                        cardBackgroundMuted =
                            Color(
                                it.mutedSwatch?.rgb ?: it.darkMutedSwatch?.rgb
                                ?: it.lightMutedSwatch?.rgb ?: Color.DarkGray.toArgb()
                            )
                        cardContentMuted =
                            Color(
                                it.mutedSwatch?.bodyTextColor ?: it.darkMutedSwatch?.bodyTextColor
                                ?: it.lightMutedSwatch?.bodyTextColor ?: Color.White.toArgb()
                            )
                    }
                }
            }
        }
    }

    val cardCorners = when (cardCornersType) {
        "Rounded" -> RoundedCornerShape(16.dp)
        else -> RoundedCornerShape(0.dp)
    }
    val cardColor = when (cardColorType) {
        "Muted" -> CardDefaults.cardColors(
            containerColor = cardBackgroundMuted,
            contentColor = cardContentMuted
        )

        "Vibrant" -> CardDefaults.cardColors(
            containerColor = cardBackgroundDominant,
            contentColor = cardContentDominant
        )

        else -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = modifier
                .width(350.dp),
            colors = cardColor,
            shape = cardCorners
        ) {
            Column(
                modifier = Modifier.padding(32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ArtFromUrl(
                        imageUrl = song.artUrl,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(MaterialTheme.shapes.small),
                    )
                    Column(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                    ) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artists,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))

                LazyColumn {
                    sortedLines.forEach {
                        item {
                            Text(
                                text = it.value,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))

                AnimatedVisibility (logo == "Rush") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.rush_transparent),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )

                        Text(
                            text = stringResource(id = R.string.app_name),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 32.dp),
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        when (cardColorType) {
                            "Vibrant" -> SettingsDataStore.updateCardColor(context, "Muted")
                            "Muted" -> SettingsDataStore.updateCardColor(context, "Default")
                            else -> SettingsDataStore.updateCardColor(context, "Vibrant")
                        }
                    }
                }
            ) {
                Icon(
                    painter = when (cardColorType) {
                        "Vibrant" -> painterResource(id = R.drawable.round_remove_red_eye_24)
                        "Muted" -> painterResource(id = R.drawable.round_lens_blur_24)
                        else -> painterResource(id = R.drawable.round_disabled_by_default_24)
                    },
                    contentDescription = null
                )
            }

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        when (cardCornersType) {
                            "Rounded" -> SettingsDataStore.updateCardRoundness(
                                context,
                                "Flat"
                            )

                            else -> SettingsDataStore.updateCardRoundness(
                                context,
                                "Rounded"
                            )
                        }
                    }
                }
            ) {
                Icon(
                    painter = when (cardCornersType) {
                        "Rounded" -> painterResource(id = R.drawable.baseline_circle_24)
                        else -> painterResource(id = R.drawable.baseline_square_24)
                    },
                    contentDescription = null
                )
            }
        }
    }
}