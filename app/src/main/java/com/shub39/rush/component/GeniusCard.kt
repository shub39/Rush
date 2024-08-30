package com.shub39.rush.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.shub39.rush.database.Song
import org.koin.compose.koinInject

@Composable
fun GeniusCard(
    modifier: Modifier,
    song: Song,
    sortedLines: Map<Int, String>,
    imageLoader: ImageLoader = koinInject()
) {
    val context = LocalContext.current
    var cardBackgroundDominant by remember { mutableStateOf(Color.DarkGray) }
    var cardContentDominant by remember { mutableStateOf(Color.White) }

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
                    }
                }
            }
        }
    }

    val cardColor = CardDefaults.cardColors(
        containerColor = cardBackgroundDominant,
        contentColor = cardContentDominant
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier.size(350.dp),
        ) {
            ArtFromUrl(
                imageUrl = song.artUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(5.dp)
            )

            Row(
                modifier = Modifier.padding(start= 16.dp, bottom = 16.dp).align(Alignment.BottomStart),
            ) {
                ArtFromUrl(
                    imageUrl = song.artUrl,
                    modifier = Modifier.size(70.dp)
                )

                Spacer(modifier = Modifier.padding(5.dp))

                Column {
                    Text(
                        text = song.title,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = song.artists,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            LazyColumn(modifier = Modifier.padding(start = 32.dp, end = 32.dp, bottom = 100.dp, top = 16.dp)) {
                item { Spacer(modifier = Modifier.padding(20.dp)) }

                items(sortedLines.values.toList()) {
                    Card(
                        modifier = Modifier.padding(bottom = 10.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = cardColor
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(
                                start = 6.dp,
                                end = 6.dp,
                                top = 4.dp,
                                bottom = 4.dp
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item { Spacer(modifier = Modifier.padding(20.dp)) }
            }
        }
    }
}