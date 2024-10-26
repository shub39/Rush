package com.shub39.rush.ui.page

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.ui.page.component.searchsheet.SearchSheet
import com.shub39.rush.ui.page.component.TopBar
import com.shub39.rush.ui.page.lyrics.LyricsPage
import com.shub39.rush.ui.page.saved.SavedPage
import com.shub39.rush.ui.page.setting.SettingPage
import com.shub39.rush.ui.page.share.SharePage
import com.shub39.rush.viewmodel.RushViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun RushApp(
    rushViewModel: RushViewModel = koinViewModel()
) {
    val searchSheetState by rushViewModel.searchSheet.collectAsState()
    val lyricsState by rushViewModel.lyricsPageState.collectAsStateWithLifecycle()
    val savedState by rushViewModel.savedPageState.collectAsStateWithLifecycle()
    val shareState by rushViewModel.sharePageState.collectAsStateWithLifecycle()
    val settingsState by rushViewModel.settingsPageState.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    val pagerState = rememberPagerState(1) { 2 }

    if (searchSheetState) {
        SearchSheet(
            rushViewModel = rushViewModel,
            coroutineScope = coroutineScope,
            pagerState = pagerState
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
                    startDestination = SavedPage,
                    modifier = Modifier.padding(innerPadding),
                    enterTransition = { fadeIn(animationSpec = tween(200)) },
                    exitTransition = { fadeOut(animationSpec = tween(200)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(200)) },
                    popExitTransition = { fadeOut(animationSpec = tween(200)) }
                ) {
                    composable<SavedPage> {
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

                    composable<SettingsPage> {
                        SettingPage(
                            state = settingsState,
                            action = rushViewModel::onSettingsPageAction
                        )
                    }

                    composable<SharePage> {
                        SharePage(
                            onDismiss = {
                                navController.navigateUp()
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            state = shareState
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
                    navController.navigate(SharePage)
                }
            )
        }
    }
}

@Serializable
object SavedPage

@Serializable
object SettingsPage

@Serializable
object SharePage