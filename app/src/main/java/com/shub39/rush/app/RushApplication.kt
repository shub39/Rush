package com.shub39.rush.app

import android.app.Application
import com.shub39.rush.BuildConfig
import com.shub39.rush.billing.BillingInitializerImpl
import com.shub39.rush.data.listener.MediaListenerImpl
import com.shub39.rush.di.RushModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class RushApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) androidLogger()
            androidContext(this@RushApplication)
            modules(RushModules().module)
        }

        BillingInitializerImpl().initialize(this)
        MediaListenerImpl.startListening(this)
    }

}