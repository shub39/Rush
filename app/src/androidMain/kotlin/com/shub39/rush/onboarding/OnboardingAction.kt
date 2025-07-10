package com.shub39.rush.onboarding

sealed interface OnboardingAction {
    data class OnSetDone(val isDone: Boolean): OnboardingAction
}