package com.shub39.rush.presentation.paywall.route

import androidx.compose.runtime.Composable
import com.shub39.rush.billing.PaywallPage

@Composable
fun PaywallRoute(
    isProUser: Boolean,
    onDismissRequest: () -> Unit,
) {
    PaywallPage(
        isProUser = isProUser,
        onDismissRequest = onDismissRequest,
    )
}

