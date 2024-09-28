package com.shub39.rush.ui.page

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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

    val pagerState = rememberPagerState(initialPage = 1) { 2 }
    val coroutineScope = rememberCoroutineScope()

    if (searchSheetState) {
        SearchSheet(
            rushViewModel = rushViewModel,
            pagerState = pagerState
        )
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
                    rushViewModel = rushViewModel,
                    pagerState = pagerState
                )
            }

            composable("settings") {
                SettingPage(rushViewModel = rushViewModel)
            }
        }
    }
}