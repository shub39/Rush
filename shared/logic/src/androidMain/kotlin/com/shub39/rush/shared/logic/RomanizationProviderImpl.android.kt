package com.shub39.rush.shared.logic

import android.content.Context
import com.shub39.romanization.RomanizationUtils
import com.shub39.rush.shared.core.interfaces.RomanizationProvider
import org.koin.core.annotation.Single

@Single(binds = [RomanizationProvider::class])
actual class RomanizationProviderImpl(private val context: Context): RomanizationProvider {
    private val utils = RomanizationUtils(context)

    override suspend fun romanize(text: String): String? = utils.romanize(text)
}