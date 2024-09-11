package com.shub39.rush.page

import androidx.annotation.StringRes
import com.shub39.rush.R

enum class SortOrder(
    val sortOrder: String,
    @StringRes val textId: Int
) {
    TITLE_ASC("title_asc", R.string.sort_title_asc),
    TITLE_DESC("title_desc", R.string.sort_title_desc),
    ARTISTS_ASC("artists_asc", R.string.sort_artists_asc),
    ALBUM_ASC("album_asc", R.string.sort_album_asc),
}