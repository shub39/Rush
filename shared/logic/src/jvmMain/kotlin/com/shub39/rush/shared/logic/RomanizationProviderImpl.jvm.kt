package com.shub39.rush.shared.logic

import com.shub39.rush.shared.core.interfaces.RomanizationProvider
import org.koin.core.annotation.Single

// stub
@Single(binds = [RomanizationProvider::class])
actual class RomanizationProviderImpl : RomanizationProvider {
    override suspend fun romanize(text: String): String? = null
}