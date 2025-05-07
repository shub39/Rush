package com.shub39.rush.core.domain

import org.jetbrains.compose.resources.StringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.default_
import rush.app.generated.resources.rounded

// default is a reserved kotlin keyword
enum class CornerRadius(
    val stringRes: StringResource
) {
    DEFAULT(Res.string.default_),
    ROUNDED(Res.string.rounded)
}