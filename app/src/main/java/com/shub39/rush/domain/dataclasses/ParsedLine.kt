package com.shub39.rush.domain.dataclasses

data class ParsedLine(
    val text: String,
    val startTime: Double,
    val words: List<ParsedWord>,
    val agent: String? = null,
    val isBackground: Boolean = false,
    val backgroundLines: List<ParsedLine> = emptyList(),
)