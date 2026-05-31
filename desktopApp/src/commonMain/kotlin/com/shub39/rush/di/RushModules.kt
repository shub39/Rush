package com.shub39.rush.di

import com.shub39.rush.shared.core.interfaces.BillingHandler
import com.shub39.rush.shared.core.interfaces.SubscriptionResult
import com.shub39.rush.shared.logic.di.DataModule
import com.shub39.rush.shared.ui.di.UIModule
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module(includes = [DataModule::class, UIModule::class])
class RushModules {
    @Single
    fun provideBillingHandler(): BillingHandler = object : BillingHandler {
        override suspend fun isProUser(): Boolean = true

        override suspend fun userResult(): SubscriptionResult = SubscriptionResult.Subscribed
    }
}