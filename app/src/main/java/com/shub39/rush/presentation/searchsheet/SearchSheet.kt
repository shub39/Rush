package com.shub39.rush.presentation.searchsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.domain.SourceError
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.presentation.RushTheme
import com.shub39.rush.presentation.errorStringRes
import com.shub39.rush.presentation.lyrics.component.ErrorCard
import com.shub39.rush.presentation.searchsheet.component.SearchResultCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchSheet(
    state: SearchSheetState,
    onAction: (SearchSheetAction) -> Unit,
    onNavigateToLyrics: () -> Unit,
    sheetState: SheetState
) {
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    if (state.visible) {
        ModalBottomSheet(
            sheetState = sheetState,
            modifier = Modifier.widthIn(max = 800.dp),
            onDismissRequest = {
                onAction(SearchSheetAction.OnToggleSearchSheet)
            }
        ) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                keyboardController?.show()
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.padding(2.dp)
                        )
                    },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            AnimatedVisibility(
                                visible = state.searchQuery.isNotBlank(),
                                enter = fadeIn(animationSpec = tween(200)),
                                exit = fadeOut(animationSpec = tween(200))
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
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete"
                                    )
                                }
                            }
                        }
                    },
                    onValueChange = { onAction(SearchSheetAction.OnQueryChange(it)) },
                    shape = MaterialTheme.shapes.extraLarge,
                    placeholder = { Text(stringResource(R.string.search)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                        }
                    )
                )


                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    state.error?.let { error ->
                        item {
                            ErrorCard(
                                error = error,
                                debugMessage = null,
                                colors = Pair(
                                    MaterialTheme.colorScheme.onSurface,
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        }
                    }

                    items(
                        state.localSearchResults,
                        key = { "Saved_${it.id}" }
                    ) {
                        SearchResultCard(
                            result = it,
                            onClick = {
                                onAction(SearchSheetAction.OnCardClicked(it.id))
                                onNavigateToLyrics()
                            },
                            downloaded = true
                        )
                    }

                    if (!state.isSearching) {
                        items(state.searchResults, key = { it.id }) {
                            SearchResultCard(
                                result = it,
                                onClick = {
                                    onAction(SearchSheetAction.OnCardClicked(it.id))
                                    onNavigateToLyrics()
                                }
                            )
                        }
                    } else {
                        item {
                            LoadingIndicator(
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.padding(60.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun Preview() {
    var state by remember {
        mutableStateOf(
            SearchSheetState(
                visible = true,
                error = errorStringRes(SourceError.Data.PARSE_ERROR)
            )
        )
    }

    RushTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        SearchSheet(
            state = state,
            onAction = {},
            onNavigateToLyrics = {},
            sheetState = rememberStandardBottomSheetState()
        )
    }
}