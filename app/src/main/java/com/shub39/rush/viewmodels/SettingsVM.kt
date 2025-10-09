package com.shub39.rush.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.billing.BillingHandler
import com.shub39.rush.billing.SubscriptionResult
import com.shub39.rush.core.data.repository.RushRepository
import com.shub39.rush.core.domain.OtherPreferences
import com.shub39.rush.core.domain.backup.ExportRepo
import com.shub39.rush.core.domain.backup.ExportState
import com.shub39.rush.core.domain.backup.RestoreRepo
import com.shub39.rush.core.domain.backup.RestoreResult
import com.shub39.rush.core.domain.backup.RestoreState
import com.shub39.rush.setting.SettingsPageAction
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
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
    private val stateLayer: StateLayer,
    private val repo: RushRepository,
    private val datastore: OtherPreferences,
    private val exportRepo: ExportRepo,
    private val restoreRepo: RestoreRepo,
    private val billingHandler: BillingHandler
) : ViewModel() {

    private var observeFlowsJob: Job? = null

    private val _state = stateLayer.settingsState
    val state = _state.asStateFlow()
        .onStart {
            checkSubscription()
            observeJob()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: SettingsPageAction) {
        viewModelScope.launch {
            when (action) {
                SettingsPageAction.OnDeleteSongs -> repo.deleteAllSongs()

                SettingsPageAction.OnExportSongs -> {
                    _state.update {
                        it.copy(
                            exportState = ExportState.Exporting
                        )
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
                        it.copy(
                            restoreState = RestoreState.Restoring
                        )
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
                                it.copy(
                                    restoreState = RestoreState.Restored
                                )
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

                SettingsPageAction.OnDismissPaywall -> {
                    _state.update { it.copy(showPaywall = false) }
                    checkSubscription()
                }

                SettingsPageAction.OnShowPaywall -> _state.update { it.copy(showPaywall = true) }

                is SettingsPageAction.OnUpdateOnboardingDone -> datastore.updateOnboardingDone(action.done)
            }
        }
    }

    private suspend fun checkSubscription() {
        val isSubscribed = billingHandler.userResult()

        when (isSubscribed) {
            SubscriptionResult.NotSubscribed -> datastore.resetAppTheme()
            SubscriptionResult.Subscribed -> {
                _state.update { it.copy(isProUser = true) }
                stateLayer.sharePageState.update { it.copy(isProUser = true) }
            }
            else -> {}
        }
    }

    private fun observeJob() {
        observeFlowsJob?.cancel()
        observeFlowsJob = viewModelScope.launch {
            observeTheme().launchIn(this)

            datastore.getOnboardingDoneFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            onBoardingDone = pref
                        )
                    }
                }
                .launchIn(this)

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