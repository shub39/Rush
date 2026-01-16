package com.shub39.rush.presentation.saved.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.presentation.saved.SavedPageAction
import com.shub39.rush.presentation.saved.SavedPageState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SavedPageActions(
    isRow: Boolean,
    state: SavedPageState,
    notificationAccess: Boolean,
    onAction: (SavedPageAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    modifier: Modifier = Modifier
) {
    @Composable
    fun Buttons() {
        if (notificationAccess) {
            FloatingActionButton(
                onClick = {
                    onAction(SavedPageAction.OnToggleAutoChange)
                    if (!state.autoChange) {
                        onNavigateToLyrics()
                    }
                },
                shape = MaterialShapes.Sunny.toShape(),
                containerColor = if (state.autoChange) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSecondary,
                contentColor = if (state.autoChange) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.secondary,
            ) {
                Icon(
                    painter = painterResource(R.drawable.meteor),
                    contentDescription = "Rush Mode",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        MediumFloatingActionButton(
            onClick = { onAction(SavedPageAction.OnToggleSearchSheet) }
        ) {
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = "Search",
                modifier = Modifier.size(40.dp)
            )
        }
    }

    if (isRow) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Buttons()
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Buttons()
        }
    }
}
