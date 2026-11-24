package com.sameerasw.island.sdk

data class IslandPayload(
    val schema: Int = IslandSchema.VERSION,
    val items: List<IslandItemPayload> = emptyList(),
    val briefCards: List<BriefCardPayload> = emptyList(),
    val options: List<IslandOptionPayload> = emptyList(),
    val attachments: List<IslandAttachmentPayload> = emptyList(),
)

data class IslandOptionPayload(
    val key: String = "",
    val title: String = "",
    val checked: Boolean = false,
)

/**
 * A provider-drawn shape shown below a host item while that item is expanded, for example lyrics under the
 * media player. Its surface key is [IslandSurfaceKey.attachment] of [key]; draw it in ExpandedContent.
 *
 * @param anchor Host item to hang under, one of [IslandAnchor].
 * @param heightDp First guess for the height; report the real one with IslandHostHandle.resize.
 * @param matchTitle When set, the host shows the attachment only while the anchor's content matches it
 *  (for [IslandAnchor.MEDIA], the playing track's title), so stale content never sits under another track.
 */
data class IslandAttachmentPayload(
    val key: String = "",
    val anchor: String = IslandAnchor.MEDIA,
    val heightDp: Int = 72,
    val matchTitle: String? = null,
)

data class BriefCardPayload(
    val key: String = "",
    val title: String = "",
    val subtitle: String? = null,
    val iconId: String? = null,
    val tone: String = IslandTone.ACCENT,
    val trailingIconId: String? = null,
    val trailingText: String? = null,
    val trailingTone: String = IslandTone.ACCENT,
    val pageHeightDp: Int? = null,
)

data class IslandItemPayload(
    val key: String = "",
    val compact: List<IslandCellPayload> = emptyList(),
    val priority: Int = 100,
    val priorityOverride: Int? = null,
    val placement: String = IslandPlacement.DYNAMIC,
    val compactVisible: Boolean = true,
    val line: IslandLinePayload? = null,
    val expanded: IslandExpandedPayload? = null,
    val accent: Int? = null,
    val dismissible: Boolean = false,
    val interactions: IslandInteractionsPayload = IslandInteractionsPayload(),
)

data class IslandCellPayload(
    val key: String = "",
    val iconId: String? = null,
    val text: String? = null,
    val tone: String = IslandTone.ACCENT,
    val soloOnly: Boolean = false,
)

data class IslandLinePayload(
    val start: String = "",
    val end: String = "",
    val iconId: String? = null,
    val tone: String = IslandTone.ACCENT,
)

data class IslandExpandedPayload(
    val heightDp: Int = 0,
    val scrollable: Boolean = false,
)

data class IslandInteractionsPayload(
    val onTap: IslandAction? = null,
    val onLongPress: IslandAction? = null,
    val onExpandedTap: IslandAction? = null,
    val onOpen: IslandAction? = null,
)

data class IslandAction(
    val type: String = IslandActionType.NONE,
    val target: String? = null,
)
