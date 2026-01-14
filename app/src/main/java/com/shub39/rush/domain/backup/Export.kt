package com.shub39.rush.domain.backup

interface ExportRepo {
    suspend fun exportToJson(): String?
}

sealed interface ExportState {
    data object Exporting: ExportState
    data class ExportReady(val data: String): ExportState
    data object Error: ExportState
}