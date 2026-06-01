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
package com.shub39.rush.shared.ui.app

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed interface Routes : NavKey {
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
