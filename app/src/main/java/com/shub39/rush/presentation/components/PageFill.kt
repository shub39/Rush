package com.shub39.rush.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * A composable that fills the entire available screen space and centers its content.
 * This is a convenience wrapper around a [Box] with `Modifier.fillMaxSize()` and
 * `contentAlignment = Alignment.Center`.
 *
 * @param content The composable content to be displayed in the center of the page.
 * The content is placed within a [BoxScope], allowing for more complex layouts if needed.
 */
@Composable
fun PageFill(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = content
    )
}