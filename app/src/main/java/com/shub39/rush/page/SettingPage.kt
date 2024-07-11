package com.shub39.rush.page

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.database.SettingsDataStore
import com.shub39.rush.viewmodel.RushViewModel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(
    rushViewModel: RushViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var deleteButtonStatus by remember { mutableStateOf(rushViewModel.songs.value.isNotEmpty()) }
    val maxLinesFlow by SettingsDataStore.getMaxLinesFlow(context).collectAsState(initial = 6)
    val appTheme by SettingsDataStore.getToggleThemeFlow(context)
        .collectAsState(initial = "Gruvbox")
    var theme = appTheme
    var maxLines = maxLinesFlow

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomEnd = 8.dp,
                    bottomStart = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = stringResource(id = R.string.theme))

                    Spacer(modifier = Modifier.padding(4.dp))

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            listOf("Material", "Yellow", "Lime").forEachIndexed { index, color ->
                                SegmentedButton(
                                    label = { Text(text = color) },
                                    selected = theme == color,
                                    onClick = {
                                        theme = color
                                        coroutineScope.launch {
                                            SettingsDataStore.updateToggleTheme(context, theme)
                                        }
                                    },
                                    shape = when (index) {
                                        0 -> RoundedCornerShape(
                                            topStart = 16.dp,
                                            bottomStart = 16.dp
                                        )

                                        2 -> RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                                        else -> RoundedCornerShape(0.dp)
                                    }
                                )
                            }
                        } else {
                            listOf("Yellow", "Lime").forEachIndexed { index, color ->
                                SegmentedButton(
                                    label = { Text(text = color) },
                                    selected = theme == color,
                                    onClick = {
                                        theme = color
                                        coroutineScope.launch {
                                            SettingsDataStore.updateToggleTheme(context, theme)
                                        }
                                    },
                                    shape = when (index) {
                                        0 -> RoundedCornerShape(
                                            topStart = 16.dp,
                                            bottomStart = 16.dp
                                        )

                                        else -> RoundedCornerShape(
                                            topEnd = 16.dp,
                                            bottomEnd = 16.dp
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }


        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp,
                    bottomEnd = 16.dp,
                    bottomStart = 16.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.max_lines))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                maxLines--
                                coroutineScope.launch {
                                    SettingsDataStore.updateMaxLines(context, maxLines)
                                }
                            },
                            enabled = maxLines > 2 && coroutineScope.isActive
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_arrow_back_ios_24),
                                contentDescription = null
                            )
                        }
                        Text(text = maxLinesFlow.toString())
                        IconButton(
                            onClick = {
                                maxLines++
                                coroutineScope.launch {
                                    SettingsDataStore.updateMaxLines(context, maxLines)
                                }
                            },
                            enabled = maxLines < 8 && coroutineScope.isActive
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_arrow_forward_ios_24),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }


        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    rushViewModel.songs.value.forEach {
                        rushViewModel.deleteSong(it)
                    }
                    deleteButtonStatus = false
                },
                shape = MaterialTheme.shapes.large,
                enabled = deleteButtonStatus
            ) {
                Text(text = stringResource(id = R.string.delete_all))
            }
        }


        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                Text(
                    text = "Made by shub39",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Row(
                    modifier = Modifier.padding(bottom = 12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.github_mark),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                openLinkInBrowser(context, "https://github.com/shub39/Rush")
                            }
                    )
                }
            }
        }
    }

}

fun openLinkInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}