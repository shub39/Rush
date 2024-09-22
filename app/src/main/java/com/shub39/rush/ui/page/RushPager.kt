package com.shub39.rush.ui.page

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import com.shub39.rush.viewmodel.RushViewModel

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
                bottomSheet = bottomSheet,
                rushViewModel = rushViewModel
            )

            1 -> SavedPage(
                bottomSheet = bottomSheet,
                onClick = {
                    onPageChange(0)
                    lazyListRefresh()
                },
                rushViewModel = rushViewModel
            )
        }
    }
}