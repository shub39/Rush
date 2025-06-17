package com.shub39.rush.lyrics.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.core.domain.OtherPreferences
import com.shub39.rush.lyrics.data.repository.RushRepository
import com.shub39.rush.lyrics.domain.backup.ExportRepo
import com.shub39.rush.lyrics.domain.backup.ExportState
import com.shub39.rush.lyrics.domain.backup.RestoreRepo
import com.shub39.rush.lyrics.domain.backup.RestoreResult
import com.shub39.rush.lyrics.domain.backup.RestoreState
import com.shub39.rush.lyrics.presentation.setting.BatchDownload
import com.shub39.rush.lyrics.presentation.setting.SettingsPageAction
import com.shub39.rush.lyrics.presentation.setting.SettingsPageState
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
            SharingStarted.Companion.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: SettingsPageAction) {
        viewModelScope.launch {
            when (action) {
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

                    when (restoreRepo.restoreSongs(action.path)) {
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
}