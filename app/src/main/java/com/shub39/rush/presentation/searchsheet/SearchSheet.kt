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
package com.shub39.rush.presentation.searchsheet

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.domain.SourceError
import com.shub39.rush.presentation.RushPreviewWrapper
import com.shub39.rush.presentation.detachedItemShape
import com.shub39.rush.presentation.endItemShape
import com.shub39.rush.presentation.errorStringRes
import com.shub39.rush.presentation.leadingItemShape
import com.shub39.rush.presentation.lyrics.component.ErrorCard
import com.shub39.rush.presentation.middleItemShape
import com.shub39.rush.presentation.searchsheet.component.SearchResultCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchSheet(
    state: SearchSheetState,
    onAction: (SearchSheetAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    AnimatedVisibility(
        visible = state.visible,
        enter =
            fadeIn(animationSpec = tween(300)) +
                slideInVertically(animationSpec = tween(300), initialOffsetY = { it }),
        exit =
            fadeOut(animationSpec = tween(300)) +
                slideOutVertically(animationSpec = tween(300), targetOffsetY = { it }),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Scrim
            Box(
                modifier =
                    Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.32f)).clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        onAction(SearchSheetAction.OnToggleSearchSheet)
                    }
            )

            // Sheet content
            Surface(
                modifier =
                    Modifier.align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .widthIn(max = 800.dp)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .imePadding(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                shadowElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier =
                            Modifier.padding(top = 12.dp)
                                .width(40.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                    )

                    OutlinedTextField(
                        value = state.searchQuery,
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.search),
                                contentDescription = "Search",
                                modifier = Modifier.padding(2.dp),
                            )
                        },
                        trailingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(2.dp),
                            ) {
                                AnimatedVisibility(
                                    visible = state.searchQuery.isNotBlank(),
                                    enter = fadeIn(animationSpec = tween(200)),
                                    exit = fadeOut(animationSpec = tween(200)),
                                ) {
                                    IconButton(
                                        onClick = {
                                            onAction(SearchSheetAction.OnQueryChange(""))
                                            coroutineScope.launch {
                                                focusRequester.requestFocus()
                                                keyboardController?.show()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.delete),
                                            contentDescription = "Delete",
                                        )
                                    }
                                }
                            }
                        },
                        onValueChange = { onAction(SearchSheetAction.OnQueryChange(it)) },
                        shape = MaterialTheme.shapes.extraLarge,
                        placeholder = { Text(stringResource(R.string.search)) },
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp)
                                .focusRequester(focusRequester),
                        keyboardOptions =
                            KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                    )

                    LazyColumn(
                        modifier =
                            Modifier.padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .heightIn(max = 480.dp)
                                .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 60.dp),
                    ) {
                        state.error?.let { error ->
                            item {
                                ErrorCard(
                                    error = error,
                                    debugMessage = null,
                                    colors =
                                        Pair(
                                            MaterialTheme.colorScheme.onSurface,
                                            MaterialTheme.colorScheme.background,
                                        ),
                                )
                            }
                        }

                        itemsIndexed(
                            items = state.localSearchResults,
                            key = { _, it -> "Saved_${it.id}" },
                        ) { index, it ->
                            val shape =
                                when {
                                    state.localSearchResults.size == 1 -> detachedItemShape()
                                    index == 0 -> leadingItemShape()
                                    index == state.localSearchResults.lastIndex -> endItemShape()
                                    else -> middleItemShape()
                                }

                            SearchResultCard(
                                result = it,
                                downloaded = true,
                                modifier =
                                    Modifier.clip(shape).clickable {
                                        onAction(SearchSheetAction.OnCardClicked(it.id))
                                        onNavigateToLyrics()
                                    },
                            )
                        }

                        if (state.localSearchResults.isNotEmpty()) {
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }

                        if (!state.isSearching) {
                            itemsIndexed(items = state.searchResults, key = { _, it -> it.id }) {
                                index,
                                it ->
                                val shape =
                                    when {
                                        state.searchResults.size == 1 -> detachedItemShape()
                                        index == 0 -> leadingItemShape()
                                        index == state.searchResults.lastIndex -> endItemShape()
                                        else -> middleItemShape()
                                    }

                                SearchResultCard(
                                    result = it,
                                    modifier =
                                        Modifier.clip(shape).clickable {
                                            onAction(SearchSheetAction.OnCardClicked(it.id))
                                            onNavigateToLyrics()
                                        },
                                )
                            }
                        } else {
                            item { LoadingIndicator(modifier = Modifier.size(60.dp)) }
                        }
                    }
                }
            }

            // Handle back: hide keyboard if visible, otherwise dismiss search
            val isImeVisible = WindowInsets.isImeVisible
            BackHandler(enabled = true) {
                if (isImeVisible) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                } else {
                    onAction(SearchSheetAction.OnToggleSearchSheet)
                }
            }

            // Auto-focus and show keyboard when sheet opens
            LaunchedEffect(Unit) {
                delay(400)
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        }
    }
}

@Composable
@PreviewWrapper(RushPreviewWrapper::class)
@Preview
private fun Preview() {
    var state by remember {
        mutableStateOf(
            SearchSheetState(visible = true, error = errorStringRes(SourceError.Data.PARSE_ERROR))
        )
    }

    SearchSheet(state = state, onAction = {}, onNavigateToLyrics = {})
}
