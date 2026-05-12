package com.shub39.rush.presentation.saved.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shub39.rush.presentation.saved.SavedPage
import com.shub39.rush.viewmodels.SavedVM
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SavedRoute(
    notificationAccess: Boolean,
    onNavigateToLyrics: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val savedVM: SavedVM = koinViewModel()
    val savedState by savedVM.state.collectAsStateWithLifecycle()

    SavedPage(
        state = savedState,
        notificationAccess = notificationAccess,
        onAction = savedVM::onAction,
        onNavigateToLyrics = onNavigateToLyrics,
        onNavigateToSettings = onNavigateToSettings,
    )
}

