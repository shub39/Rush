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
package com.shub39.rush.shared.logic.listener

import com.shub39.rush.shared.core.interfaces.MediaListener
import kotlinx.coroutines.flow.MutableSharedFlow

// stub for now
actual object MediaListenerImpl : MediaListener {
    override val playbackSpeedFlow: MutableSharedFlow<Float> = MutableSharedFlow()
    override val songInfoFlow: MutableSharedFlow<Pair<String, String>> = MutableSharedFlow()
    override val songPositionFlow: MutableSharedFlow<Long> = MutableSharedFlow()

    override fun startListening(context: Any?) {}

    override fun onSeekEagerly() {}

    override fun seek(timeStamp: Long) {}

    override fun pauseOrResume(resume: Boolean) {}

    override fun playNext() {}

    override fun playPrevious() {}
}
