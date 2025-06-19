package com.shub39.rush.lyrics.presentation.share.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> ListSelect(
    title: String,
    options: List<T>,
    selected: T,
    onSelectedChange: (T) -> Unit,
    labelProvider: @Composable (T) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        FlowRow(
            horizontalArrangement = Arrangement.Center
        ) {
            options.forEach { option ->
                ToggleButton(
                    checked = option == selected,
                    onCheckedChange = { onSelectedChange(option) },
                    content = { labelProvider(option) },
                    colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}