package com.shub39.rush.lyrics.presentation.viewmodels

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.core.domain.OtherPreferences
import com.shub39.rush.core.domain.Result
import com.shub39.rush.core.presentation.getMainArtist
import com.shub39.rush.core.presentation.getMainTitle
import com.shub39.rush.lyrics.data.repository.RushRepository
import com.shub39.rush.lyrics.domain.backup.ExportRepo
import com.shub39.rush.lyrics.domain.backup.ExportState
import com.shub39.rush.lyrics.domain.backup.RestoreRepo
import com.shub39.rush.lyrics.domain.backup.RestoreResult
import com.shub39.rush.lyrics.domain.backup.RestoreState
import com.shub39.rush.lyrics.presentation.setting.BatchDownload
import com.shub39.rush.lyrics.presentation.setting.SettingsPageAction
import com.shub39.rush.lyrics.presentation.setting.SettingsPageState
import com.shub39.rush.lyrics.presentation.setting.component.AudioFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsVM(
    private val repo: RushRepository,
    private val datastore: OtherPreferences,
    private val exportRepo: ExportRepo,
    private val restoreRepo: RestoreRepo
) : ViewModel() {

    private var observeJob: Job? = null

    private val _state = MutableStateFlow(SettingsPageState())
    val state = _state.asStateFlow()
        .onStart { observeJob() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onSettingsPageAction(action: SettingsPageAction) {
        viewModelScope.launch {
            when (action) {
                is SettingsPageAction.OnBatchDownload -> batchDownload()


                SettingsPageAction.OnClearIndexes -> {
                    _state.update {
                        it.copy(
                            batchDownload = BatchDownload(
                                indexes = emptyMap(),
                                audioFiles = emptyList()
                            )
                        )
                    }
                }

                SettingsPageAction.OnDeleteSongs -> repo.deleteAllSongs()

                is SettingsPageAction.OnUpdateMaxLines -> datastore.updateMaxLines(action.lines)


                SettingsPageAction.OnExportSongs -> {
                    _state.update {
                        it.copy(
                            exportState = ExportState.EXPORTING
                        )
                    }

                    exportRepo.exportToJson()

                    _state.update {
                        it.copy(
                            exportState = ExportState.EXPORTED
                        )
                    }
                }

                is SettingsPageAction.OnRestoreSongs -> {
                    _state.update {
                        it.copy(
                            restoreState = RestoreState.RESTORING
                        )
                    }

                    when (restoreRepo.restoreSongs(action.uri)) {
                        is RestoreResult.Failure -> {
                            _state.update {
                                it.copy(
                                    restoreState = RestoreState.FAILURE
                                )
                            }
                        }

                        RestoreResult.Success -> {
                            _state.update {
                                it.copy(
                                    restoreState = RestoreState.RESTORED
                                )
                            }
                        }
                    }
                }

                SettingsPageAction.ResetBackup -> {
                    _state.update {
                        it.copy(
                            restoreState = RestoreState.IDLE,
                            exportState = ExportState.IDLE
                        )
                    }
                }

                is SettingsPageAction.OnThemeSwitch -> datastore.updateAppThemePref(action.appTheme)

                is SettingsPageAction.OnAmoledSwitch -> datastore.updateAmoledPref(action.amoled)

                is SettingsPageAction.OnSeedColorChange -> datastore.updateSeedColor(action.color)

                is SettingsPageAction.OnPaletteChange -> datastore.updatePaletteStyle(action.style)

                is SettingsPageAction.OnMaterialThemeToggle -> datastore.updateMaterialTheme(action.pref)

                is SettingsPageAction.OnFontChange -> datastore.updateFonts(action.fonts)

                is SettingsPageAction.OnProcessAudioFiles -> {
                    launch(Dispatchers.IO) {
                        _state.update {
                            it.copy(
                                batchDownload = it.batchDownload.copy(
                                    isLoadingFiles = true
                                )
                            )
                        }

                        val documentFile = DocumentFile.fromTreeUri(action.context, action.uri)

                        documentFile?.let {
                            processFiles(action.context, documentFile)
                        }

                        _state.update {
                            it.copy(
                                batchDownload = it.batchDownload.copy(
                                    isLoadingFiles = false
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeJob() = viewModelScope.launch {
        observeJob?.cancel()
        observeJob = launch {
            observeTheme().launchIn(this)

            datastore.getFontFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            theme = it.theme.copy(
                                fonts = pref
                            )
                        )
                    }
                }
                .launchIn(this)

            datastore.getMaterialYouFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            theme = it.theme.copy(
                                materialTheme = pref
                            )
                        )
                    }
                }
                .launchIn(this)

            datastore.getMaxLinesFlow()
                .onEach { lines ->
                    _state.update {
                        it.copy(
                            maxLines = lines
                        )
                    }
                }
                .launchIn(this)
        }
    }

    // this variant of combine takes at most 5 flows and the other variant doesn't work with nullable values
    private fun observeTheme(): Flow<Unit> {
        return combine(
            datastore.getSeedColorFlow(),
            datastore.getAppThemePrefFlow(),
            datastore.getAmoledPrefFlow(),
            datastore.getPaletteStyle(),
        ) { seedColor, useDarkTheme, withAmoled, style ->
            _state.update {
                it.copy(
                    theme = it.theme.copy(
                        seedColor = seedColor,
                        appTheme = useDarkTheme,
                        withAmoled = withAmoled,
                        style = style
                    )
                )
            }
        }
    }

    private fun processFiles(
        context: Context,
        directory: DocumentFile
    ) {
        for (file in directory.listFiles()) {
            if (file.isDirectory) {
                processFiles(context, file)
            } else if (file.isFile && file.type?.startsWith("audio/") == true) {
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(context, file.uri)

                    val title =
                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                    val artist =
                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)

                    if (title != null && artist != null) {
                        _state.update {
                            it.copy(
                                batchDownload = it.batchDownload.copy(
                                    audioFiles = it.batchDownload.audioFiles.plus(
                                        AudioFile(getMainTitle(title), getMainArtist(artist))
                                    )
                                )
                            )
                        }
                    }

                    retriever.release()
                } catch (e: Exception) {
                    Log.d("BatchDownloader", "Can't set data source $e")
                }
            }
        }
    }

    private suspend fun batchDownload(
        list: List<AudioFile> = _state.value.batchDownload.audioFiles,
    ) {
        _state.update {
            it.copy(
                batchDownload = it.batchDownload.copy(
                    isDownloading = true
                )
            )
        }

        list.forEachIndexed { index, audioFile ->
            when (val result = repo.searchGenius("${audioFile.title} ${audioFile.artist}")) {
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            batchDownload = it.batchDownload.copy(
                                indexes = it.batchDownload.indexes.plus(index to false)
                            )
                        )
                    }
                }

                is Result.Success -> {
                    val id = result.data.firstOrNull()?.id

                    if (id == null) {
                        _state.update {
                            it.copy(
                                batchDownload = it.batchDownload.copy(
                                    indexes = it.batchDownload.indexes + (index to false)
                                )
                            )
                        }

                        return@forEachIndexed
                    } else {
                        when (repo.fetchSong(id)) {
                            is Result.Error -> {
                                _state.update {
                                    it.copy(
                                        batchDownload = it.batchDownload.copy(
                                            indexes = it.batchDownload.indexes + (index to false)
                                        )
                                    )
                                }
                            }

                            is Result.Success -> {
                                _state.update {
                                    it.copy(
                                        batchDownload = it.batchDownload.copy(
                                            indexes = it.batchDownload.indexes + (index to true),
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        _state.update {
            it.copy(
                batchDownload = it.batchDownload.copy(
                    isDownloading = false
                )
            )
        }
    }
}