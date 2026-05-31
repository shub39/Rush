package com.shub39.rush.shared.logic

import com.shub39.rush.shared.core.interfaces.MediaAccessChecker
import org.koin.core.annotation.Single

@Single(binds = [MediaAccessChecker::class])
actual class MediaAccessCheckerImpl : MediaAccessChecker {
    override fun canAccessMediaInfo(): Boolean = true
}