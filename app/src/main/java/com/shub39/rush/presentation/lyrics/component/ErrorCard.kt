package com.shub39.rush.presentation.lyrics.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.presentation.copyToClipboard
import kotlinx.coroutines.launch

@Composable
fun ErrorCard(
    error: Int,
    debugMessage: String?,
    colors: Pair<Color, Color>
) {
    val clipboard = LocalClipboard.current
    val scope  = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.warning),
            contentDescription = "Error",
            modifier = Modifier
                .size(128.dp)
                .padding(16.dp),
            tint = colors.first
        )

        Text(
            text = stringResource(error),
            color = colors.first
        )

        AnimatedVisibility(
            visible = debugMessage != null
        ) {
            TextButton(
                onClick = {
                    scope.launch { clipboard.copyToClipboard(text = debugMessage ?: "i am dumb") }
                },
                modifier = Modifier.padding(top = 4.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = colors.first,
                    containerColor = colors.second
                )
            ) {
                Text(text = stringResource(R.string.copy_error))
            }
        }
    }
}