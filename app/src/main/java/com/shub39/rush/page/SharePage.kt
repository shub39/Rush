package com.shub39.rush.page

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.ImageLoader
import com.shub39.rush.R
import com.shub39.rush.component.ArtFromUrl
import com.shub39.rush.database.Song
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun SharePage(
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    song: Song,
    selectedLines: Map<Int, String>,
    imageLoader: ImageLoader
) {
    val coroutineScope = rememberCoroutineScope()
    val cardGraphicsLayer = rememberGraphicsLayer()
    val context = LocalContext.current
    val sortedLines = sortMapByKeys(selectedLines)

    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        onDismissRequest = { onDismiss() },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(400.dp)
                        .drawWithContent {
                            cardGraphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(cardGraphicsLayer)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    ) {
                        ArtFromUrl(
                            imageUrl = song.artUrl,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(MaterialTheme.shapes.small),
                            imageLoader = imageLoader
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
                                text = stringResource(id = R.string.from_genius),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
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
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val bitmap = cardGraphicsLayer.toImageBitmap().asAndroidBitmap()
                            shareImage(context, bitmap)
                            onShare()
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.share))
                }
            }
        }
    )
}


fun sortMapByKeys(map: Map<Int, String>): Map<Int, String> {
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