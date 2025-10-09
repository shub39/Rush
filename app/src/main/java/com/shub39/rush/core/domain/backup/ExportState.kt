package com.shub39.rush.core.domain.backup

sealed interface ExportState {
    data object Exporting: ExportState
    data class ExportReady(val data: String): ExportState
    data object Error: ExportState
}