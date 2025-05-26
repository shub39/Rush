package com.shub39.rush.core.presentation

import androidx.compose.runtime.Composable
import com.shub39.rush.core.domain.data_classes.Theme

@Composable
expect fun RushTheme(
    state: Theme = Theme(),
    content: @Composable () -> Unit
)