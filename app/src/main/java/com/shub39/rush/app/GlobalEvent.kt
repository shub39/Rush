package com.shub39.rush.app

sealed interface GlobalEvent {
    data object NavigateToOnboarding : GlobalEvent
}