package com.shub39.rush

import android.app.Application
import com.shub39.rush.billing.BillingInitializerImpl
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
    }
}