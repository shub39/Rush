package com.shub39.rush.share

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.compose.rememberShareFileLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
import java.io.FileOutputStream
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun ShareButton(
    coroutineScope: CoroutineScope,
    cardGraphicsLayer: GraphicsLayer
) {
    val context = LocalContext.current
    val launcher = rememberShareFileLauncher()

    IconButton(
        onClick = {
            coroutineScope.launch {
                val imageBitmap = withContext(Dispatchers.Main) {
                    cardGraphicsLayer.toImageBitmap().asAndroidBitmap()
                }

                withContext(Dispatchers.IO) {
                    val cachePath = File(context.cacheDir, "images")
                    cachePath.mkdirs()
                    val file = File(cachePath, "image.png")

                    val stream = FileOutputStream(file)
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.close()

                    val contentUri: Uri =
                        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

                    launcher.launch(PlatformFile(contentUri))
                }
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            modifier = Modifier.size(24.dp)
        )
    }

    Spacer(modifier = Modifier.padding(4.dp))
}

@OptIn(ExperimentalTime::class)
fun getFormattedTime(): String {
    val now = Clock.System.now()
    val localTime = now.toLocalDateTime(TimeZone.currentSystemDefault()).time

    val hour = localTime.hour % 12
    val minute = localTime.minute
    val amPm = if (localTime.hour < 12) "AM" else "PM"

    val hourFormatted = if (hour == 0) 12 else hour
    val minuteFormatted = minute.toString().padStart(2, '0')

    return "$hourFormatted:$minuteFormatted $amPm"
}