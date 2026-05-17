/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.app

import android.content.Context
import android.util.Log
import com.shub39.romanization.RomanizationUtils
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Loads the IPADIC reading dictionary and builds a longest-match tokenizer index.
 *
 * The reading dictionary (ja_readings.tsv, compiled from MeCab IPADIC) maps surface forms to
 * katakana readings. It is used by [RomanizationUtils] for both tokenization (longest-match
 * segmentation) and kanji reading lookup.
 *
 * Usage: JapaneseReadingProvider.initialize(context) // call once on background thread
 */
object JapaneseReadingProvider {

    private const val TAG = "JapaneseReadingProvider"

    @Volatile private var isInitialized = false
    private val initMutex = Mutex()

    /**
     * Load the reading dictionary and build the tokenizer index. Can be called multiple times
     * safely — subsequent calls are no-ops. Must be called from a background coroutine.
     *
     * @param context Application context for accessing assets
     * @return true if the reading dictionary was loaded successfully
     */
    suspend fun initialize(context: Context): Boolean {
        if (isInitialized) return true

        return initMutex.withLock {
            if (isInitialized) return@withLock true

            try {
                Log.i(TAG, "Loading IPADIC reading dictionary...")
                RomanizationUtils.loadReadingDictionary(context)
                isInitialized = true
                Log.i(TAG, "Reading dictionary loaded, tokenizer index built")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load reading dictionary", e)
                false
            }
        }
    }

    // Reload the dictionary (e.g. after an update). Must be called from a coroutine.
    suspend fun reload(context: Context) {
        initMutex.withLock {
            isInitialized = false
            RomanizationUtils.resetReadingDictionary()
        }
        initialize(context)
    }

    /** The reading dictionary is bundled in the APK, always available. */
    fun isDictionaryDownloaded(context: Context): Boolean = true
}
