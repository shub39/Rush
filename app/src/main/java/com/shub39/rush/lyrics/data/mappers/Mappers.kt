package com.shub39.rush.lyrics.data.mappers

import com.shub39.rush.lyrics.data.database.SongEntity
import com.shub39.rush.lyrics.data.backup.export.SongSchema
import com.shub39.rush.lyrics.domain.Song

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
        dateAdded = dateAdded
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
        geniusLyrics = geniusLyrics,
        dateAdded = dateAdded
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
        geniusLyrics = geniusLyrics,
        dateAdded = dateAdded
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
        geniusLyrics = geniusLyrics,
        dateAdded = dateAdded,
    )
}