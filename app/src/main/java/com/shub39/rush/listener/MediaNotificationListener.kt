package com.shub39.rush.listener

import android.app.Notification
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.shub39.rush.database.SettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MediaNotificationListener: NotificationListenerService() {

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification= sbn?.notification
        if (notification != null) {
            if (notification.extras.getString(Notification.EXTRA_TEMPLATE) == "android.app.Notification\$MediaStyle") {
                val title = notification.extras.getString(Notification.EXTRA_TITLE) ?: ""
                receiverScope.launch {
                    SettingsDataStore.updateCurrentPlayingSong(this@MediaNotificationListener, title)
                    Log.d("NotificationListener", "Song Title: $title")
                }
            }
        }
    }

    companion object {
        fun canAccessNotifications(context: Context): Boolean {
            return NotificationManagerCompat.getEnabledListenerPackages(context)
                .contains(context.packageName)
        }
    }

}