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
package com.shub39.rush.shared.core.interfaces

import com.shub39.rush.shared.core.RushLogger

interface AnalyticsWrapper {
    fun setup(context: Any?)

    fun trackEvent(event: String, properties: Map<String, Any>)

    companion object {
        @JvmInline
        value class AnalyticsEvent(val name: String) {
            companion object {
                val APP_OPENED = AnalyticsEvent("app_opened")
                val ONBOARDING_COMPLETED = AnalyticsEvent("onboarding_completed")
                val NOTIFICATION_ACCESS_GRANTED = AnalyticsEvent("notification_access_granted")
                val PAYWALL_OPENED = AnalyticsEvent("paywall_opened")
                val PAYWALL_PURCHASED = AnalyticsEvent("paywall_purchased")
                val SAVED_SORT_ORDER_CHANGED = AnalyticsEvent("saved_sort_order_changed")
                val RUSH_MODE_TOGGLED = AnalyticsEvent("rush_mode_toggled")
                val SEARCH_OPENED = AnalyticsEvent("search_opened")
                val SEARCH_PERFORMED = AnalyticsEvent("search_performed")
                val LYRICS_OPENED = AnalyticsEvent("lyrics_opened")
                val LYRICS_CORRECTED = AnalyticsEvent("lyrics_corrected")
                val SETTINGS_OPENED = AnalyticsEvent("settings_opened")
                val SHARE_OPENED = AnalyticsEvent("share_opened")
                val CARD_SHARED = AnalyticsEvent("card_shared")
                val BACKUP_CREATED = AnalyticsEvent("backup_created")
                val BACKUP_RESTORED = AnalyticsEvent("backup_restored")
                val ALL_SONG_DELETED = AnalyticsEvent("all_songs_deleted")
                val ABOUT_OPENED = AnalyticsEvent("about_opened")
                val ABOUT_LINK_CLICKED = AnalyticsEvent("about_link_clicked")
                val CHANGELOG_OPENED = AnalyticsEvent("changelog_opened")
                val APP_THEME_CHANGED = AnalyticsEvent("app_theme_changed")
                val LYRICS_THEME_CHANGED = AnalyticsEvent("lyrics_theme_changed")
            }
        }

        class DummyAnalyticsWrapper : AnalyticsWrapper {
            companion object {
                private const val TAG = "DummyAnalyticsWrapper"
            }

            override fun setup(context: Any?) {
                RushLogger.i(TAG, "Setup Analytics")
            }

            override fun trackEvent(event: String, properties: Map<String, Any>) {
                RushLogger.i(TAG, "Track Event: $event\nProperties: $properties")
            }
        }
    }
}
