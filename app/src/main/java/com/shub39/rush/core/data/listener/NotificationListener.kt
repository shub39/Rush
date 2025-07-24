package com.shub39.rush.core.data.listener

import android.content.Context
import android.service.notification.NotificationListenerService
import androidx.core.app.NotificationManagerCompat

class NotificationListener : NotificationListenerService() {

    companion object {
        fun canAccessNotifications(context: Context): Boolean {
            return NotificationManagerCompat.getEnabledListenerPackages(context)
                .contains(context.packageName)
        }
    }

}