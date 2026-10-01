package com.shub39.rush.shared.ui.pro_sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.ui.component.RushBottomSheet
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import rush.shared.ui.generated.resources.Res
import rush.shared.ui.generated.resources.rush_pro

@Composable
fun BaseProSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState =
        rememberBottomSheetState(
            initialValue = SheetValue.Hidden,
            enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
        ),
    onDismissRequest: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    RushBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().heightIn(max = 700.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            title()

            content()

            FilledTonalButton(
                onClick = onNavigateToPaywall,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Get Pro")
            }
        }
    }
}