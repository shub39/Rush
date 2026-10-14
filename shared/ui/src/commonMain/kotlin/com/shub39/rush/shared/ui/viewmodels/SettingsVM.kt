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
package com.shub39.rush.shared.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.shared.core.AnalyticsEvent
import com.shub39.rush.shared.core.backup.ExportRepo
import com.shub39.rush.shared.core.backup.ExportState
import com.shub39.rush.shared.core.backup.RestoreRepo
import com.shub39.rush.shared.core.backup.RestoreResult
import com.shub39.rush.shared.core.backup.RestoreState
import com.shub39.rush.shared.core.dataclasses.OverlaySettings
import com.shub39.rush.shared.core.enums.BackgroundLyricsMode
import com.shub39.rush.shared.core.interfaces.AnalyticsWrapper
import com.shub39.rush.shared.core.interfaces.BackgroundLyricsController
import com.shub39.rush.shared.core.interfaces.BackgroundLyricsPreferences
import com.shub39.rush.shared.core.interfaces.ChangelogManager
import com.shub39.rush.shared.core.interfaces.LyricsSourcePreferences
import com.shub39.rush.shared.core.interfaces.OtherPreferences
import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.core.interfaces.SyncOffsetManager
import com.shub39.rush.shared.ui.setting.SettingsPageAction
import com.shub39.rush.shared.ui.setting.SettingsPageState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class SettingsVM(
    @Provided private val repo: SongRepository,
    @Provided private val datastore: OtherPreferences,
    @Provided private val exportRepo: ExportRepo,
    @Provided private val restoreRepo: RestoreRepo,
    @Provided private val changelogManager: ChangelogManager,
    @Provided private val analytics: AnalyticsWrapper,
    @Provided private val lyricsSourcePreferences: LyricsSourcePreferences,
    @Provided private val syncOffsetManager: SyncOffsetManager,
    @Provided private val backgroundPreferences: BackgroundLyricsPreferences,
    @Provided private val backgroundController: BackgroundLyricsController,
) : ViewModel() {

    private var observeFlowsJob: Job? = null

    private val _state = MutableStateFlow(SettingsPageState())
    val state =
        _state
            .asStateFlow()
            .onStart {
                analytics.trackEvent(AnalyticsEvent.SETTINGS_OPENED, emptyMap())
                observeJob()
                getChangeLogs()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun onAction(action: SettingsPageAction) {
        viewModelScope.launch {
            when (action) {
                SettingsPageAction.OnDeleteSongs -> {
                    analytics.trackEvent(AnalyticsEvent.ALL_SONG_DELETED, emptyMap())
                    repo.deleteAllSongs()
                }

                SettingsPageAction.OnExportSongs -> {
                    _state.update { it.copy(exportState = ExportState.Exporting) }

                    val exportString = exportRepo.exportToJson()

                    if (exportString != null) {
                        analytics.trackEvent(AnalyticsEvent.BACKUP_CREATED, emptyMap())
                    }

                    _state.update {
                        it.copy(
                            exportState =
                                if (exportString != null) ExportState.ExportReady(exportString)
                                else ExportState.Error
                        )
                    }
                }

                is SettingsPageAction.OnRestoreSongs -> {
                    _state.update { it.copy(restoreState = RestoreState.Restoring) }

                    when (val result = restoreRepo.restoreSongs(action.path)) {
                        is RestoreResult.Failure -> {
                            _state.update {
                                it.copy(
                                    restoreState =
                                        RestoreState.Failure(exception = result.exceptionType)
                                )
                            }
                        }

                        RestoreResult.Success -> {
                            analytics.trackEvent(AnalyticsEvent.BACKUP_RESTORED, emptyMap())
                            _state.update { it.copy(restoreState = RestoreState.Restored) }
                        }
                    }
                }

                SettingsPageAction.ResetBackup -> {
                    _state.update {
                        it.copy(
                            restoreState = RestoreState.Idle,
                            exportState = ExportState.Exporting,
                        )
                    }
                }

                is SettingsPageAction.OnThemeSwitch -> {
                    datastore.updateAppThemePref(action.appTheme)
                }

                is SettingsPageAction.OnAmoledSwitch -> {
                    datastore.updateAmoledPref(action.amoled)
                }
                is SettingsPageAction.OnSeedColorChange -> {
                    datastore.updateSeedColor(action.color)
                }
                is SettingsPageAction.OnPaletteChange -> {
                    datastore.updatePaletteStyle(action.style)
                }
                is SettingsPageAction.OnMaterialThemeToggle -> {
                    datastore.updateMaterialTheme(action.pref)
                }

                is SettingsPageAction.OnFontChange -> {
                    datastore.updateFonts(action.fonts)
                }

                is SettingsPageAction.OnToggleLyricsSource -> {
                    val sources = _state.value.lyricsSources
                    // At least one source must stay enabled
                    if (!action.enabled && sources.count { it.enabled } <= 1) return@launch

                    lyricsSourcePreferences.updateSources(
                        sources.map {
                            if (it.provider == action.provider) it.copy(enabled = action.enabled)
                            else it
                        }
                    )
                }

                is SettingsPageAction.OnMoveLyricsSource -> {
                    val sources = _state.value.lyricsSources.toMutableList()
                    val from = sources.indexOfFirst { it.provider == action.provider }
                    val to = from + action.offset
                    if (from < 0 || to !in sources.indices) return@launch

                    sources.add(to, sources.removeAt(from))
                    lyricsSourcePreferences.updateSources(sources)
                }

                SettingsPageAction.OnResetLyricsSources -> lyricsSourcePreferences.resetSources()

                is SettingsPageAction.OnSetDeviceOffset ->
                    syncOffsetManager.setDeviceOffset(action.key, action.offsetMs)

                is SettingsPageAction.OnRemoveDevice -> syncOffsetManager.removeDevice(action.key)

                is SettingsPageAction.OnSetTypeOffset ->
                    syncOffsetManager.setTypeOffset(action.type, action.offsetMs)

                SettingsPageAction.OnBluetoothPermissionChanged -> syncOffsetManager.refresh()

                is SettingsPageAction.OnSetBackgroundMode -> {
                    backgroundPreferences.setMode(action.mode)
                    if (
                        action.mode == BackgroundLyricsMode.OVERLAY &&
                            !backgroundController.canDrawOverlays()
                    ) {
                        backgroundController.requestOverlayPermission()
                    }
                    backgroundController.apply(action.mode)
                }

                SettingsPageAction.OnRequestOverlayPermission ->
                    backgroundController.requestOverlayPermission()

                SettingsPageAction.OnRefreshBackground -> {
                    val permission = backgroundController.canDrawOverlays()
                    val ignoringBattery = backgroundController.isIgnoringBatteryOptimizations()
                    _state.update {
                        it.copy(
                            background =
                                it.background.copy(
                                    overlayPermission = permission,
                                    ignoringBatteryOptimizations = ignoringBattery,
                                )
                        )
                    }
                    // Starts the service once the permission was granted in the system settings
                    val mode = backgroundPreferences.getModeFlow().first()
                    if (mode != BackgroundLyricsMode.OFF && !backgroundController.isRunning.value) {
                        backgroundController.apply(mode)
                    }
                }

                is SettingsPageAction.OnOverlayOpacityChange ->
                    backgroundPreferences.updateOverlaySettings {
                        it.copy(opacity = action.opacity)
                    }

                is SettingsPageAction.OnOverlayTextScaleChange ->
                    backgroundPreferences.updateOverlaySettings {
                        it.copy(textScale = action.textScale)
                    }

                SettingsPageAction.OnResetOverlayLayout ->
                    backgroundPreferences.updateOverlaySettings {
                        OverlaySettings(opacity = it.opacity)
                    }

                SettingsPageAction.OnRequestIgnoreBatteryOptimizations ->
                    backgroundController.requestIgnoreBatteryOptimizations()

                SettingsPageAction.OnOpenAutoStartSettings -> {
                    if (!backgroundController.openAutoStartSettings()) {
                        backgroundController.openAppDetails()
                    }
                }
            }
        }
    }

    private fun getChangeLogs() {
        viewModelScope.launch {
            _state.update { it.copy(changelog = changelogManager.changelogs.first()) }
        }
    }

    private fun observeJob() {
        observeFlowsJob?.cancel()
        observeFlowsJob =
            viewModelScope.launch {
                combine(
                        datastore.getSeedColorFlow(),
                        datastore.getAppThemePrefFlow(),
                        datastore.getAmoledPrefFlow(),
                        datastore.getPaletteStyle(),
                    ) { seedColor, useDarkTheme, withAmoled, style ->
                        _state.update {
                            it.copy(
                                theme =
                                    it.theme.copy(
                                        seedColor = seedColor,
                                        appTheme = useDarkTheme,
                                        withAmoled = withAmoled,
                                        style = style,
                                    )
                            )
                        }
                    }
                    .launchIn(this)

                datastore
                    .getFontFlow()
                    .onEach { pref ->
                        _state.update { it.copy(theme = it.theme.copy(font = pref)) }
                    }
                    .launchIn(this)

                datastore
                    .getMaterialYouFlow()
                    .onEach { pref ->
                        _state.update { it.copy(theme = it.theme.copy(materialTheme = pref)) }
                    }
                    .launchIn(this)

                lyricsSourcePreferences
                    .getSourcesFlow()
                    .onEach { sources -> _state.update { it.copy(lyricsSources = sources) } }
                    .launchIn(this)

                syncOffsetManager.state
                    .onEach { offset -> _state.update { it.copy(syncOffset = offset) } }
                    .launchIn(this)

                combine(
                        backgroundPreferences.getModeFlow(),
                        backgroundPreferences.getOverlaySettingsFlow(),
                        backgroundController.isRunning,
                    ) { mode, overlay, running ->
                        _state.update {
                            it.copy(
                                background =
                                    it.background.copy(
                                        supported = backgroundController.isSupported,
                                        mode = mode,
                                        running = running,
                                        overlayPermission = backgroundController.canDrawOverlays(),
                                        overlay = overlay,
                                        ignoringBatteryOptimizations =
                                            backgroundController.isIgnoringBatteryOptimizations(),
                                        manufacturer = backgroundController.manufacturer,
                                    )
                            )
                        }
                    }
                    .launchIn(this)
            }
    }
}
