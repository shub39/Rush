package com.shub39.rush.app

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.rush.domain.dataclasses.Theme

@Stable
@Immutable
data class GlobalState(
    val isProUser: Boolean = false,
    val showPaywall: Boolean = false,
    val theme: Theme = Theme(),
    val onBoardingDone: Boolean = true,
    val notificationAccess: Boolean = false
)
