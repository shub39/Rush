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