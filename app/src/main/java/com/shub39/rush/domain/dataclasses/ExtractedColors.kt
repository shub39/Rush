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
    val cardBackgroundDominant: Int = 0xFF4444,
    val cardContentDominant: Int = 0xFFFFFF,
    val cardBackgroundMuted: Int = 0xFF8888,
    val cardContentMuted: Int = 0xFFFFFF
)