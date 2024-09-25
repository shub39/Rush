package com.shub39.rush.ui.page

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shub39.rush.ui.component.SearchSheet
import com.shub39.rush.viewmodel.RushViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun RushApp(
    navController: NavHostController,
    rushViewModel: RushViewModel = koinViewModel()
) {
    val searchSheetState by rushViewModel.searchSheet.collectAsState()
    val currentPage by rushViewModel.currentPage.collectAsState()
    val scrollTrigger by rushViewModel.scrollTrigger.collectAsState()

    val pagerState = rememberPagerState(initialPage = currentPage) { 2 }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentPage) {
        pagerState.animateScrollToPage(currentPage)
    }

    LaunchedEffect(scrollTrigger) {
        lazyListState.scrollToItem(0)
    }

    if (searchSheetState) {
        SearchSheet(rushViewModel)
    }

    BackHandler(
        enabled = pagerState.currentPage == 0
    ) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(1)
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
                SettingPage(rushViewModel)
            }
        }
    }
}