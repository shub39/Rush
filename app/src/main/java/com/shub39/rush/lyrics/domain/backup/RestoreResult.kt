package com.shub39.rush.lyrics.domain.backup

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class RestoreResult {
    data object Success : RestoreResult()
    data class Failiure(val exceptionType: RestoreFailedException) : RestoreResult()

    companion object {
        private val _state = MutableStateFlow(RestoreState.IDLE)
        val state = _state.asStateFlow()

        suspend fun updateState(newState: RestoreState) {
            _state.emit(newState)
        }

        suspend fun resetState() {
            _state.emit(RestoreState.IDLE)
        }
    }
}

enum class RestoreState {
    IDLE,
    RESTORING,
    RESTORED
}

sealed interface RestoreFailedException {
    data object InvalidFile : RestoreFailedException
    data object OldSchema : RestoreFailedException
}