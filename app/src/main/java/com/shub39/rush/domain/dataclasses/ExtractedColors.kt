package com.shub39.rush.domain.dataclasses


/**
 * A data class to hold the extracted colors from an image
 *
 * @property cardBackgroundDominant The dominant color from the image
 * @property cardContentDominant A suitable color for text or icons placed on top of [cardBackgroundDominant].
 * @property cardBackgroundMuted A more subdued or muted color from the image
 * @property cardContentMuted A suitable color for text or icons placed on top of [cardBackgroundMuted].
 */
data class ExtractedColors(
    val cardBackgroundDominant: Long = 0xFF444444,
    val cardContentDominant: Long = 0xFFFFFFFF,
    val cardBackgroundMuted: Long = 0xFF888888,
    val cardContentMuted: Long = 0xFFFFFFFF
)