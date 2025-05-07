package com.shub39.rush.core.domain

import androidx.annotation.StringRes
import com.shub39.rush.R

// default is a reserved kotlin keyword
enum class CornerRadius(
    @StringRes val title: Int
) {
    DEFAULT(R.string.default_),
    ROUNDED(R.string.rounded)
}