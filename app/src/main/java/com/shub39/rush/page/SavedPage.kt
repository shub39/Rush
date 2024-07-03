package com.shub39.rush.page

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.shub39.rush.R
import com.shub39.rush.component.Empty
import com.shub39.rush.component.SongCard
import com.shub39.rush.viewmodel.RushViewModel

@Composable
fun SavedPage(
    rushViewModel: RushViewModel,
    imageLoader: ImageLoader,
    bottomSheet: () -> Unit,
    onClick: () -> Unit
) {
    val songs = rushViewModel.songs.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (songs.value.isEmpty()) {
            Empty()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            ) {
                items(songs.value, key = { it.id }) {
                    SongCard(
                        result = it,
                        onDelete = {
                            rushViewModel.deleteSong(it)
                        },
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

        FloatingActionButton(
            onClick = { bottomSheet() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 32.dp, bottom = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_search_24),
                contentDescription = null
            )
        }
    }
}