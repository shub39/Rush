package com.shub39.rush.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.app.GlobalAction
import com.shub39.rush.app.GlobalState
import com.shub39.rush.billing.BillingHandler
import com.shub39.rush.billing.SubscriptionResult
import com.shub39.rush.data.listener.NotificationListener
import com.shub39.rush.domain.interfaces.OtherPreferences
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
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class GlobalVM(
    private val billingHandler: BillingHandler,
    private val otherPreferences: OtherPreferences
) : ViewModel() {
    private var syncJob: Job? = null

    private val _state = MutableStateFlow(GlobalState())
    val state = _state.asStateFlow()
        .onStart {
            checkSubscription()
            startSync()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    fun onAction(action: GlobalAction) {
        when (action) {
            GlobalAction.OnTogglePaywall -> _state.update { it.copy(showPaywall = !_state.value.showPaywall) }
            is GlobalAction.OnUpdateOnboardingDone -> viewModelScope.launch {
                otherPreferences.updateOnboardingDone(action.status)
            }
            is GlobalAction.OnCheckNotificationAccess -> {
                _state.update {
                    it.copy(
                        notificationAccess = NotificationListener.canAccessNotifications(action.context)
                    )
                }
            }
        }
    }

    private suspend fun checkSubscription() {
        val isSubscribed = billingHandler.userResult()

        when (isSubscribed) {
            SubscriptionResult.Subscribed -> {
                _state.update { it.copy(isProUser = true) }
            }

            else -> {}
        }
    }

    private fun startSync() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            combine(
                otherPreferences.getFontFlow(),
                otherPreferences.getPaletteStyle(),
                otherPreferences.getSeedColorFlow(),
                otherPreferences.getAmoledPrefFlow(),
                otherPreferences.getAppThemePrefFlow(),
            ) { font, style, seedColor, withAmoled, theme ->
                _state.update {
                    it.copy(
                        theme = it.theme.copy(
                            appTheme = theme,
                            font = font,
                            style = style,
                            seedColor = seedColor,
                            withAmoled = withAmoled,
                        )
                    )
                }
            }.launchIn(this)

            otherPreferences
                .getSeedColorFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(theme = it.theme.copy(seedColor = pref))
                    }
                }
                .launchIn(this)

            otherPreferences
                .getOnboardingDoneFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(onBoardingDone = pref)
                    }
                }
                .launchIn(this)
        }
    }
}