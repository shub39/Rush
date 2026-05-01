package com.shub39.rush.domain.dataclasses

data class SpanInfo(
    val text: String,
    val startTime: Double,
    val endTime: Double,
    val hasTrailingSpace: Boolean,
)