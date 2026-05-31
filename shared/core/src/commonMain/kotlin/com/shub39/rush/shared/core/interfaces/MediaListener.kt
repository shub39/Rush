package com.shub39.rush.shared.core.interfaces

import kotlinx.coroutines.flow.MutableSharedFlow

interface MediaListener {
    val playbackSpeedFlow: MutableSharedFlow<Float>
    val songInfoFlow: MutableSharedFlow<Pair<String, String>>
    val songPositionFlow: MutableSharedFlow<Long>

    fun startListening(context: Any?)
    fun onSeekEagerly()
    fun seek(timeStamp: Long)
    fun pauseOrResume(resume: Boolean)
    fun playNext()
    fun playPrevious()
}