package com.shub39.rush.core.presentation

import androidx.compose.runtime.Composable
import com.shub39.rush.core.data.Theme

@Composable
expect fun RushTheme(
    state: Theme = Theme(),
    content: @Composable () -> Unit
)