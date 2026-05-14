package com.shub39.rush.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.presentation.theme.RushTheme

class RushPreviewWrapper : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable (() -> Unit)) {
        RushTheme(theme = Theme(), content = content)
    }
}
