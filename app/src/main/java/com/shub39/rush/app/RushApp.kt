package com.shub39.rush.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.lyrics.presentation.component.searchsheet.SearchSheet
import com.shub39.rush.lyrics.presentation.component.TopBar
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPage
import com.shub39.rush.lyrics.presentation.saved.SavedPage
import com.shub39.rush.lyrics.presentation.setting.SettingPage
import com.shub39.rush.share.presentation.SharePage
import com.shub39.rush.lyrics.presentation.RushViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun RushApp(
    rushViewModel: RushViewModel = koinViewModel()
) {
    val lyricsState by rushViewModel.lyricsState.collectAsStateWithLifecycle()
    val savedState by rushViewModel.savedState.collectAsStateWithLifecycle()
    val shareState by rushViewModel.shareState.collectAsStateWithLifecycle()
    val settingsState by rushViewModel.settingsState.collectAsStateWithLifecycle()
    val searchSheetState by rushViewModel.searchState.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    val pagerState = rememberPagerState(1) { 2 }

    if (searchSheetState.visible) {
        SearchSheet(
            state = searchSheetState,
            action = rushViewModel::onSearchSheetAction,
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(0)
                }
            }
        )
    }

    BackHandler(pagerState.currentPage == 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(1)
        }
    }

    HorizontalPager(pagerState) {
        when (it) {
            1 -> Scaffold(
                topBar = { TopBar(navController = navController) },
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = SavedPage.ROUTE,
                    modifier = Modifier.padding(innerPadding),
                    enterTransition = { fadeIn(animationSpec = tween(200)) },
                    exitTransition = { fadeOut(animationSpec = tween(200)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(200)) },
                    popExitTransition = { fadeOut(animationSpec = tween(200)) }
                ) {
                    composable(SavedPage.ROUTE) {
                        SavedPage(
                            state = savedState,
                            action = rushViewModel::onSavedPageAction,
                            onSongClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            }
                        )
                    }

                    composable(SettingsPage.ROUTE) {
                        SettingPage(
                            state = settingsState,
                            action = rushViewModel::onSettingsPageAction
                        )
                    }

                    composable(SharePage.ROUTE) {
                        SharePage(
                            onDismiss = {
                                navController.navigateUp()
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            state = shareState,
                            action = rushViewModel::onSharePageAction
                        )
                    }
                }
            }

            0 -> LyricsPage(
                state = lyricsState,
                action = rushViewModel::onLyricsPageAction,
                onShare = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                    navController.navigate(SharePage.ROUTE)
                }
            )
        }
    }
}

@Serializable
object SavedPage {
    const val ROUTE = "saved"
}

@Serializable
object SettingsPage {
    const val ROUTE = "settings"
}

@Serializable
object SharePage {
    const val ROUTE = "share"
}