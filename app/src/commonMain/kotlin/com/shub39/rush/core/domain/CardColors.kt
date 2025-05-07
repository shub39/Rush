package com.shub39.rush.core.domain

import org.jetbrains.compose.resources.StringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.custom
import rush.app.generated.resources.muted
import rush.app.generated.resources.vibrant

enum class CardColors(
    val stringRes: StringResource
) {
    MUTED(Res.string.muted),
    VIBRANT(Res.string.vibrant),
    CUSTOM(Res.string.custom)
}