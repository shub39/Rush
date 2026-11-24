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
package com.shub39.rush.shared.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.blur.BlurRadiusSpec
import androidx.compose.ui.graphics.blur.BlurStop
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.ui.progressiveBlurPossible

/** Space reserved by the app chrome that is drawn on top of the page content. */
object PageInsets {
    /** Height of the floating toolbar that overlays the saved page on compact windows. */
    val NavBarHeight: Dp = 88.dp

    /** Standard gap between page content and the window edges. */
    val Edge: Dp = 16.dp
}

/** Which edges of a lazily laid out page fade out behind the app chrome. */
enum class LazierBlur {
    /** No blur at all. */
    None,

    /** Fade out at the top edge, behind the top bar. */
    Top,
}

/** Defaults shared by the lazily laid out pages of the app. */
object LazierDefaults {
    private val EdgeBlur: Dp = 24.dp

    /**
     * Edge padding of a page plus clearance for the chrome that overlays the content, for example
     * the floating toolbar of the saved page.
     *
     * @param navBarClearance bottom space taken by an overlaid bottom bar, or zero when the page
     *   has none.
     */
    @Composable
    fun pagePadding(navBarClearance: Dp = 0.dp): PaddingValues =
        PaddingValues(
            start = PageInsets.Edge,
            end = PageInsets.Edge,
            top = PageInsets.Edge,
            bottom = PageInsets.Edge + navBarClearance,
        )

    /**
     * Adds [pagePadding] on top of the padding handed over by `Scaffold`. Use this for scroll
     * containers that are not a [LazierColumn], for example the sidebar of the saved page.
     */
    @Composable
    fun contentPaddingWith(
        scaffoldPadding: PaddingValues,
        navBarClearance: Dp = 0.dp,
    ): PaddingValues {
        val direction = LocalLayoutDirection.current
        val page = pagePadding(navBarClearance)

        return PaddingValues(
            start =
                scaffoldPadding.calculateStartPadding(direction) +
                    page.calculateStartPadding(direction),
            end =
                scaffoldPadding.calculateEndPadding(direction) +
                    page.calculateEndPadding(direction),
            top = scaffoldPadding.calculateTopPadding() + page.calculateTopPadding(),
            bottom = scaffoldPadding.calculateBottomPadding() + page.calculateBottomPadding(),
        )
    }

    /** Applies the progressive edge blur requested by [blur], if the platform supports it. */
    @Composable
    fun Modifier.lazierEdgeBlur(blur: LazierBlur): Modifier {
        val spec = edgeBlurSpec(blur) ?: return this

        return this.blur(blurRadiusSpec = spec)
    }

    /**
     * Progressive blur that fades the scrolling content out at the requested edges. Returns null
     * when no blur is requested or when the platform cannot render progressive blur.
     */
    @Composable
    fun edgeBlurSpec(edges: LazierBlur): BlurRadiusSpec? {
        if (edges == LazierBlur.None || !progressiveBlurPossible()) return null

        return when (edges) {
            LazierBlur.Top ->
                BlurRadiusSpec.verticalGradient(
                    listOf(BlurStop(0f, EdgeBlur), BlurStop(0.15f, 0.dp), BlurStop(1f, 0.dp))
                )

            LazierBlur.None -> null
        }
    }
}
