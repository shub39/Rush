package com.shub39.rush.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.viewmodel.RushViewModel

@Composable
fun SettingPage(
    rushViewModel: RushViewModel,
) {
    val songs by rushViewModel.songs.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Row {
                    Text(
                        text = stringResource(id = R.string.downloaded),
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = songs.size.toString(),
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {
                        rushViewModel.songs.value.forEach {
                            rushViewModel.deleteSong(it)
                        }
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(text = stringResource(id = R.string.delete_all))
                }
            }
        }

        OutlinedCard(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                .fillMaxWidth(),
            onClick = {
                openLinkInBrowser(context, "https://github.com/shub39/Rush")
            }
        ) {
            Text(
                text = "Made by shub39",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}