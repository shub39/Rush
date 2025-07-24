package com.shub39.rush.core.domain.enums

import com.shub39.rush.R

enum class CardTheme(
    val stringRes: Int
) {
    SPOTIFY(R.string.spotify),
    RUSHED(R.string.rushed),
    VERTICAL(R.string.vertical),
    COUPLET(R.string.couplet),

    HYPNOTIC(R.string.hypnotic),
    QUOTE(R.string.quote),
    MESSY(R.string.messy),
    CHAT(R.string.chat),
    EXPRESSIVE(R.string.expressive)

    ;

    companion object {
        val premiumCards = listOf(
            EXPRESSIVE, HYPNOTIC, MESSY, QUOTE, CHAT
        )
    }
}