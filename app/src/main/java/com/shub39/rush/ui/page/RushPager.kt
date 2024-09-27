package com.shub39.rush.ui.page

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import com.shub39.rush.viewmodel.RushViewModel

@Composable
fun RushPager(
    rushViewModel: RushViewModel,
    pagerState: PagerState
) {
    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false
    ) { page ->
        when (page) {
            0 -> LyricsPage(rushViewModel = rushViewModel)
            1 -> SavedPage(rushViewModel = rushViewModel)
        }
    }
}