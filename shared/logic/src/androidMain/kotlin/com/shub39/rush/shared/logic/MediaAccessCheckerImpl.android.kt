package com.shub39.rush.shared.logic

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.shub39.rush.shared.core.interfaces.MediaAccessChecker
import org.koin.core.annotation.Single

@Single(binds = [MediaAccessChecker::class])
actual class MediaAccessCheckerImpl(private val context: Context) : MediaAccessChecker {
    override fun canAccessMediaInfo(): Boolean {
        val enabled =
            NotificationManagerCompat.getEnabledListenerPackages(context)
                .contains(context.packageName)

//        if (enabled) MediaListenerImpl.startListening(context) TODO Launch Medialistener

        return enabled
    }
}