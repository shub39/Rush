package com.shub39.rush.ui.page.component.searchsheet

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSheet(
    state: SearchSheetState,
    action: (SearchSheetAction) -> Unit,
    onClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = {
            query = ""
            action(SearchSheetAction.OnToggleSearchSheet)
        }
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }
        var searchJob: Job? by remember { mutableStateOf(null) }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = query,
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.round_search_24),
                        contentDescription = null,
                        modifier = Modifier.padding(2.dp)
                    )
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(2.dp)
                    ) {
                        AnimatedVisibility(
                            visible = state.isSearching,
                            enter = fadeIn(animationSpec = tween(200)),
                            exit = fadeOut(animationSpec = tween(200))
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeCap = StrokeCap.Round
                            )
                        }

                        AnimatedVisibility(
                            visible = query.isNotBlank(),
                            enter = fadeIn(animationSpec = tween(200)),
                            exit = fadeOut(animationSpec = tween(200))
                        ) {
                            IconButton(
                                onClick = {
                                    query = ""
                                    coroutineScope.launch {
                                        focusRequester.requestFocus()
                                        keyboardController?.show()
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_delete_forever_24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                },
                onValueChange = {
                    query = it
                    searchJob?.cancel()

                    searchJob = coroutineScope.launch {
                        delay(500)
                        if (it.isNotEmpty()) {
                            action(SearchSheetAction.OnSearch(it))
                        }
                    }
                },
                shape = MaterialTheme.shapes.extraLarge,
                label = { Text(stringResource(id = R.string.search)) },
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
                horizontalAlignment = Alignment.CenterHorizontally,
                state = LazyListState(0)
            ) {
                items(
                    state.localSearchResults,
                    key = { it.title.hashCode() + it.artist.hashCode() }) {
                    SearchResultCard(
                        result = it,
                        onClick = {
                            query = ""
                            action(SearchSheetAction.OnCardClicked(it.id))
                            onClick()
                        },
                        downloaded = true
                    )
                }

                items(state.searchResults, key = { it.id }) {
                    SearchResultCard(
                        result = it,
                        onClick = {
                            query = ""
                            action(SearchSheetAction.OnCardClicked(it.id))
                            onClick()
                        },
                    )
                }

                item {
                    Spacer(modifier = Modifier.padding(60.dp))
                }
            }
        }
    }
}