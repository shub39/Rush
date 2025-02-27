package com.shub39.rush.lyrics.presentation.share.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ListSelect(
    title: String,
    options: List<T>,
    selected: T,
    onSelectedChange: (T) -> Unit,
    labelProvider: (T) -> Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        FlowRow(
            horizontalArrangement = Arrangement.Center
        ) {
            options.forEach { option ->
                InputChip(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    selected = option == selected,
                    onClick = { onSelectedChange(option) },
                    label = { Text(
                        text = stringResource(labelProvider(option)),
                        style = MaterialTheme.typography.bodyMedium
                    ) }
                )
            }
        }
    }
}