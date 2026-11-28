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
package com.shub39.rush.shared.ui.lyrics.component

import com.shub39.rush.shared.core.enums.Sources
import kotlin.test.Test
import kotlin.test.assertEquals

class ActionsStateTest {
    private fun state(
        fitToScreen: Boolean = true,
        sync: Boolean = false,
        selectedLines: Map<Int, String> = emptyMap(),
    ) =
        ActionsState(
            song = null,
            selectedLines = selectedLines,
            source = Sources.LRCLIB,
            syncedAvailable = true,
            sync = sync,
            rushEnabled = false,
            fitToScreen = fitToScreen,
        )

    @Test
    fun canCompactMatchesEveryCombination() {
        for (fit in listOf(true, false)) {
            for (sync in listOf(true, false)) {
                for (lines in listOf(emptyMap(), mapOf(0 to "line"))) {
                    val expected = fit && !sync && lines.isEmpty()
                    assertEquals(
                        expected,
                        state(fit, sync, lines).canCompact,
                        "fit=$fit sync=$sync lines=$lines",
                    )
                }
            }
        }
    }
}
