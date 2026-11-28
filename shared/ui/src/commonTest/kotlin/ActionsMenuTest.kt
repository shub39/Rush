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

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.shub39.rush.shared.core.enums.Sources
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import kotlin.test.Test
import kotlin.test.assertEquals
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import rush.shared.ui.generated.resources.*

@OptIn(ExperimentalTestApi::class)
class ActionsMenuTest {
    private val actions = mutableListOf<LyricsPageAction>()
    private var editClicks = 0

    private fun state(
        source: Sources = Sources.LRCLIB,
        fitToScreen: Boolean = true,
        sync: Boolean = false,
        selectedLines: Map<Int, String> = emptyMap(),
        rushEnabled: Boolean = false,
        syncedAvailable: Boolean = true,
    ) =
        ActionsState(
            song = null,
            selectedLines = selectedLines,
            source = source,
            syncedAvailable = syncedAvailable,
            sync = sync,
            rushEnabled = rushEnabled,
            fitToScreen = fitToScreen,
        )

    private fun ComposeUiTest.setActions(
        state: ActionsState = state(),
        allowCompact: Boolean = true,
        notificationAccess: Boolean = true,
        isCustomisationsOpened: Boolean = false,
    ) {
        setContent {
            MaterialTheme {
                Row {
                    Actions(
                        state = state,
                        action = { actions += it },
                        notificationAccess = notificationAccess,
                        cardBackground = Color.Black,
                        cardContent = Color.White,
                        onShare = {},
                        onEdit = { editClicks++ },
                        isCustomisationsOpened = isCustomisationsOpened,
                        allowCompact = allowCompact,
                    )
                }
            }
        }
    }

    private suspend fun ComposeUiTest.openMenu() {
        onNodeWithContentDescription(getString(Res.string.more_options)).performClick()
        waitForIdle()
    }

    private suspend fun ComposeUiTest.assertMenuItems(vararg expected: StringResource) {
        val all =
            listOf(
                Res.string.customisations,
                Res.string.copy,
                Res.string.genius,
                Res.string.lrclib,
                Res.string.correct_lyrics,
                Res.string.show_synced_lyrics,
                Res.string.rush_mode,
            )
        for (item in all) {
            onAllNodesWithText(getString(item)).assertCountEquals(if (item in expected) 1 else 0)
        }
    }

    private suspend fun ComposeUiTest.assertMenuClosed() {
        onAllNodesWithText(getString(Res.string.customisations)).assertCountEquals(0)
    }

    private fun ComposeUiTest.assertDisplayed(vararg descriptions: String) {
        descriptions.forEach { onNodeWithContentDescription(it).assertIsDisplayed() }
    }

    private fun ComposeUiTest.assertDoesNotExist(vararg descriptions: String) {
        descriptions.forEach { onNodeWithContentDescription(it).assertDoesNotExist() }
    }

    private val normalIcons =
        arrayOf("Open Palette", "Copy", "Genius", "Correct Lyrics", "Rush Mode")

    @Test
    fun compactShowsOnlyFitAndMore() = runComposeUiTest {
        setActions()

        assertDisplayed(getString(Res.string.fit_to_screen), getString(Res.string.more_options))
        assertDoesNotExist(*normalIcons, "Synced Lyrics")
    }

    @Test
    fun notCompactWhenCompactIsNotAllowed() = runComposeUiTest {
        setActions(allowCompact = false)

        assertDisplayed(*normalIcons, "Synced Lyrics", getString(Res.string.fit_to_screen))
        assertDoesNotExist(getString(Res.string.more_options))
    }

    @Test
    fun notCompactWhenFitIsOff() = runComposeUiTest {
        setActions(state(fitToScreen = false))

        assertDisplayed(*normalIcons, "Synced Lyrics", getString(Res.string.fit_to_screen))
        assertDoesNotExist(getString(Res.string.more_options))
    }

    @Test
    fun notCompactWithSelectedLines() = runComposeUiTest {
        setActions(state(selectedLines = mapOf(0 to "line")), notificationAccess = false)

        assertDisplayed("Open Palette", "Copy", "Share", getString(Res.string.clear_selection))
        assertDoesNotExist(getString(Res.string.more_options), getString(Res.string.fit_to_screen))

        onNodeWithContentDescription(getString(Res.string.clear_selection)).performClick()
        assertEquals(
            listOf<LyricsPageAction>(LyricsPageAction.OnChangeSelectedLines(emptyMap())),
            actions,
        )
    }

    @Test
    fun fitToggleWorksInCompactMode() = runComposeUiTest {
        setActions()
        assertDisplayed(getString(Res.string.more_options))

        onNodeWithContentDescription(getString(Res.string.fit_to_screen)).performClick()

        assertEquals(listOf<LyricsPageAction>(LyricsPageAction.OnFitToScreenToggle(false)), actions)
    }

    @Test
    fun menuShowsAllItemsForLrcLibWithNotificationAccess() = runComposeUiTest {
        setActions(state(rushEnabled = true))
        openMenu()

        assertMenuItems(
            Res.string.customisations,
            Res.string.copy,
            Res.string.genius,
            Res.string.correct_lyrics,
            Res.string.show_synced_lyrics,
            Res.string.rush_mode,
        )
        onNodeWithText(getString(Res.string.rush_mode))
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ToggleableState,
                    ToggleableState.On,
                )
            )
    }

    @Test
    fun menuHidesLrcLibOnlyItemsForGenius() = runComposeUiTest {
        setActions(state(source = Sources.GENIUS))
        openMenu()

        assertMenuItems(
            Res.string.customisations,
            Res.string.copy,
            Res.string.lrclib,
            Res.string.rush_mode,
        )
        onNodeWithText(getString(Res.string.rush_mode))
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ToggleableState,
                    ToggleableState.Off,
                )
            )
    }

    @Test
    fun menuHidesSyncedItemWhenSyncedLyricsUnavailable() = runComposeUiTest {
        setActions(state(syncedAvailable = false), notificationAccess = true)
        openMenu()

        assertMenuItems(
            Res.string.customisations,
            Res.string.copy,
            Res.string.genius,
            Res.string.correct_lyrics,
            Res.string.rush_mode,
        )
    }

    @Test
    fun menuHidesNotificationItemsWithoutAccess() = runComposeUiTest {
        setActions(notificationAccess = false)
        openMenu()

        assertMenuItems(
            Res.string.customisations,
            Res.string.copy,
            Res.string.genius,
            Res.string.correct_lyrics,
        )
    }

    @Test
    fun customisationsCallsOnEditAndClosesMenu() = runComposeUiTest {
        setActions()
        openMenu()

        onNodeWithText(getString(Res.string.customisations)).assertIsEnabled().performClick()
        waitForIdle()

        assertEquals(1, editClicks)
        assertEquals(emptyList(), actions)
        assertMenuClosed()
    }

    @Test
    fun customisationsDisabledWhileOpened() = runComposeUiTest {
        setActions(isCustomisationsOpened = true)
        openMenu()

        onNodeWithText(getString(Res.string.customisations)).assertIsNotEnabled()
    }

    @Test
    fun copyClosesMenuWithoutActions() = runComposeUiTest {
        // Only checks that the menu closes without dispatching actions; with song = null the
        // clipboard is not exercised.
        setActions()
        openMenu()

        onNodeWithText(getString(Res.string.copy)).performClick()
        waitForIdle()

        assertEquals(emptyList(), actions)
        assertMenuClosed()
    }

    @Test
    fun sourceSwitchDispatchesAndClosesMenu() = runComposeUiTest {
        setActions()
        openMenu()

        onNodeWithText(getString(Res.string.genius)).performClick()
        waitForIdle()

        assertEquals(
            listOf(LyricsPageAction.OnSourceChange(Sources.GENIUS), LyricsPageAction.OnSync(false)),
            actions,
        )
        assertMenuClosed()
    }

    @Test
    fun correctLyricsDispatchesAndClosesMenu() = runComposeUiTest {
        setActions(state(rushEnabled = true))
        openMenu()

        onNodeWithText(getString(Res.string.correct_lyrics)).performClick()
        waitForIdle()

        assertEquals(
            listOf(
                LyricsPageAction.OnLyricsCorrect(true),
                LyricsPageAction.OnSync(false),
                LyricsPageAction.OnDisableRushMode,
            ),
            actions,
        )
        assertMenuClosed()
    }

    @Test
    fun syncedLyricsDispatchesAndClosesMenu() = runComposeUiTest {
        setActions()
        openMenu()

        onNodeWithText(getString(Res.string.show_synced_lyrics)).performClick()
        waitForIdle()

        assertEquals(listOf<LyricsPageAction>(LyricsPageAction.OnSync(true)), actions)
        assertMenuClosed()
    }

    @Test
    fun rushModeDispatchesAndClosesMenu() = runComposeUiTest {
        setActions()
        openMenu()

        onNodeWithText(getString(Res.string.rush_mode)).performClick()
        waitForIdle()

        assertEquals(listOf<LyricsPageAction>(LyricsPageAction.OnToggleRushMode), actions)
        assertMenuClosed()
    }
}
