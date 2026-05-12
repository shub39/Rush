package com.shub39.rush.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.BuildConfig
import com.shub39.rush.app.GlobalAction
import com.shub39.rush.app.GlobalEvent
import com.shub39.rush.app.state.GlobalOverlay
import com.shub39.rush.app.state.GlobalState
import com.shub39.rush.billing.BillingHandler
import com.shub39.rush.billing.SubscriptionResult
import com.shub39.rush.data.ChangelogManager
import com.shub39.rush.domain.PermissionsHelper
import com.shub39.rush.domain.interfaces.OtherPreferences
import com.shub39.rush.warning.FossWarningCalculator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Named

@KoinViewModel
class GlobalVM(
    private val billingHandler: BillingHandler,
    private val otherPreferences: OtherPreferences,
    private val changelogManager: ChangelogManager,
    private val datastore: OtherPreferences,
    @Named("PermissionsHelper") private val permissionsHelper: PermissionsHelper
) : ViewModel() {

    /**
     * MutableStateFlow is already hot and stateful
     * - keeps latest value
     * - survives configuration changes via ViewModel (koin ftw)
     * - replays to new collectors
     *
     * stateIn() is only needed for cold flows (e.g. combine/map/repository flows).
     * Using it here would add unnecessary sharing layers without benefits.
     */
    private val _state = MutableStateFlow(GlobalState())
    val state: StateFlow<GlobalState> = _state.asStateFlow()

    private val _overlay = MutableStateFlow<GlobalOverlay>(GlobalOverlay.None)
    val overlay: StateFlow<GlobalOverlay> = _overlay.asStateFlow()

    init {
        bootstrap()
    }

    /**
     * App-wide initialization work that should happen once
     * during the ViewModel lifecycle (view model initialization).
     */
    private fun bootstrap() {
        observePreferences()
        checkSubscription()
        updateFossWarningDays()
        checkChangelog()
    }

    private fun updateFossWarningDays() {
        if (BuildConfig.FLAVOR != "foss") return

        _state.update {
            it.copy(fossWarningDaysLeft = FossWarningCalculator.daysLeft())
        }
    }

    private val _globalEvents = Channel<GlobalEvent>(Channel.BUFFERED)
    val globalEvents: Flow<GlobalEvent> = _globalEvents.receiveAsFlow()

    fun onAction(action: GlobalAction) {
        when (action) {
            is GlobalAction.OnUpdateOnboardingDone -> {
                updateOnboarding(action.status)
            }

            is GlobalAction.OnCheckNotificationAccess -> {
                checkNotificationAccess()
            }

            GlobalAction.DismissChangelog -> {
                dismissOverlay()
            }
        }
    }

    private fun updateOnboarding(status: Boolean) {
        viewModelScope.launch {
            otherPreferences.updateOnboardingDone(status)
        }
    }

    /**
     * Lifecycle-bound async operation.
     * The caller should not care about coroutine management.
     */
    private fun checkSubscription() {
        viewModelScope.launch {
            when (billingHandler.userResult()) {
                SubscriptionResult.Subscribed -> {
                    _state.update { it.copy(isProUser = true) }
                }

                else -> Unit
            }
        }
    }

    private fun checkNotificationAccess() {
        val hasAccess = permissionsHelper.hasNotificationAccess()

        _state.update {
            it.copy(notificationAccess = hasAccess)
        }
    }

    private fun checkChangelog() {
        viewModelScope.launch {

            val changelog = changelogManager.changelogs.first().firstOrNull()

            val lastShown = datastore.getLastChangelogShown().first()

            if (changelog == null) return@launch

            val shouldShow = BuildConfig.DEBUG || lastShown != BuildConfig.VERSION_NAME

            if (!shouldShow) return@launch

            _overlay.update {

                if (BuildConfig.FLAVOR == "foss") {
                    GlobalOverlay.FossWarning(
                        daysLeft = FossWarningCalculator.daysLeft(),
                    )
                } else {
                    GlobalOverlay.Changelog(
                        changelog = changelog,
                    )
                }

            }
        }
    }

    private fun dismissOverlay() {
        viewModelScope.launch {
            datastore.updateLastChangelogShown(BuildConfig.VERSION_NAME)
            _overlay.update { GlobalOverlay.None }
        }
    }

    private fun observePreferences() {
        combine(
            otherPreferences.getFontFlow(),
            otherPreferences.getPaletteStyle(),
            otherPreferences.getSeedColorFlow(),
            otherPreferences.getAmoledPrefFlow(),
            otherPreferences.getAppThemePrefFlow(),
        ) { font, style, seedColor, withAmoled, appTheme ->

            _state.update {
                it.copy(
                    theme = it.theme.copy(
                        appTheme = appTheme,
                        font = font,
                        style = style,
                        seedColor = seedColor,
                        withAmoled = withAmoled,
                    )
                )
            }
        }.launchIn(viewModelScope)

        otherPreferences.getMaterialYouFlow().distinctUntilChanged().onEach { enabled ->
            _state.update {
                it.copy(
                    theme = it.theme.copy(
                        materialTheme = enabled
                    )
                )
            }
        }.launchIn(viewModelScope)

        otherPreferences.getOnboardingDoneFlow().distinctUntilChanged().onEach { completed ->
            _state.update {
                it.copy(onBoardingDone = completed)
            }

            if (!completed) {
                _globalEvents.send(GlobalEvent.GoToOnboarding)
            }
        }.launchIn(viewModelScope)
    }
}