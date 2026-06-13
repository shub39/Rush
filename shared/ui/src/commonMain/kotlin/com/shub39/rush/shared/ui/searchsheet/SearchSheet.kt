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
package com.shub39.rush.shared.ui.searchsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.SourceError
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.component.RushBottomSheet
import com.shub39.rush.shared.ui.detachedItemShape
import com.shub39.rush.shared.ui.endItemShape
import com.shub39.rush.shared.ui.errorStringRes
import com.shub39.rush.shared.ui.leadingItemShape
import com.shub39.rush.shared.ui.lyrics.component.ErrorCard
import com.shub39.rush.shared.ui.middleItemShape
import com.shub39.rush.shared.ui.searchsheet.component.SearchResultCard
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchSheet(
    state: SearchSheetState,
    onAction: (SearchSheetAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    RushBottomSheet(
        modifier = modifier.imePadding(),
        padding = 0.dp,
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        SearchSheetBackHandler {
            focusManager.clearFocus()
            keyboardController?.hide()
        }

        LaunchedEffect(Unit) {
            delay(400.milliseconds)
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.search),
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
                                    painter = painterResource(Res.drawable.delete),
                                    contentDescription = "Delete",
                                )
                            }
                        }
                    }
                },
                onValueChange = { onAction(SearchSheetAction.OnQueryChange(it)) },
                shape = MaterialTheme.shapes.extraLarge,
                placeholder = { Text(stringResource(Res.string.search)) },
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
            )

            LazyColumn(
                modifier =
                    Modifier.padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .heightIn(max = 700.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp),
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
                    itemsIndexed(items = state.searchResults, key = { _, it -> it.id }) { index, it
                        ->
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

    SearchSheet(state = state, onAction = {}, onNavigateToLyrics = {}, onDismissRequest = {})
}
