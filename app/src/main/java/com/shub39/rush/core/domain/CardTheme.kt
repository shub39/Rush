package com.shub39.rush.core.domain

import androidx.annotation.StringRes
import com.shub39.rush.R

enum class CardTheme(
    @StringRes val title: Int
) {
    SPOTIFY(R.string.spotify),
    RUSHED(R.string.rushed),
    IMAGE(R.string.image),
    HYPNOTIC(R.string.hypnotic)
}