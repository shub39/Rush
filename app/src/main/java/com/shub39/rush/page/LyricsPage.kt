package com.shub39.rush.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.shub39.rush.R
import com.shub39.rush.component.ArtFromUrl
import com.shub39.rush.component.Empty
import com.shub39.rush.viewmodel.RushViewModel

@Composable
fun LyricsPage(
    rushViewModel: RushViewModel,
    imageLoader: ImageLoader
) {
    val song by rushViewModel.currentSong.collectAsState()
    val fetching by rushViewModel.isFetchingLyrics.collectAsState()
    val context = LocalContext.current

    if (fetching) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeCap = StrokeCap.Round
                )
            }
        }
    } else if (song != null) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
            ) {
                ArtFromUrl(
                    imageUrl = song!!.artUrl,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(MaterialTheme.shapes.small),
                    imageLoader = imageLoader
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = song!!.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song!!.artists,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    song!!.album?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Row {
                        IconButton(onClick = { openLinkInBrowser(context, song!!.sourceUrl) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.genius),
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_share_24),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            LazyColumn(
                modifier = Modifier.padding(end = 16.dp, start = 16.dp, bottom = 16.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = song!!.lyrics,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    } else {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Empty()
        }
    }
}