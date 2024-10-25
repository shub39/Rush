package com.shub39.rush.ui.page.saved

import androidx.compose.runtime.Immutable
import com.shub39.rush.database.Song

@Immutable
data class SavedPageState(
    val songsAsc: List<Song> = emptyList(),
    val songsDesc: List<Song> = emptyList(),
    val groupedAlbum: List<Map.Entry<String, List<Song>>> = emptyList(),
    val groupedArtist: List<Map.Entry<String, List<Song>>> = emptyList(),
    val autoChange: Boolean = false
)
