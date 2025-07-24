package com.shub39.rush.billing

interface BillingHandler {
    suspend fun isProUser(): Boolean
    suspend fun userResult(): SubscriptionResult
}