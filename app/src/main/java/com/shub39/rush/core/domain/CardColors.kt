package com.shub39.rush.core.domain

import androidx.annotation.StringRes
import com.shub39.rush.R

enum class CardColors(
    @StringRes val title: Int
) {
    MUTED(R.string.muted),
    VIBRANT(R.string.vibrant),
    CUSTOM(R.string.custom)
}