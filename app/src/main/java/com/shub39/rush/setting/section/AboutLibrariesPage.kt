package com.shub39.rush.setting.section

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.shub39.rush.R
import com.shub39.rush.core.presentation.PageFill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutLibrariesPage(
    onNavigateBack: () -> Unit
) = PageFill {
    val libraries by produceLibraries()

    Scaffold(
        modifier = Modifier.widthIn(max = 1000.dp),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about_libraries)) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Navigate Back",
                        )
                    }
                }
            )
        }
    ) { padding ->
        LibrariesContainer(
            libraries = libraries,
            typography = MaterialTheme.typography,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}