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
package com.shub39.rush.shared.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.window.core.layout.WindowSizeClass
import com.shub39.rush.shared.ui.WindowSize
import com.shub39.rush.shared.ui.WindowSize.Companion.isExpanded

/**
 * A [Scene] that displays two screens.
 * - In [WindowSize.EXPANDED], it uses a list-detail arrangement (40/60 split).
 * - In [WindowSize.COMPACT] or [WindowSize.MEDIUM], it uses a splitscreen from bottom (50/50
 *   vertical split).
 */
data class EditScreenScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val mainEntry: NavEntry<T>,
    val editEntry: NavEntry<T>,
    val isExpanded: Boolean,
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOf(mainEntry, editEntry)

    override val content: @Composable (() -> Unit) = {
        if (isExpanded) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.weight(0.6f)) { mainEntry.Content() }
                Column(modifier = Modifier.weight(0.4f)) { editEntry.Content() }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.weight(0.6f)) { mainEntry.Content() }
                Column(modifier = Modifier.weight(0.4f)) { editEntry.Content() }
            }
        }
    }

    companion object {
        /**
         * Helper function to add metadata to a [NavEntry] indicating it can be displayed in the
         * list pane of an [EditScreenScene].
         */
        fun editPane() = metadata { put(EditKey, true) }

        /**
         * Helper function to add metadata to a [NavEntry] indicating it can be displayed in the
         * detail pane of an [EditScreenScene].
         */
        fun mainPane() = metadata { put(MainKey, true) }
    }

    object EditKey : NavMetadataKey<Boolean>

    object MainKey : NavMetadataKey<Boolean>
}

/**
 * A [SceneStrategy] that returns an [EditScreenScene] if both list and detail entries are present.
 */
class EditScreenSceneStrategy<T : Any>(val windowSizeClass: WindowSizeClass) : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        // Find the top-most entry that declares itself as an edit pane.
        val editEntry =
            entries.lastOrNull()?.takeIf { it.metadata.contains(EditScreenScene.EditKey) }
                ?: return null

        // Find the last entry that declares itself as a main pane.
        val mainEntry =
            entries.findLast { it.metadata.contains(EditScreenScene.MainKey) } ?: return null

        // The scene key must uniquely represent the state of the scene.
        val sceneKey = Pair(editEntry.contentKey, mainEntry.contentKey)

        return EditScreenScene(
            key = sceneKey,
            // Where we go back to is a UX decision. In this case, we only remove the top
            // entry from the back stack, following the pattern of TwoPaneSceneStrategy.
            previousEntries = entries.dropLast(1),
            mainEntry = mainEntry,
            editEntry = editEntry,
            isExpanded = windowSizeClass.isExpanded(),
        )
    }
}
