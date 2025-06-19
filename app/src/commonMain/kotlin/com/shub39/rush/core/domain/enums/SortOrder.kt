package com.shub39.rush.core.domain.enums

import org.jetbrains.compose.resources.StringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.sort_date_added
import rush.app.generated.resources.sort_title_asc
import rush.app.generated.resources.sort_title_desc

enum class SortOrder(
    val stringRes: StringResource
) {
    DATE_ADDED(Res.string.sort_date_added),
    TITLE_ASC(Res.string.sort_title_asc),
    TITLE_DESC(Res.string.sort_title_desc),
}