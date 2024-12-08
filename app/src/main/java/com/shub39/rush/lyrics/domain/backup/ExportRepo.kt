package com.shub39.rush.lyrics.domain.backup

interface ExportRepo {
    suspend fun exportToJson()
}