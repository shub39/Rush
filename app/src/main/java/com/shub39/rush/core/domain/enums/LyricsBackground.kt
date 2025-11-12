package com.shub39.rush.core.domain.enums

import com.shub39.rush.R
import com.shub39.rush.core.presentation.blurAvailable
import com.shub39.rush.core.presentation.hypnoticAvailable

enum class LyricsBackground(val stringRes: Int) {
    HYPNOTIC(R.string.hypnotic),
    ALBUM_ART(R.string.blurred_art),
    SOLID_COLOR(R.string.solid_color),
    WAVE(R.string.wave),
    GRADIENT(R.string.gradient),
    CURVE(R.string.curve)

    ;

    companion object {
        val allBackgrounds = if (hypnoticAvailable()) {
            listOf(SOLID_COLOR, ALBUM_ART, HYPNOTIC, WAVE, GRADIENT, CURVE)
        } else if (blurAvailable()) {
            listOf(SOLID_COLOR, ALBUM_ART, WAVE, GRADIENT, CURVE)
        } else {
            listOf(SOLID_COLOR, WAVE, GRADIENT)
        }

        val audioDependentBackgrounds = listOf(WAVE, GRADIENT, CURVE)
    }
}