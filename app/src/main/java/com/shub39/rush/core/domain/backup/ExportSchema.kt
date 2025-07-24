package com.shub39.rush.core.domain.backup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Export")
data class ExportSchema(
    val schemaVersion: Int = 3,
    val songs: List<SongSchema>
)