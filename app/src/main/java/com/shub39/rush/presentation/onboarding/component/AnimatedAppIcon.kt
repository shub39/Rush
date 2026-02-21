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
package com.shub39.rush.presentation.onboarding.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedAppIcon() {
    Box(modifier = Modifier.size(300.dp)) {
        val offsetX = remember { Animatable(300f) }
        val offsetY = remember { Animatable(-300f) }
        val scale = remember { Animatable(0.2f) }
        val alpha = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            while (true) {
                offsetX.snapTo(300f)
                offsetY.snapTo(-300f)
                scale.snapTo(0.2f)
                alpha.snapTo(0f)

                launch { offsetX.animateTo(0f, animationSpec = tween(1000)) }
                launch { offsetY.animateTo(0f, animationSpec = tween(1000)) }
                launch { scale.animateTo(1f, animationSpec = tween(1000)) }
                alpha.animateTo(1f, animationSpec = tween(1000))

                delay(2000)

                launch { offsetX.animateTo(-300f, animationSpec = tween(1000)) }
                launch { offsetY.animateTo(300f, animationSpec = tween(1000)) }
                launch { scale.animateTo(0.2f, animationSpec = tween(1000)) }
                alpha.animateTo(0f, animationSpec = tween(1000))

                delay(300)
            }
        }

        Icon(
            painter = painterResource(R.drawable.app_icon),
            contentDescription = "App Icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier =
                Modifier.graphicsLayer {
                        translationX = offsetX.value
                        translationY = offsetY.value
                        scaleX = scale.value
                        scaleY = scale.value
                        this.alpha = alpha.value
                    }
                    .size(300.dp),
        )
    }
}
