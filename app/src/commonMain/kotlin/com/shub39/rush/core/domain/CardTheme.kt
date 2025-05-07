package com.shub39.rush.core.domain

import org.jetbrains.compose.resources.StringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.couplet
import rush.app.generated.resources.hypnotic
import rush.app.generated.resources.quote
import rush.app.generated.resources.rushed
import rush.app.generated.resources.spotify
import rush.app.generated.resources.vertical

enum class CardTheme(
    val stringRes: StringResource
) {
    SPOTIFY(Res.string.spotify),
    RUSHED(Res.string.rushed),
    HYPNOTIC(Res.string.hypnotic),
    VERTICAL(Res.string.vertical),
    QUOTE(Res.string.quote),
    COUPLET(Res.string.couplet)
}