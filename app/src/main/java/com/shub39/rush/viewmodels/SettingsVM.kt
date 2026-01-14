package com.shub39.rush.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.data.repository.RushRepository
import com.shub39.rush.domain.backup.ExportRepo
import com.shub39.rush.domain.backup.ExportState
import com.shub39.rush.domain.backup.RestoreRepo
import com.shub39.rush.domain.backup.RestoreResult
import com.shub39.rush.domain.backup.RestoreState
import com.shub39.rush.domain.interfaces.OtherPreferences
import com.shub39.rush.presentation.setting.SettingsPageAction
import com.shub39.rush.presentation.setting.SettingsPageState
import kotlinx.coroutines.Job
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
    private val restoreRepo: RestoreRepo,
) : ViewModel() {

    private var observeFlowsJob: Job? = null

    private val _state = MutableStateFlow(SettingsPageState())
    val state = _state.asStateFlow()
        .onStart {
            observeJob()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: SettingsPageAction) {
        viewModelScope.launch {
            when (action) {
                SettingsPageAction.OnDeleteSongs -> repo.deleteAllSongs()
                SettingsPageAction.OnExportSongs -> {
                    _state.update {
                        it.copy(exportState = ExportState.Exporting)
                    }

                    val exportString = exportRepo.exportToJson()

                    _state.update {
                        it.copy(
                            exportState = if (exportString != null) ExportState.ExportReady(exportString) else ExportState.Error
                        )
                    }
                }
                is SettingsPageAction.OnRestoreSongs -> {
                    _state.update {
                        it.copy(restoreState = RestoreState.Restoring)
                    }

                    when (val result = restoreRepo.restoreSongs(action.path)) {
                        is RestoreResult.Failure -> {
                            _state.update {
                                it.copy(
                                    restoreState = RestoreState.Failure(exception = result.exceptionType)
                                )
                            }
                        }

                        RestoreResult.Success -> {
                            _state.update {
                                it.copy(restoreState = RestoreState.Restored)
                            }
                        }
                    }
                }
                SettingsPageAction.ResetBackup -> {
                    _state.update {
                        it.copy(
                            restoreState = RestoreState.Idle,
                            exportState = ExportState.Exporting
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

    private fun observeJob() {
        observeFlowsJob?.cancel()
        observeFlowsJob = viewModelScope.launch {
            combine(
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
            }.launchIn(this)

            datastore.getFontFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            theme = it.theme.copy(
                                font = pref
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
}