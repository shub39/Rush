package com.shub39.rush.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.viewmodel.RushViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ErrorCard(
    rushViewModel: RushViewModel = koinViewModel()
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 15.dp, end = 16.dp, bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_warning_24),
                contentDescription = null,
                modifier = Modifier.size(128.dp).padding(16.dp),
                tint = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = stringResource(id = R.string.error),
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.padding(4.dp))

            Button(onClick = {rushViewModel.retry()}) {
                Text(stringResource(R.string.try_again))
            }
        }
    }
}