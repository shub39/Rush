package com.shub39.rush.lyrics.presentation.setting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.shub39.rush.R
import com.shub39.rush.core.presentation.PageFill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutLibraries() = PageFill {
    Scaffold(
        modifier = Modifier.widthIn(max = 500.dp),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about_libraries)) }
            )
        }
    ) { padding ->
        LibrariesContainer(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            colors = LibraryDefaults.libraryColors(
                backgroundColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                badgeBackgroundColor = MaterialTheme.colorScheme.primary,
                badgeContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}