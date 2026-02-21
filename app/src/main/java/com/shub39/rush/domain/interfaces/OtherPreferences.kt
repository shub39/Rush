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
package com.shub39.rush.domain.interfaces

import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.enums.Fonts
import com.shub39.rush.domain.enums.PaletteStyle
import com.shub39.rush.domain.enums.SortOrder
import kotlinx.coroutines.flow.Flow

interface OtherPreferences {
    suspend fun resetAppTheme()

    fun getAppThemePrefFlow(): Flow<AppTheme>

    suspend fun updateAppThemePref(pref: AppTheme)

    fun getSeedColorFlow(): Flow<Int>

    suspend fun updateSeedColor(newCardContent: Int)

    fun getAmoledPrefFlow(): Flow<Boolean>

    suspend fun updateAmoledPref(amoled: Boolean)

    fun getPaletteStyle(): Flow<PaletteStyle>

    suspend fun updatePaletteStyle(style: PaletteStyle)

    fun getSortOrderFlow(): Flow<SortOrder>

    suspend fun updateSortOrder(newSortOrder: SortOrder)

    fun getMaterialYouFlow(): Flow<Boolean>

    suspend fun updateMaterialTheme(pref: Boolean)

    fun getFontFlow(): Flow<Fonts>

    suspend fun updateFonts(font: Fonts)

    fun getOnboardingDoneFlow(): Flow<Boolean>

    suspend fun updateOnboardingDone(done: Boolean)
}
