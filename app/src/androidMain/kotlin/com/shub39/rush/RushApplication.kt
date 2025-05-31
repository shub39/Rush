package com.shub39.rush

import android.app.Application
import com.shub39.rush.di.initKoin
import org.koin.android.ext.koin.androidContext

class RushApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // Check if androidMain process
        if (packageName == getProcessName()) {
            initKoin {
                androidContext(this@RushApplication)
            }
        }

    }

}