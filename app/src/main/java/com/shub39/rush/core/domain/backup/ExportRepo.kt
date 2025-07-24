package com.shub39.rush.core.domain.backup

interface ExportRepo {
    suspend fun exportToJson()
}