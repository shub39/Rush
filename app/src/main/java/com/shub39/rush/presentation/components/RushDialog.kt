package com.shub39.rush.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A generic, customizable dialog composable used throughout the app.
 *
 * @param onDismissRequest Lambda to be invoked when the user attempts to dismiss the dialog,
 * @param modifier The [Modifier] to be applied to the dialog's root container.
 * @param padding The padding to be applied inside the card, around the content.
 * @param content A composable lambda block that defines the content of the dialog.
 *   The content is placed within a [ColumnScope], allowing for direct use of column-specific
 *   modifiers like `weight`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RushDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    padding: Dp = 16.dp,
    content: @Composable (ColumnScope.() -> Unit)
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = content
            )
        }
    }
}