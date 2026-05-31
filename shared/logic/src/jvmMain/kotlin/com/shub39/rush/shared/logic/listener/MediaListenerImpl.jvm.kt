package com.shub39.rush.shared.logic.listener

import com.shub39.rush.shared.core.interfaces.MediaListener
import kotlinx.coroutines.flow.MutableSharedFlow

// stub for now
actual object MediaListenerImpl : MediaListener {
    override val playbackSpeedFlow: MutableSharedFlow<Float> = MutableSharedFlow()
    override val songInfoFlow: MutableSharedFlow<Pair<String, String>> = MutableSharedFlow()
    override val songPositionFlow: MutableSharedFlow<Long> = MutableSharedFlow()

    override fun startListening(context: Any?) {
    }

    override fun onSeekEagerly() {
    }

    override fun seek(timeStamp: Long) {
    }

    override fun pauseOrResume(resume: Boolean) {
    }

    override fun playNext() {
    }

    override fun playPrevious() {
    }
}