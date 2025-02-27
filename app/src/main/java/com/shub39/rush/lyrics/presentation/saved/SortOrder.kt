package com.shub39.rush.lyrics.presentation.saved

import androidx.annotation.StringRes
import com.shub39.rush.R

enum class SortOrder(
    @StringRes val textId: Int
) {
    DATE_ADDED(R.string.sort_date_added),
    TITLE_ASC(R.string.sort_title_asc),
    TITLE_DESC(R.string.sort_title_desc),
    ARTISTS_ASC(R.string.sort_artists_asc),
    ALBUM_ASC(R.string.sort_album_asc)
}