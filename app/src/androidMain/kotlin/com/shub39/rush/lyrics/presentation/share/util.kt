package com.shub39.rush.lyrics.presentation.share

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun isValidFilename(filename: String): Boolean {
    val invalidCharsPattern = Regex("[/\\\\:*?\"<>|\u0000\r\n]")
    return !invalidCharsPattern.containsMatchIn(filename)
            && filename.length <= 50
            && filename.isNotBlank()
            && filename.isNotEmpty()
            && filename.endsWith(".png")
}

/*
* Converts a given Bitmap into a png image and opens dialog to share the image
* if shareToPictures is True then it stores the image in /Pictures/Rush in internal storage
*/
fun shareImage(context: Context, bitmap: Bitmap, name: String, saveToPictures: Boolean = false) {
    val file: File = if (saveToPictures) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Rush")
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            val stream = resolver.openOutputStream(it)
            if (stream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            stream?.close()
        } ?: run {
            Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show()
            return
        }
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), name)
    } else {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        File(cachePath, name)
    }

    try {
        if (!saveToPictures) {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return
    }

    if (saveToPictures) {
        Toast.makeText(context, "Image saved to Pictures/$name", Toast.LENGTH_SHORT).show()
    } else {
        val contentUri: Uri =
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share image using"))
    }
}

fun getFormattedTime(): String {
    val now = Clock.System.now()
    val localTime = now.toLocalDateTime(TimeZone.currentSystemDefault()).time

    val hour = localTime.hour % 12
    val minute = localTime.minute
    val amPm = if (localTime.hour < 12) "AM" else "PM"

    val hourFormatted = if (hour == 0) 12 else hour
    val minuteFormatted = minute.toString().padStart(2, '0')

    return "$hourFormatted:$minuteFormatted $amPm"
}