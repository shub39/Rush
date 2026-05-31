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

import com.shub39.rush.shared.core.RushLogger
import com.shub39.rush.shared.core.dataclasses.Changelog
import com.shub39.rush.shared.core.interfaces.ChangelogManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import rush.shared.logic.generated.resources.Res

@Single(binds = [ChangelogManager::class])
class ChangelogManagerImpl : ChangelogManager {
    private val _changelogs: MutableStateFlow<Changelog> = MutableStateFlow(emptyList())
    override val changelogs = _changelogs.asStateFlow().onStart { getChangelogs() }

    private suspend fun getChangelogs() {
        try {
            val rawJson = Res.readBytes("files/changelog.json").decodeToString()

            val json = Json.decodeFromString<Changelog>(rawJson)

            _changelogs.update { json }
        } catch (e: Exception) {
            RushLogger.e("ChangelogManager", "Error reading changelog", e)
        }
    }
}
