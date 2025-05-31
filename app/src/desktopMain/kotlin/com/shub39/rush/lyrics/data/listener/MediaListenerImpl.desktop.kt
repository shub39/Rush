package com.shub39.rush.lyrics.data.listener

import com.shub39.rush.lyrics.domain.MediaInterface
import kotlinx.coroutines.flow.MutableSharedFlow

actual class MediaListenerImpl: MediaInterface {
    override val playbackSpeedFlow: MutableSharedFlow<Float>
        get() = TODO("Not yet implemented")
    override val songInfoFlow: MutableSharedFlow<Pair<String, String>>
        get() = TODO("Not yet implemented")
    override val songPositionFlow: MutableSharedFlow<Long>
        get() = TODO("Not yet implemented")

    override fun destroy() {
        TODO("Not yet implemented")
    }

    override fun seek(timestamp: Long) {
        TODO("Not yet implemented")
    }

    override fun pauseOrResume(resume: Boolean) {
        TODO("Not yet implemented")
    }
}