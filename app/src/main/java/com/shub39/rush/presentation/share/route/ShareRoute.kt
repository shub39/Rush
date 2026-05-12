package com.shub39.rush.presentation.share.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shub39.rush.presentation.share.SharePage
import com.shub39.rush.viewmodels.ShareVM
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShareRoute(
    isProUser: Boolean,
    onDismiss: () -> Unit,
    onShowPaywall: () -> Unit,
) {
    val shareVM: ShareVM = koinViewModel()
    val shareState by shareVM.state.collectAsStateWithLifecycle()

    SharePage(
        onDismiss = onDismiss,
        state = shareState,
        onAction = shareVM::onAction,
        isProUser = isProUser,
        onShowPaywall = onShowPaywall,
    )
}

