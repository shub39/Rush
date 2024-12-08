package com.shub39.rush.lyrics.domain.backup

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ExportResult {
    companion object {
        private val _state = MutableStateFlow(ExportState.IDLE)
        val state = _state.asStateFlow()

        suspend fun updateState(newState: ExportState) {
            _state.emit(newState)
        }

        suspend fun resetState() {
            _state.emit(ExportState.IDLE)
        }
    }
}

enum class ExportState {
    IDLE,
    EXPORTING,
    EXPORTED
}