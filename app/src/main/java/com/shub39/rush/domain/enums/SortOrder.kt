package com.shub39.rush.domain.enums

import com.shub39.rush.R

enum class SortOrder(
    val stringRes: Int
) {
    DATE_ADDED(R.string.sort_date_added),
    TITLE_ASC(R.string.sort_title_asc),
    TITLE_DESC(R.string.sort_title_desc),
}