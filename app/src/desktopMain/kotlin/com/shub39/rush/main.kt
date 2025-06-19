package com.shub39.rush

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.shub39.rush.di.initKoin

fun main() {
    initKoin()

    singleWindowApplication(
        title = "Rush",
        state = WindowState(width = 1200.dp, height = 900.dp),
        resizable = false,
        alwaysOnTop = true
    ) {
        RushApp() // Broke for some reason, maybe because of cmp beta??
    }

//    singleWindowApplication(
//        title = "Hot Reload",
//        state = WindowState(width = 400.dp, height = 950.dp),
//        resizable = false,
//        alwaysOnTop = true
//    ) {
//        val songs = (0..100).map {
//            Song(
//                id = it.toLong(),
//                title = "Song $it",
//                artists = "Artist $it",
//                lyrics = "L",
//                album = "Album $it",
//                sourceUrl = "",
//                artUrl = "",
//                geniusLyrics = "",
//                syncedLyrics = "",
//                dateAdded = it.toLong()
//            )
//        }
//        var state by remember { mutableStateOf(SavedPageState(
//            songsAsc = songs.sortedBy { it.title },
//            songsDesc = songs.sortedByDescending { it.title },
//            songsByTime = songs.sortedByDescending { it.dateAdded },
//            sortOrder = SortOrder.DATE_ADDED
//        )) }
//
//        RushTheme(
//            state = Theme(
//                appTheme = AppTheme.DARK,
//                fonts = Fonts.MANROPE
//            )
//        ) {
//            SavedPage(
//                state = state,
//                currentSong = songs.firstOrNull()?.toSongUi(),
//                autoChange = false,
//                notificationAccess = true,
//                showCurrent = true,
//                action = {},
//                onNavigateToLyrics = {},
//                onNavigateToSettings = {}
//            )
//        }
//    }
}