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
package com.shub39.rush.shared.logic

import android.content.Context
import com.shub39.romanization.RomanizationUtils
import com.shub39.rush.shared.core.interfaces.RomanizationProvider
import org.koin.core.annotation.Single

@Single(binds = [RomanizationProvider::class])
actual class RomanizationProviderImpl(private val context: Context) : RomanizationProvider {
    private val utils = RomanizationUtils(context)

    override suspend fun romanize(text: String): String? = utils.romanize(text)
}
