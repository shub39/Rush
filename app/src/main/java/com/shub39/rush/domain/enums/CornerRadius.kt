package com.shub39.rush.domain.enums

import com.shub39.rush.R

// default is a reserved java keyword
enum class CornerRadius(
    val stringRes: Int
) {
    DEFAULT(R.string.default_),
    ROUNDED(R.string.rounded)
}