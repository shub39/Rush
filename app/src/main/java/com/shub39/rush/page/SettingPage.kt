package com.shub39.rush.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.viewmodel.RushViewModel

@Composable
fun SettingPage(
    rushViewModel: RushViewModel,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                rushViewModel.songs.value.forEach {
                    rushViewModel.deleteSong(it)
                }
            },
            shape = MaterialTheme.shapes.large
        ) {
            Text(text = stringResource(id = R.string.delete_all))
        }

        Spacer(modifier = Modifier.padding(4.dp))

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                openLinkInBrowser(context, "https://github.com/shub39/Rush")
            },
            shape = MaterialTheme.shapes.large
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