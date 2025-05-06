package com.shub39.rush.core.domain

import androidx.annotation.StringRes
import com.shub39.rush.R

enum class CardTheme(
    @StringRes val title: Int
) {
    SPOTIFY(R.string.spotify),
    RUSHED(R.string.rushed),
    HYPNOTIC(R.string.hypnotic),
    VERTICAL(R.string.vertical),
    QUOTE(R.string.quote)
}