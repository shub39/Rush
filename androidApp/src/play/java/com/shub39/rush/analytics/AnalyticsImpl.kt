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
package com.shub39.rush.analytics

import android.content.Context
import com.posthog.PostHog
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig
import com.revenuecat.purchases.Purchases
import com.shub39.rush.BuildConfig
import com.shub39.rush.shared.core.RushLogger
import com.shub39.rush.shared.core.interfaces.AnalyticsWrapper
import kotlin.time.Clock

class AnalyticsImpl : AnalyticsWrapper {
    companion object {
        private const val TAG = "AnalyticsImpl"
    }

    private val config =
        PostHogAndroidConfig(apiKey = BuildConfig.POSTHOG_API_KEY, host = BuildConfig.POSTHOG_HOST)

    private fun getDefaultProperties() =
        mapOf(
            "app_name" to "Rush",
            "app_version" to BuildConfig.VERSION_NAME,
            "time_stamp" to Clock.System.now().toEpochMilliseconds() * 1000,
        )

    override fun setup(context: Any?) {
        if (context is Context) {
            PostHogAndroid.setup(context, config)

            val rcId = Purchases.sharedInstance.appUserID
            PostHog.identify("rush:$rcId")
        } else {
            RushLogger.e(TAG, "Invalid Context Provided")
        }
    }

    override fun trackEvent(event: String, properties: Map<String, Any>) {
        PostHog.capture(event = event, properties = getDefaultProperties() + properties)
    }
}
