package com.shub39.rush.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun ListItemCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    colors: CardColors = CardDefaults.cardColors(),
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}