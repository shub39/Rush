package com.shub39.rush.app

import android.content.Context

sealed interface GlobalAction {
    data object OnTogglePaywall : GlobalAction
    data class OnUpdateOnboardingDone(val status: Boolean) : GlobalAction
    data class OnCheckNotificationAccess(val context: Context) : GlobalAction
}