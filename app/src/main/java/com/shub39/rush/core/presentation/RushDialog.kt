package com.shub39.rush.core.presentation

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

// Generic Dialog used app wide
@Composable
fun RushDialog(
    onDismissRequest: () -> Unit,
    content: @Composable  (ColumnScope.() -> Unit)
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = Modifier.widthIn(max = 500.dp),
            shape = MaterialTheme.shapes.extraLarge,
            content = content
        )
    }
}