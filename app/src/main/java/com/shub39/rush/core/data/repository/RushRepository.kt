package com.shub39.rush.core.data.repository

import com.shub39.rush.core.data.database.SongDao
import com.shub39.rush.core.data.mappers.toSong
import com.shub39.rush.core.data.mappers.toSongEntity
import com.shub39.rush.core.data.network.GeniusApi
import com.shub39.rush.core.data.network.GeniusScraper
import com.shub39.rush.core.data.network.LrcLibApi
import com.shub39.rush.core.domain.Result
import com.shub39.rush.core.domain.SongRepo
import com.shub39.rush.core.domain.SourceError
import com.shub39.rush.core.domain.data_classes.LrcLibSong
import com.shub39.rush.core.domain.data_classes.SearchResult
import com.shub39.rush.core.domain.data_classes.Song
import com.shub39.rush.core.presentation.getMainArtist
import com.shub39.rush.core.presentation.getMainTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class RushRepository(
    private val localDao: SongDao,
    private val geniusApi: GeniusApi,
    private val lrcLibApi: LrcLibApi,
    private val geniusScraper: GeniusScraper
) : SongRepo {
    @OptIn(ExperimentalTime::class)
    override suspend fun fetchSong(result: SearchResult): Result<Song, SourceError> {
        try {
            val lrcLibLyrics = withContext(Dispatchers.IO) {
                lrcLibApi.getLrcLyrics(
                    trackName = getMainTitle(result.title),
                    artistName = getMainArtist(result.artist)
                )
            }
            val geniusLyrics = if (lrcLibLyrics == null) {
                withContext(Dispatchers.IO) {
                    (geniusScraper.geniusScrape(result.url) as? Result.Success)?.data
                }
            } else {
                null
            }

            return Result.Success<Song, SourceError>(
                Song(
                    id = result.id,
                    title = result.title,
                    artists = result.artist,
                    lyrics = lrcLibLyrics?.plainLyrics ?: "",
                    album = result.album,
                    sourceUrl = result.url,
                    artUrl = result.artUrl,
                    geniusLyrics = geniusLyrics,
                    syncedLyrics = lrcLibLyrics?.syncedLyrics,
                    dateAdded = Clock.System.now().epochSeconds,
                )
            ).also {
                localDao.insertSong(it.data.toSongEntity())
            }
        } catch (e: Exception) {
            return Result.Error(
                SourceError.Data.UNKNOWN,
                "Unexpected exception: $e"
            )
        }
    }

    override suspend fun scrapeGeniusLyrics(id: Long, url: String): Result<String, SourceError> {
        val request = withContext(Dispatchers.IO) {
            geniusScraper.geniusScrape(url)
        }

        return when (request) {
            is Result.Error -> {
                Result.Error(error = request.error, debugMessage = request.debugMessage)
            }
            is Result.Success -> {
                Result.Success<String, SourceError>(request.data.ifBlank { "[INSTRUMENTAL]" }).also {
                    localDao.updateGeniusLyrics(
                        id = id,
                        lyrics = it.data
                    )
                }
            }
        }
    }

    override suspend fun searchGenius(query: String): Result<List<SearchResult>, SourceError> {
        val result = withContext(Dispatchers.IO) {
            geniusApi.geniusSearch(query)
        }

        when (result) {
            is Result.Success -> {
                val results = result.data.response.hits.filter { it.type == "song" }
                val searchResults = results.filter { it.type == "song" }.map { hit ->
                    SearchResult(
                        title = hit.result.title,
                        artist = hit.result.artistNames,
                        album = null,
                        artUrl = hit.result.songArtImageURL,
                        url = hit.result.url,
                        id = hit.result.id
                    )
                }

                return Result.Success(searchResults)
            }

            is Result.Error -> {
                return Result.Error(error = result.error, debugMessage = result.debugMessage)
            }
        }
    }

    override suspend fun searchLrcLib(
        track: String,
        artist: String
    ): Result<List<LrcLibSong>, SourceError> {
        val result = withContext(Dispatchers.IO) {
            lrcLibApi.searchLrcLyrics(track, artist)
        }

        when (result) {
            is Result.Success -> {
                val searchResults = result.data.filter { it.instrumental == false }.map { dto ->
                    LrcLibSong(
                        id = dto.id.toInt(),
                        name = dto.name,
                        trackName = dto.trackName,
                        artistName = dto.artistName ?: "???",
                        albumName = dto.albumName ?: "???",
                        duration = dto.duration ?: 0.0,
                        instrumental = false,
                        plainLyrics = dto.plainLyrics,
                        syncedLyrics = dto.syncedLyrics,
                    )
                }

                return Result.Success(searchResults)
            }

            is Result.Error -> {
                return Result.Error(error = result.error, debugMessage = result.debugMessage)
            }
        }
    }

    override suspend fun insertSong(song: Song) {
        localDao.insertSong(song.toSongEntity())
    }

    override suspend fun getSongs(): Flow<List<Song>> {
        return localDao
            .getAllSongs().map { entities ->
                entities.map { it.toSong() }
            }
    }

    override suspend fun getAllSongs(): List<Song> {
        return localDao.getAllSongs().first().map { it.toSong() }
    }

    override suspend fun getSong(id: Long): Song {
        return localDao.getSongById(id).toSong()
    }

    override suspend fun getSong(query: String): List<Song> {
        return localDao.searchSong(query).map { it.toSong() }
    }

    override suspend fun deleteSong(id: Long) {
        localDao.deleteSong(id)
    }

    override suspend fun updateLrcLyrics(id: Long, plainLyrics: String, syncedLyrics: String?) {
        localDao.updateLrcLyricsById(id, plainLyrics, syncedLyrics)
    }

    override suspend fun deleteAllSongs() {
        localDao.deleteAllSongs()
    }
}