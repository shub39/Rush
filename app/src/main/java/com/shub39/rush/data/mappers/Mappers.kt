package com.shub39.rush.data.mappers

import com.shub39.rush.data.database.SongEntity
import com.shub39.rush.domain.backup.SongSchema
import com.shub39.rush.domain.dataclasses.Song

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
        dateAdded = dateAdded,
        geniusLyrics = geniusLyrics
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
        geniusLyrics = geniusLyrics
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
        geniusLyrics = geniusLyrics
    )
}