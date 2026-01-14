package com.shub39.rush.billing

import org.koin.core.annotation.Single

@Single(binds = [BillingHandler::class])
class BillingHandlerImpl: BillingHandler {
    override suspend fun isProUser(): Boolean = true
    override suspend fun userResult(): SubscriptionResult = SubscriptionResult.Subscribed
}