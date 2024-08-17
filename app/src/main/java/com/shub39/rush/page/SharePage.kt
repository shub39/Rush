package com.shub39.rush.page

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.shub39.rush.R
import com.shub39.rush.component.ArtFromUrl
import com.shub39.rush.database.SettingsDataStore
import com.shub39.rush.viewmodel.RushViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun SharePage(
    onDismiss: () -> Unit,
    rushViewModel: RushViewModel,
    imageLoader: ImageLoader = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    val cardGraphicsLayer = rememberGraphicsLayer()
    val context = LocalContext.current
    val song = rushViewModel.currentSong.collectAsState().value!!
    val selectedLines = rushViewModel.shareLines.collectAsState().value
    val cardColorType by SettingsDataStore.getCardColorFlow(context)
        .collectAsState(initial = "Default")
    val cardCornersType by SettingsDataStore.getCardRoundnessFlow(context)
        .collectAsState(initial = "Rounded")
    val logo by SettingsDataStore.getLogoFlow(context)
        .collectAsState(initial = "None")
    val sortedLines = sortMapByKeys(selectedLines)

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

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismiss() },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(350.dp)
                        .drawWithContent {
                            cardGraphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(cardGraphicsLayer)
                        },
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
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = song.artists,
                                    style = MaterialTheme.typography.bodyMedium,
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
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.padding(8.dp))

                        AnimatedVisibility (logo == "Spotify") {
                            Icon(
                                painter = painterResource(id = R.drawable.spotify_logo_with_text),
                                contentDescription = null,
                                modifier = Modifier.width(100.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 64.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                when (cardColorType) {
                                    "Vibrant" -> SettingsDataStore.updateCardColor(context, "Muted")
                                    "Muted" -> SettingsDataStore.updateCardColor(context, "Default")
                                    else -> SettingsDataStore.updateCardColor(context, "Vibrant")
                                }
                            }
                        },
                        shape = MaterialTheme.shapes.extraLarge
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

                    Spacer(modifier = Modifier.padding(4.dp))

                    FloatingActionButton(
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
                        },
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Icon(
                            painter = when (cardCornersType) {
                                "Rounded" -> painterResource(id = R.drawable.baseline_circle_24)
                                else -> painterResource(id = R.drawable.baseline_square_24)
                            },
                            contentDescription = null
                        )
                    }

                    Spacer(modifier = Modifier.padding(4.dp))

                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                when (logo) {
                                    "Spotify" -> SettingsDataStore.updateLogo(context, "None")
                                    else -> SettingsDataStore.updateLogo(context, "Spotify")
                                }
                            }
                        },
                        containerColor = if (logo == "Spotify") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.spotify_logo_svgrepo_com),
                            contentDescription = null
                        )
                    }

                    Spacer(modifier = Modifier.padding(4.dp))

                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                val bitmap = cardGraphicsLayer.toImageBitmap().asAndroidBitmap()
                                shareImage(context, bitmap)
                                onDismiss()
                            }
                        },
                        shape = MaterialTheme.shapes.extraLarge,
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_share_24),
                            contentDescription = null
                        )
                    }
                }
            }

        }
    )
}

private fun sortMapByKeys(map: Map<Int, String>): Map<Int, String> {
    val sortedEntries = map.entries.toList().sortedBy { it.key }
    val sortedMap = LinkedHashMap<Int, String>()
    for (entry in sortedEntries) {
        sortedMap[entry.key] = entry.value
    }
    return sortedMap
}

fun shareImage(context: Context, bitmap: Bitmap) {
    val cachePath = File(context.cacheDir, "images")
    cachePath.mkdirs()
    val file = File(cachePath, "shared_image.png")
    try {
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
    } catch (e: IOException) {
        e.printStackTrace()
        return
    }

    val contentUri: Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, contentUri)
        type = "image/png"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share image using"))
}