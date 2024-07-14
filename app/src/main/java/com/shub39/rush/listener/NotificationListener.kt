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

class NotificationListener : NotificationListenerService() {

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val notification = sbn?.notification ?: return
        val mediaStyle = notification.extras?.getCharSequence(Notification.EXTRA_TEMPLATE)?.toString()

        if (mediaStyle == Notification.MediaStyle::class.java.name) {
            val title = notification.extras?.getString(Notification.EXTRA_TITLE)
            Log.d("NotificationListener", "Media Notification - Title: $title")
            receiverScope.launch {
                SettingsDataStore.updateCurrentPlayingSong(this@NotificationListener,title ?: "")
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