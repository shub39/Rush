package com.shub39.rush.page

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable

@Composable
fun RushPager(
    lazyListState: LazyListState,
    pagerState: PagerState,
    bottomSheet: () -> Unit = {},
    onPageChange: (Int) -> Unit,
    lazyListRefresh: () -> Unit,
) {
    HorizontalPager(
        state = pagerState,
    ) { page ->
        when (page) {
            0 -> LyricsPage(
                lazyListState = lazyListState,
                bottomSheet = bottomSheet,
            )

            1 -> SavedPage(
                bottomSheet = bottomSheet,
                onClick = {
                    onPageChange(0)
                    lazyListRefresh()
                }
            )
        }
    }
}