package com.shub39.rush.domain.dataclasses


/**
 * Represents a single line of a lyric with its corresponding timestamp.
 *
 * @property time The timestamp in milliseconds at which this lyric line appears.
 * @property text The actual text content of the lyric line.
 */
data class Lyric(
    val time: Long,
    val text: String
)