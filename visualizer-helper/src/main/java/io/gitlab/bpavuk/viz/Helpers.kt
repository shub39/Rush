/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.gitlab.bpavuk.viz

fun VisualizerData.bassBucket(): VisualizerData {
    if (isEmpty()) return emptyList()
    val highBassUpperIndex = this.size * HIGH_BASS / HIGH_TREBLE
    return slice(0..highBassUpperIndex)
}

fun VisualizerData.midBucket(): VisualizerData {
    if (isEmpty()) return emptyList()
    val highBassUpperIndex = this.size * HIGH_BASS / HIGH_TREBLE
    val centerUpperMidUpperIndex = this.size * CENTER_UPPER_MID / HIGH_TREBLE
    return slice(highBassUpperIndex..centerUpperMidUpperIndex)
}

fun VisualizerData.trebleBucket(): VisualizerData {
    if (isEmpty()) return emptyList()
    val centerUpperMidUpperIndex = this.size * CENTER_UPPER_MID / HIGH_TREBLE
    val lowMidTrebleUpperIndex = this.size * LOW_MID_TREBLE / HIGH_TREBLE
    return slice(centerUpperMidUpperIndex..lowMidTrebleUpperIndex)
}
