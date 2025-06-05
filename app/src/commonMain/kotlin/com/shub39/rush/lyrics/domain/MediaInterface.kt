package com.shub39.rush.lyrics.domain

import kotlinx.coroutines.flow.MutableSharedFlow

interface MediaInterface {
    val songInfoFlow: MutableSharedFlow<Pair<String, String>>
    val songPositionFlow: MutableSharedFlow<Long>
    val playbackSpeedFlow: MutableSharedFlow<Float>

    fun destroy()
    fun startListening()
    fun seek(timestamp: Long)
    fun pauseOrResume(resume: Boolean)
}