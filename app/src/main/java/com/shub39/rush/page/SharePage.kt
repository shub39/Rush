package com.shub39.rush.page

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.shub39.rush.R
import com.shub39.rush.component.GeniusShareCard
import com.shub39.rush.component.MaterialShareCard
import com.shub39.rush.database.SettingsDataStore
import com.shub39.rush.viewmodel.RushViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePage(
    onDismiss: () -> Unit,
    rushViewModel: RushViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val cardGraphicsLayer = rememberGraphicsLayer()
    val context = LocalContext.current
    val song = rushViewModel.currentSong.collectAsState().value!!
    val selectedLines = rushViewModel.shareLines.collectAsState().value
    val cardThemeFlow = remember { SettingsDataStore.getCardThemeFlow(context) }
    val cardTheme by cardThemeFlow.collectAsState(initial = "Default")
    val sortedLines = sortMapByKeys(selectedLines)
    var namePicker by remember { mutableStateOf(false) }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismiss() },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (cardTheme) {
                    "Default" -> MaterialShareCard(
                        modifier = Modifier
                            .drawWithContent {
                                cardGraphicsLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawLayer(cardGraphicsLayer)
                            },
                        song = song,
                        sortedLines = sortedLines
                    )

                    "Genius" -> GeniusShareCard(
                        modifier = Modifier
                            .drawWithContent {
                                cardGraphicsLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawLayer(cardGraphicsLayer)
                            },
                        song = song,
                        sortedLines = sortedLines
                    )
                }

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 32.dp)
                ) {
                    listOf("Default", "Genius").forEachIndexed { index, style ->
                        SegmentedButton(
                            label = { Text(text = style) },
                            selected = cardTheme == style,
                            onClick = {
                                coroutineScope.launch {
                                    SettingsDataStore.updateCardTheme(context, style)
                                }
                            },
                            shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                                1 -> RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                                else -> RoundedCornerShape(0.dp)
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                namePicker = true
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_download_done_24),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.padding(4.dp))

                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                val bitmap = cardGraphicsLayer.toImageBitmap().asAndroidBitmap()
                                shareImage(context, bitmap, "${song.artists}-${song.title}.png")
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

    if (namePicker) {
        BasicAlertDialog(onDismissRequest = { namePicker = false }
        ) {
            var name by remember { mutableStateOf("${song.artists}-${song.title}.png") }

            Card(shape = RoundedCornerShape(32.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(32.dp)
                )

                Button(
                    onClick = {
                        coroutineScope.launch {
                            namePicker = false
                            val bitmap = cardGraphicsLayer.toImageBitmap().asAndroidBitmap()
                            shareImage(context, bitmap, name, true)
                        }
                    },
                    enabled = name.isNotEmpty() && name.isNotBlank() && name.endsWith(".png"),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        }
    }
}

private fun sortMapByKeys(map: Map<Int, String>): Map<Int, String> {
    val sortedEntries = map.entries.toList().sortedBy { it.key }
    val sortedMap = LinkedHashMap<Int, String>()
    for (entry in sortedEntries) {
        sortedMap[entry.key] = entry.value
    }
    return sortedMap
}

fun shareImage(context: Context, bitmap: Bitmap, name: String, saveToPictures: Boolean = false) {
    val file: File = if (saveToPictures) {
        val picturesDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Rush"
        )
        picturesDir.mkdirs()
        File(picturesDir, name)
    } else {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        File(cachePath, name)
    }

    try {
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
    } catch (e: IOException) {
        e.printStackTrace()
        return
    }

    if (saveToPictures) {
        MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null, null)
        Toast.makeText(context, "Image saved to Pictures/$name", Toast.LENGTH_SHORT).show()
    } else {
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
}