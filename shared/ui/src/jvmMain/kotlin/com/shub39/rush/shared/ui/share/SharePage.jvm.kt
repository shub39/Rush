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

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
actual fun SharePage(
    onDismiss: () -> Unit,
    state: SharePageState,
    isProUser: Boolean,
    onShowPaywall: () -> Unit,
    onAction: (SharePageAction) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
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
        onShareImage = {},
    )
}

@Composable actual fun RowScope.ShareButton(onClick: () -> Unit, modifier: Modifier) {}
