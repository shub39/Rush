package com.shub39.rush.core.domain

import androidx.annotation.StringRes
import com.shub39.rush.R

enum class AppTheme(@StringRes val fullName: Int) {
    SYSTEM(R.string.system),
    LIGHT(R.string.light),
    DARK(R.string.dark)
}