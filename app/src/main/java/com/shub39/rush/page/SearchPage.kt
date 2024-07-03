package com.shub39.rush.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.shub39.rush.R
import com.shub39.rush.component.Empty
import com.shub39.rush.component.SearchResultCard
import com.shub39.rush.viewmodel.RushViewModel

@Composable
fun SearchPage(
    rushViewModel: RushViewModel,
    imageLoader: ImageLoader,
    onClick: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val searchResults by rushViewModel.searchResults.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val isFetchingLyrics by rushViewModel.isSearchingLyrics.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(
            value = query,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.round_search_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (isFetchingLyrics) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeCap = StrokeCap.Round
                    )
                }
            },
            onValueChange = {
                query = it
                if (query.isNotBlank()) {
                    rushViewModel.searchSong(it)
                }
            },
            shape = MaterialTheme.shapes.large,
            label = { Text(stringResource(id = R.string.search)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 32.dp, end = 32.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = {
                    keyboardController?.hide()
                }
            )
        )

        if (searchResults.isEmpty()) {
            Empty()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (searchResults.isEmpty() && query.isNotBlank()) {
                    item {
                        Spacer(modifier = Modifier.padding(16.dp))
                        CircularProgressIndicator()
                    }
                } else {
                    items(searchResults, key = { it.id }) {
                        SearchResultCard(
                            result = it,
                            onClick = {
                                rushViewModel.changeCurrentSong(it.id)
                                onClick()
                            },
                            imageLoader = imageLoader
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.padding(60.dp))
                    }
                }
            }
        }

    }

}