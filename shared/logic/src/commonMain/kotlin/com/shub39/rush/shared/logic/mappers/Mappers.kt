/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.shared.logic.mappers

import com.shub39.rush.shared.core.backup.SongSchema
import com.shub39.rush.shared.core.dataclasses.Song
import com.shub39.rush.shared.logic.database.SongEntity

fun SongEntity.toSong(): Song {
    return Song(
        id = id,
        title = title,
        artists = artists,
        lyrics = lyrics,
        album = album,
        sourceUrl = sourceUrl,
        artUrl = artUrl,
        syncedLyrics = syncedLyrics,
        geniusLyrics = geniusLyrics,
        dateAdded = dateAdded,
        ttmlLyrics = ttmlLyrics,
    )
}

fun Song.toSongEntity(): SongEntity {
    return SongEntity(
        id = id,
        title = title,
        artists = artists,
        lyrics = lyrics,
        album = album,
        sourceUrl = sourceUrl,
        artUrl = artUrl,
        syncedLyrics = syncedLyrics,
        dateAdded = dateAdded,
        geniusLyrics = geniusLyrics,
        ttmlLyrics = ttmlLyrics,
    )
}

fun Song.toSongSchema(): SongSchema {
    return SongSchema(
        id = id,
        title = title,
        artists = artists,
        lyrics = lyrics,
        album = album,
        sourceUrl = sourceUrl,
        artUrl = artUrl,
        syncedLyrics = syncedLyrics,
        dateAdded = dateAdded,
        geniusLyrics = geniusLyrics,
        ttmlLyrics = ttmlLyrics,
    )
}

fun SongSchema.toSong(): Song {
    return Song(
        id = id,
        title = title,
        artists = artists,
        lyrics = lyrics,
        album = album,
        sourceUrl = sourceUrl,
        artUrl = artUrl,
        syncedLyrics = syncedLyrics,
        dateAdded = dateAdded,
        geniusLyrics = geniusLyrics,
        ttmlLyrics = ttmlLyrics,
    )
}
