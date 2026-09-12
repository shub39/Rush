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
package com.shub39.rush.shared.ui.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey

sealed interface Routes : NavKey {
    sealed interface Lyrics : Routes {
        data object LyricsRoot : Lyrics

        data object LyricsCustomisations : Lyrics
    }

    sealed interface Share : Routes {
        data object ShareRoot : Share

        data object ShareEdit : Share
    }

    data object Search : Routes

    data object Saved : Routes

    data object Changelog : Routes

    data object Onboarding : Routes

    data object Paywall : Routes

    sealed interface Settings : Routes {
        data object SettingsRoot : Settings

        data object SettingsLookAndFeel : Settings

        data object SettingsBackup : Settings

        data object SettingsChangelog : Settings

        data object SettingsAppInfo : Settings
    }
}

class TopLevelBackStack(startKey: Routes = Routes.Saved) {

    val backStack = mutableStateListOf(startKey)

    private fun Routes.getSubtype() =
        when (this) {
            is Routes.Lyrics -> Routes.Lyrics::class
            is Routes.Settings -> Routes.Settings::class
            Routes.Search -> Routes.Search::class
            Routes.Saved -> Routes.Saved::class
            Routes.Changelog -> Routes.Changelog::class
            Routes.Onboarding -> Routes.Onboarding::class
            Routes.Paywall -> Routes.Paywall::class
            Routes.Share.ShareRoot -> Routes.Share.ShareRoot::class
            Routes.Share.ShareEdit -> Routes.Share.ShareEdit::class
        }

    fun isRouteOnTop(route: Routes): Boolean = backStack.lastOrNull() == route

    fun add(route: Routes) {
        if (route == Routes.Saved) return

        if (route is Routes.Settings) {
            // Remove all existing settings routes
            val settingsRoutes = backStack.filterIsInstance<Routes.Settings>()
            backStack.removeAll(settingsRoutes)

            if (route != Routes.Settings.SettingsRoot) {
                // If adding a sub-page, ensure Root is always the predecessor
                backStack.add(Routes.Settings.SettingsRoot)
            }
            backStack.add(route)
        } else {
            // Remove if already exists to move it to the top
            backStack.remove(route)

            // Enforce max 2 unique routes per subtype
            val subtype = route.getSubtype()
            val existingOfSubtype = backStack.filter { it.getSubtype() == subtype }
            if (existingOfSubtype.size >= 2) {
                backStack.remove(existingOfSubtype.first())
            }

            backStack.add(route)
        }
    }

    fun addTopLevel(route: Routes) = add(route)

    fun removeLast() {
        if (backStack.size > 1) {
            backStack.removeLast()
        }
    }
}
