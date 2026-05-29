/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.shared.ui.share

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberShareFileLauncher
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import io.github.vinceglb.filekit.write
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import rush.shared.ui.generated.resources.Res
import rush.shared.ui.generated.resources.share

@Composable
actual fun SharePage(
    onDismiss: () -> Unit,
    state: SharePageState,
    isProUser: Boolean,
    onShowPaywall: () -> Unit,
    onAction: (SharePageAction) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val cardGraphicsLayer = rememberGraphicsLayer()
    val fullScreenGraphicsLayer = rememberGraphicsLayer()

    var selectedImage: PlatformFile? by remember { mutableStateOf(null) }
    var saveImage: ImageBitmap? by remember { mutableStateOf(null) }

    val imagePicker =
        rememberFilePickerLauncher(type = FileKitType.Image) { image -> selectedImage = image }

    val imageSaver =
        rememberFileSaverLauncher(dialogSettings = FileKitDialogSettings()) { file ->
            if (saveImage != null) {
                coroutineScope.launch(Dispatchers.IO) {
                    file?.write(saveImage!!.encodeToByteArray(format = ImageFormat.PNG))
                }
            }
        }

    val shareLauncher = rememberShareFileLauncher()

    SharePageContent(
        state = state,
        onDismiss = onDismiss,
        selectedImage = selectedImage,
        onAction = onAction,
        cardGraphicsLayer = cardGraphicsLayer,
        fullScreenGraphicsLayer = fullScreenGraphicsLayer,
        onSaveImage = {
            saveImage = it
            imageSaver.launch(
                suggestedName = "${state.songDetails.title} - ${state.songDetails.artist}",
                defaultExtension = "png",
            )
        },
        onLaunchImagePicker = { imagePicker.launch() },
        isProUser = isProUser,
        onShowPaywall = onShowPaywall,
        onShareImage = {
            coroutineScope.launch(Dispatchers.IO) {
                val graphicsLayer =
                    if (state.fullScreen) fullScreenGraphicsLayer else cardGraphicsLayer
                val imageBitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()

                val cachePath = File(context.cacheDir, "images")
                cachePath.mkdirs()
                val file = File(cachePath, "image.png")

                val stream = FileOutputStream(file)
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()

                val contentUri: Uri =
                    FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

                shareLauncher.launch(PlatformFile(contentUri))
            }
        },
    )
}

@Composable
actual fun RowScope.ShareButton(onClick: () -> Unit, modifier: Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(Res.drawable.share),
            contentDescription = "Share",
            modifier = Modifier.size(24.dp),
        )
    }
}
