package com.shub39.rush.lyrics.domain

interface ExportRepo {
    suspend fun exportToJson()
}