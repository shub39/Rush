package com.shub39.rush.billing

import com.revenuecat.purchases.CacheFetchPolicy
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitCustomerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single(binds = [BillingHandler::class])
class BillingHandlerImpl: BillingHandler {
    private val purchases by lazy { Purchases.sharedInstance }

    override suspend fun isProUser(): Boolean {
        return userResult() is SubscriptionResult.Subscribed
    }

    override suspend fun userResult(): SubscriptionResult {
        try {
            val userInfo = withContext(Dispatchers.IO) {
                purchases.awaitCustomerInfo(fetchPolicy = CacheFetchPolicy.NOT_STALE_CACHED_OR_CURRENT)
            }
            val entitlement = userInfo.entitlements.all[ENTITLEMENT_PRO]
            val isPlus = entitlement?.isActive
            if (isPlus == true) {
                return SubscriptionResult.Subscribed
            }
        } catch (e: Exception) {
            return SubscriptionResult.Error(e)
        }

        return SubscriptionResult.NotSubscribed
    }

    companion object {
        private const val ENTITLEMENT_PRO = "pro"
    }
}