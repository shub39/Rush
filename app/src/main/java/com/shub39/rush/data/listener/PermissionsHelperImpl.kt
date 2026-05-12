package com.shub39.rush.data.listener

import android.content.Context
import com.shub39.rush.domain.PermissionsHelper

class PermissionsHelperImpl(
    private val context: Context
) : PermissionsHelper {
    override fun hasNotificationAccess(): Boolean {
        return NotificationListener.canAccessNotifications(context)
    }
}