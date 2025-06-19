package com.shub39.rush.lyrics.presentation.share

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
import java.io.File
import java.io.FileOutputStream

@Composable
actual fun ShareButton(
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