package com.shub39.rush.saved

import androidx.compose.runtime.Immutable
import com.shub39.rush.core.domain.data_classes.Song
import com.shub39.rush.core.domain.enums.SortOrder

@Immutable
data class SavedPageState(
    val songsByTime: List<Song> = emptyList(),
    val songsAsc: List<Song> = emptyList(),
    val songsDesc: List<Song> = emptyList(),
    val sortOrder: SortOrder = SortOrder.DATE_ADDED,
)
