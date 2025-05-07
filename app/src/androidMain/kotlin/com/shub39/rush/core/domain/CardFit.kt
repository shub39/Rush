package com.shub39.rush.core.domain

import androidx.annotation.StringRes
import com.shub39.rush.R

enum class CardFit(
    @StringRes val title: Int
) {
    FIT(R.string.fit),
    STANDARD(R.string.standard)
}