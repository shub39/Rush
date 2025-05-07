package com.shub39.rush.core.domain

import org.jetbrains.compose.resources.StringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.dark
import rush.app.generated.resources.light
import rush.app.generated.resources.system

enum class AppTheme(val stringRes: StringResource) {
    SYSTEM(Res.string.system),
    LIGHT(Res.string.light),
    DARK(Res.string.dark)
}