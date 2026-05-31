package com.shub39.rush.shared.ui.app

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed interface Routes: NavKey {
    @Serializable data object SavedPage : Routes

    @Serializable data object LyricsGraph : Routes

    @Serializable data object SettingsGraph : Routes

    @Serializable data object SharePage : Routes

    @Serializable data object OnboardingPage : Routes

    @Serializable data object PaywallPage : Routes

    companion object {
        val configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(SavedPage::class, SavedPage.serializer())
                    subclass(LyricsGraph::class, LyricsGraph.serializer())
                    subclass(SettingsGraph::class, SettingsGraph.serializer())
                    subclass(SharePage::class, SharePage.serializer())
                    subclass(OnboardingPage::class, OnboardingPage.serializer())
                    subclass(PaywallPage::class, PaywallPage.serializer())
                }
            }
        }
    }
}