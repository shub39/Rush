package com.shub39.rush.page

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.shub39.rush.R
import com.shub39.rush.component.SearchResultCard
import com.shub39.rush.database.SettingsDataStore
import com.shub39.rush.viewmodel.RushViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RushApp(
    navController: NavHostController,
    rushViewModel: RushViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = 1) { 2 }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val song by rushViewModel.currentSong.collectAsState()
    var searchSheetState by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val searchResults by rushViewModel.searchResults.collectAsState()
    val isFetchingLyrics by rushViewModel.isSearchingLyrics.collectAsState()

    if (searchSheetState) {
        ModalBottomSheet(
            onDismissRequest = {
                searchSheetState = false
                query = ""
            }
        ) {
            val keyboardController = LocalSoftwareKeyboardController.current
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                keyboardController?.show()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                OutlinedTextField(
                    value = query,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.round_search_24),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp)
                        )
                    },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            AnimatedVisibility(
                                visible = isFetchingLyrics,
                                enter = fadeIn(animationSpec = tween(200)),
                                exit = fadeOut(animationSpec = tween(200))
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeCap = StrokeCap.Round
                                )
                            }
                            AnimatedVisibility(
                                visible = query.isNotBlank(),
                                enter = fadeIn(animationSpec = tween(200)),
                                exit = fadeOut(animationSpec = tween(200))
                            ) {
                                IconButton(
                                    onClick = {
                                        query = ""
                                        coroutineScope.launch {
                                            focusRequester.requestFocus()
                                            keyboardController?.show()
                                        }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.round_delete_forever_24),
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    },
                    onValueChange = {
                        query = it
                        rushViewModel.searchSong(it)
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    label = { Text(stringResource(id = R.string.search)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            rushViewModel.searchSong(query)
                        }
                    )
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(searchResults, key = { it.id }) {
                        SearchResultCard(
                            result = it,
                            onClick = {
                                searchSheetState = false
                                query = ""
                                if (song == null) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(0)
                                        rushViewModel.changeCurrentSong(it.id)
                                        SettingsDataStore.updateSongAutofill(context, false)
                                    }
                                } else {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(0)
                                        lazyListState.scrollToItem(0)
                                        rushViewModel.changeCurrentSong(it.id)
                                        SettingsDataStore.updateSongAutofill(context, false)
                                    }
                                }
                            },
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.padding(60.dp))
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = { TopBar(navController = navController, pagerState = pagerState) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "lyrics",
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(200)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            composable("lyrics") {
                RushPager(
                    lazyListState = lazyListState,
                    pagerState = pagerState,
                    bottomSheet = { searchSheetState = true },
                    onPageChange = { page ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page)
                        }
                    },
                    lazyListRefresh = {
                        coroutineScope.launch {
                            lazyListState.scrollToItem(0)
                        }
                    },
                    rushViewModel = rushViewModel
                )
            }
            composable("settings") {
                SettingPage(
                    rushViewModel = rushViewModel
                )
            }
        }
    }
}

@Composable
fun RushPager(
    lazyListState: LazyListState,
    pagerState: PagerState,
    bottomSheet: () -> Unit = {},
    onPageChange: (Int) -> Unit,
    lazyListRefresh: () -> Unit,
    rushViewModel: RushViewModel
) {
    HorizontalPager(
        state = pagerState,
    ) { page ->
        when (page) {
            0 -> LyricsPage(
                lazyListState = lazyListState,
                rushViewModel = rushViewModel,
                bottomSheet = { bottomSheet() },
                lazyListRefresh = lazyListRefresh

            )

            1 -> SavedPage(
                rushViewModel = rushViewModel,
                bottomSheet = { bottomSheet() },
                onClick = {
                    onPageChange(0)
                    lazyListRefresh()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    pagerState: PagerState
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    TopAppBar(
        title = {
            if (currentDestination?.route == "settings") {
                Text(
                    text = stringResource(id = R.string.settings),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (pagerState.currentPage == 1) {
                Text(
                    text = stringResource(id = R.string.saved),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            BackHandler(
                enabled = currentDestination?.route == "settings"
            ) {
                navController.navigateUp()
            }

            IconButton(
                onClick = {
                    if (currentDestination?.route != "settings") {
                        navController.navigate("settings") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigateUp()
                    }
                }
            ) {
                if (currentDestination?.route != "settings") {
                    Icon(
                        painter = painterResource(id = R.drawable.round_settings_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.round_arrow_back_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
    )
}