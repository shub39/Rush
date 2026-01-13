package com.shub39.rush.presentation.saved

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.rush.domain.dataclasses.ExtractedColors
import com.shub39.rush.domain.dataclasses.Song
import com.shub39.rush.domain.dataclasses.SongUi
import com.shub39.rush.domain.enums.SortOrder

@Immutable
@Stable
data class SavedPageState(
    val extractedColors: ExtractedColors = ExtractedColors(),
    val currentSong: SongUi? = null,
    val autoChange: Boolean = false,
    val songsByTime: List<Song> = emptyList(),
    val songsAsc: List<Song> = emptyList(),
    val songsDesc: List<Song> = emptyList(),
    val sortOrder: SortOrder = SortOrder.DATE_ADDED,
)
