package com.shub39.rush.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import java.awt.datatransfer.StringSelection

actual fun hypnoticAvailable() = true

@Composable
actual fun KeepScreenOn() {}

actual suspend fun Clipboard.copyToClipboard(text: String) {
    setClipEntry(
        ClipEntry(StringSelection(text))
    )
}