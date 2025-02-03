package com.shub39.rush.lyrics.presentation.lyrics.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerContainer

@Composable
fun LoadingCard(
    fetching: Pair<Boolean, String>,
    searching: Pair<Boolean, String>,
    colors: Pair<Color, Color>
) {
    if (searching.first) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.round_search_24),
                contentDescription = null,
                modifier = Modifier.size(150.dp),
                tint = colors.first
            )

            Spacer(modifier = Modifier.padding(4.dp))

            LinearProgressIndicator(
                color = colors.first,
                trackColor = Color.Transparent
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = searching.second,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }

    if (fetching.first) {
        val artist = fetching.second.split(" - ")[0]
        val album = fetching.second.split(" - ")[1]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ShimmerContainer(
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium),
                shimmer = Shimmer.Resonate(
                    highlightColor = colors.first,
                    baseColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = artist,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = album,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Row (
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ){
                repeat(4) {
                    ShimmerContainer(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(MaterialTheme.shapes.extraLarge),
                        shimmer = Shimmer.Resonate(
                            highlightColor = colors.first,
                            baseColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            repeat(8) {
                Spacer(modifier = Modifier.padding(4.dp))

                ShimmerContainer(
                    modifier = Modifier
                        .height(32.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium),
                    shimmer = Shimmer.Resonate(
                        highlightColor = colors.first,
                        baseColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingPreview() {
    LoadingCard(
        fetching = Pair(true, "Digital Bath - Deftones"),
        searching = Pair(false, "Digital Bath - Deftones"),
        colors = Pair(Color.Blue, Color.White)
    )
}