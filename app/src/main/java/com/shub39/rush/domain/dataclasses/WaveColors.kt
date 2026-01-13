package com.shub39.rush.domain.dataclasses

/**
 * A data class that holds the colors for a wave-style card.
 *
 * @property cardBackground The primary background color of the card.
 * @property cardWaveBackground The color used for the wave animation/pattern on the card.
 */
data class WaveColors(
    val cardBackground: Int,
    val cardWaveBackground: Int,
)