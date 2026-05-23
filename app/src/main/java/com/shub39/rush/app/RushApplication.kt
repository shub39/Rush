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
package com.shub39.rush.app

import android.app.Application
import com.shub39.romanization.RomanizationUtils
import com.shub39.rush.BuildConfig
import com.shub39.rush.billing.BillingInitializerImpl
import com.shub39.rush.data.listener.MediaListenerImpl
import com.shub39.rush.di.RushModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.plugin.module.dsl.startKoin

class RushApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin<RushModules> {
            if (BuildConfig.DEBUG) androidLogger()
            androidContext(this@RushApplication)
        }

        BillingInitializerImpl().initialize(this)
        MediaListenerImpl.startListening(this)

        // Initialize RomanizationUtils with app context for lazy dictionary loading
        RomanizationUtils.init(this)
    }
}
