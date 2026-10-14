/*
 * Copyright (C) 2026  Karelisio
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
package com.karelisio.rushio.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.karelisio.rushio.MainActivity
import com.karelisio.rushio.R

/** The persistent notification of the background lyrics service. */
object BackgroundLyricsNotification {
    const val ID = 39
    private const val OVERLAY_CHANNEL_ID = "background_lyrics"
    private const val LYRICS_CHANNEL_ID = "lyrics_notification"

    fun createChannels(context: Context) {
        val overlay =
            NotificationChannel(
                    OVERLAY_CHANNEL_ID,
                    context.getString(R.string.notification_channel_background),
                    // Low importance: shown without sound, vibration or heads-up
                    NotificationManager.IMPORTANCE_LOW,
                )
                .silent()
                .apply {
                    description = context.getString(R.string.notification_channel_background_desc)
                }
        val lyrics =
            NotificationChannel(
                    LYRICS_CHANNEL_ID,
                    context.getString(R.string.notification_channel_lyrics),
                    // Default importance keeps the lyrics among the visible notifications, every
                    // update stays silent: no sound, no vibration, alerted only once
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
                .silent()
                .apply {
                    description = context.getString(R.string.notification_channel_lyrics_desc)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
        context
            .getSystemService(NotificationManager::class.java)
            .createNotificationChannels(listOf(overlay, lyrics))
    }

    private fun NotificationChannel.silent(): NotificationChannel = apply {
        setSound(null, null)
        enableVibration(false)
        enableLights(false)
        setShowBadge(false)
    }

    /** Notification following the lyrics: current line as title, next line as text. */
    fun lyrics(context: Context, lyrics: NowPlayingLyrics, texts: OverlayTexts): Notification {
        val content = lyricsNotificationContent(lyrics, texts)
        return base(context, LYRICS_CHANNEL_ID)
            .setContentTitle(content.title)
            .setContentText(content.text.ifEmpty { null })
            .setSubText(content.subText.ifEmpty { null })
            // Expanded, long lines are shown entirely
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(content.title)
                    .bigText(content.text.ifEmpty { null })
            )
            .build()
    }

    /** Notification while the floating window shows the lyrics. */
    fun overlay(context: Context, lyrics: NowPlayingLyrics): Notification =
        base(context, OVERLAY_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_overlay_title))
            .setContentText(
                lyrics.title
                    .takeIf { it.isNotBlank() }
                    ?.let { title ->
                        listOf(title, lyrics.artist).filter { it.isNotBlank() }.joinToString(" · ")
                    } ?: context.getString(R.string.overlay_waiting)
            )
            .build()

    private fun base(context: Context, channelId: String): NotificationCompat.Builder =
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.app_icon)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setShowWhen(false)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(openAppIntent(context))
            .addAction(0, context.getString(R.string.notification_stop), stopIntent(context))

    fun openAppIntent(context: Context): PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

    private fun stopIntent(context: Context): PendingIntent =
        PendingIntent.getService(
            context,
            1,
            Intent(context, BackgroundLyricsService::class.java)
                .setAction(BackgroundLyricsService.ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
}
