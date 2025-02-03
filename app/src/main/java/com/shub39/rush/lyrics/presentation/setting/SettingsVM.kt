package com.shub39.rush.lyrics.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.core.data.RushDatastore
import com.shub39.rush.core.domain.Result
import com.shub39.rush.lyrics.data.repository.RushRepository
import com.shub39.rush.lyrics.domain.backup.ExportRepo
import com.shub39.rush.lyrics.domain.backup.ExportState
import com.shub39.rush.lyrics.domain.backup.RestoreRepo
import com.shub39.rush.lyrics.domain.backup.RestoreResult
import com.shub39.rush.lyrics.domain.backup.RestoreState
import com.shub39.rush.lyrics.presentation.setting.component.AudioFile
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsVM(
    private val repo: RushRepository,
    private val datastore: RushDatastore,
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
                is SettingsPageAction.OnBatchDownload -> {
                    batchDownload(action.files)
                }

                SettingsPageAction.OnClearIndexes -> {
                    _state.update {
                        it.copy(
                            batchDownload = BatchDownload(
                                indexes = emptyMap()
                            )
                        )
                    }
                }

                SettingsPageAction.OnDeleteSongs -> {
                    repo.deleteAllSongs()
                }

                is SettingsPageAction.OnUpdateLyricsColor -> {
                    datastore.updateLyricsColor(action.color)
                }

                is SettingsPageAction.OnUpdateMaxLines -> {
                    datastore.updateMaxLines(action.lines)
                }

                is SettingsPageAction.OnUpdateTheme -> {
                    datastore.updateToggleTheme(action.theme)
                }

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
            }
        }
    }

    private fun observeJob() {
        observeJob?.cancel()
        observeJob = combine(
            datastore.getSeedColorFlow(),
            datastore.getDarkThemePrefFlow(),
            datastore.getAmoledPrefFlow(),
            datastore.getPaletteStyle(),
            datastore.getMaxLinesFlow()
        ) { seedColor, useDarkTheme, withAmoled, style, maxLines ->
            _state.update {
                it.copy(
                    theme = it.theme.copy(
                        seedColor = seedColor,
                        useDarkTheme = useDarkTheme,
                        withAmoled = withAmoled,
                        style = style
                    ),
                    maxLines = maxLines
                )
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun batchDownload(
        list: List<AudioFile>,
    ) {
        _state.update {
            it.copy(
                batchDownload = it.batchDownload.copy(
                    isDownloading = true
                )
            )
        }

        list.forEachIndexed { index, audioFile ->
            when (val result = repo.searchGenius(audioFile.title)) {
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
                    val id = result.data.first().id

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

        _state.update {
            it.copy(
                batchDownload = it.batchDownload.copy(
                    isDownloading = false
                )
            )
        }
    }
}