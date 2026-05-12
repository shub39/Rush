package com.shub39.rush.domain.util

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

fun NavBackStack<NavKey>.pop() {
    if (size > 1) removeLastOrNull()
}