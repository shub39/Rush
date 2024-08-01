package com.shub39.rush.page

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
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
import com.shub39.rush.database.Song
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePage(
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    song: Song,
    selectedLines: Map<Int, String>,
    imageLoader: ImageLoader = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    val cardGraphicsLayer = rememberGraphicsLayer()
    var cardWidthType by remember { mutableStateOf("Small") }
    var cardCornersType by remember { mutableStateOf("Rounded") }
    var cardColorType by remember { mutableStateOf("Vibrant") }
    var isEditSheetVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
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
    val cardWidth = when (cardWidthType) {
        "Small" -> 300.dp
        "Medium" -> 350.dp
        else -> 400.dp
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

    if (isEditSheetVisible) {
        Dialog(
            onDismissRequest = { isEditSheetVisible = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Card(
                    modifier = Modifier.width(380.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(
                            text = stringResource(id = R.string.width),
                            style = MaterialTheme.typography.titleMedium
                        )
                        SingleChoiceSegmentedButtonRow {
                            listOf("Small", "Medium", "Large").forEachIndexed { index, width ->
                                SegmentedButton(
                                    label = { Text(text = width) },
                                    selected = cardWidthType == width,
                                    onClick = {
                                        cardWidthType = width
                                    },
                                    shape = when (index) {
                                        0 -> RoundedCornerShape(
                                            topStart = 16.dp,
                                            bottomStart = 16.dp
                                        )

                                        2 -> RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                                        else -> RoundedCornerShape(0.dp)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(
                            text = stringResource(id = R.string.colors),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        SingleChoiceSegmentedButtonRow {
                            listOf("Default", "Muted", "Vibrant").forEachIndexed { index, color ->
                                SegmentedButton(
                                    label = { Text(text = color) },
                                    selected = cardColorType == color,
                                    onClick = {
                                        cardColorType = color
                                    },
                                    shape = when (index) {
                                        0 -> RoundedCornerShape(
                                            topStart = 16.dp,
                                            bottomStart = 16.dp
                                        )

                                        1 -> RoundedCornerShape(0.dp)
                                        else -> RoundedCornerShape(
                                            topEnd = 16.dp,
                                            bottomEnd = 16.dp
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.padding(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(text = stringResource(id = R.string.rounded_corners))
                            Spacer(modifier = Modifier.padding(8.dp))
                            Switch(
                                checked = cardCornersType == "Rounded",
                                onCheckedChange = {
                                    cardCornersType = if (cardCornersType == "Rounded") {
                                        "Rectangle"
                                    } else {
                                        "Rounded"
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.padding(4.dp))
                        Button(onClick = { isEditSheetVisible = false }) {
                            Text(text = stringResource(id = R.string.save))
                        }

                        Spacer(modifier = Modifier.padding(8.dp))
                    }
                }
                Spacer(modifier = Modifier.padding(30.dp))
            }
        }
    }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismiss() },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(cardWidth)
                        .drawWithContent {
                            cardGraphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(cardGraphicsLayer)
                        },
                    colors = cardColor,
                    shape = cardCorners
                ) {
                    Row(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    ) {
                        ArtFromUrl(
                            imageUrl = song.artUrl,
                            modifier = Modifier
                                .size(100.dp)
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
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = song.album ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        sortedLines.forEach {
                            item {
                                Text(
                                    text = it.value,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Row {
                    FloatingActionButton(
                        onClick = { isEditSheetVisible = true },
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_edit_square_24),
                            contentDescription = null
                        )
                    }

                    Spacer(modifier = Modifier.padding(4.dp))
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                val bitmap = cardGraphicsLayer.toImageBitmap().asAndroidBitmap()
                                shareImage(context, bitmap)
                                onShare()
                            }
                        },
                        shape = MaterialTheme.shapes.extraLarge
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

private fun shareImage(context: Context, bitmap: Bitmap) {
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