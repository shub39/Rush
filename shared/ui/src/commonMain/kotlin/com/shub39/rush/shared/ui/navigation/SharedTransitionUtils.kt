/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.shared.ui.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.ArcMode
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

val LocalAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

@Composable
fun Modifier.rushSharedElement(key: Any, boundsTransform: BoundsTransform? = null): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current ?: return this
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current ?: return this
    with(sharedTransitionScope) {
        return if (boundsTransform != null) {
            this@rushSharedElement.sharedElement(
                rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = boundsTransform,
            )
        } else {
            this@rushSharedElement.sharedElement(
                rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope,
            )
        }
    }
}

@Composable
fun Modifier.rushSharedBounds(key: Any, boundsTransform: BoundsTransform? = null): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current ?: return this
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current ?: return this
    with(sharedTransitionScope) {
        return if (boundsTransform != null) {
            this@rushSharedBounds.sharedBounds(
                rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = boundsTransform,
                resizeMode =
                    SharedTransitionScope.ResizeMode.scaleToBounds(
                        contentScale = ContentScale.FillBounds
                    ),
            )
        } else {
            this@rushSharedBounds.sharedBounds(
                rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope,
                resizeMode =
                    SharedTransitionScope.ResizeMode.scaleToBounds(
                        contentScale = ContentScale.FillBounds
                    ),
            )
        }
    }
}

private const val BOUND_DURATION_MILLIS = 500

fun getTextBoundsTransform(): BoundsTransform = BoundsTransform { initialBounds, targetBounds ->
    keyframes {
        durationMillis = BOUND_DURATION_MILLIS
        initialBounds at 0 using ArcMode.ArcBelow using FastOutSlowInEasing
        targetBounds at BOUND_DURATION_MILLIS
    }
}

fun getImageBoundsTransform(): BoundsTransform = BoundsTransform { initialBounds, targetBounds ->
    keyframes {
        durationMillis = BOUND_DURATION_MILLIS
        initialBounds at 0 using ArcMode.ArcLinear using FastOutSlowInEasing
        targetBounds at BOUND_DURATION_MILLIS
    }
}
