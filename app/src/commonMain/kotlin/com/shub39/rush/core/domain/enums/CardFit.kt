package com.shub39.rush.core.domain.enums

import org.jetbrains.compose.resources.StringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.fit
import rush.app.generated.resources.standard

enum class CardFit(
    val stringRes: StringResource
) {
    FIT(Res.string.fit),
    STANDARD(Res.string.standard)
}