package com.shub39.rush.page

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
                rushViewModel = rushViewModel,
                bottomSheet = bottomSheet,
            )

            1 -> SavedPage(
                rushViewModel = rushViewModel,
                bottomSheet = bottomSheet,
                onClick = {
                    onPageChange(0)
                    lazyListRefresh()
                }
            )
        }
    }
}