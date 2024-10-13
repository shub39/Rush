package com.shub39.rush.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.viewmodel.RushViewModel

@Composable
fun ErrorCard(
    rushViewModel: RushViewModel,
    colors: Pair<Color, Color>
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.round_warning_24),
            contentDescription = null,
            modifier = Modifier
                .size(128.dp)
                .padding(16.dp),
            tint = colors.first
        )

        Text(
            text = stringResource(id = R.string.error),
            color = colors.first
        )

        Spacer(Modifier.padding(4.dp))

        Button(
            onClick = { rushViewModel.retry() },
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.first,
                contentColor = colors.second
            )
        ) {
            Text(stringResource(R.string.try_again))
        }
    }
}