package com.shub39.rush.billing

import android.content.Context
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.shub39.rush.BuildConfig

class BillingInitializerImpl: BillingInitializer {
    override fun initialize(context: Context) {
        Purchases.logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.WARN
        Purchases.configure(
            PurchasesConfiguration.Builder(
                context,
                PURCHASES_KEY
            ).build()
        )
    }

    companion object {
        private const val PURCHASES_KEY = "goog_UWGwVYYNwfsymFOSGcheNqGUIGm"
    }
}