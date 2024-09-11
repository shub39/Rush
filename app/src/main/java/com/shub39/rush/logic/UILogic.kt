package com.shub39.rush.logic

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.shub39.rush.database.Lyric
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object UILogic {
    fun breakLyrics(lyrics: String): Map<Int, String> {
        val lines = lyrics.lines()
        val map = mutableMapOf<Int, String>()
        for (i in lines.indices) {
            map[i] = lines[i]
        }
        return map
    }

    fun updateSelectedLines(
        selectedLines: Map<Int, String>,
        key: Int,
        value: String,
        maxSelections: Int = 6
    ): Map<Int, String> {
        return if (!selectedLines.contains(key) && selectedLines.size < maxSelections) {
            selectedLines.plus(key to value)
        } else {
            selectedLines.minus(key)
        }
    }

    fun copyToClipBoard(context: Context, text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    fun parseLyrics(lyricsString: String): List<Lyric> {
        return lyricsString.lines().mapNotNull { line ->
            val parts = line.split("] ")
            if (parts.size == 2) {
                val time = parts[0].removePrefix("[").split(":").let { (minutes, seconds) ->
                    minutes.toLong() * 60 * 1000 + (seconds.toDouble() * 1000).toLong()
                }
                val text = parts[1]
                Lyric(time, text)
            } else {
                null
            }
        }
    }

    fun getCurrentLyricIndex(playbackPosition: Long, lyrics: List<Lyric>): Int {
        return if (lyrics.indexOfLast { it.time <= playbackPosition } < 0) {
            0
        } else {
            lyrics.indexOfLast { it.time <= playbackPosition }
        }
    }

    fun openLinkInBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    fun sortMapByKeys(map: Map<Int, String>): Map<Int, String> {
        val sortedEntries = map.entries.toList().sortedBy { it.key }
        val sortedMap = LinkedHashMap<Int, String>()
        for (entry in sortedEntries) {
            sortedMap[entry.key] = entry.value
        }
        return sortedMap
    }

    fun isValidFilename(filename: String): Boolean {
        val invalidCharsPattern = Regex("[/\\\\:*?\"<>|\u0000\r\n]")
        return !invalidCharsPattern.containsMatchIn(filename)
                && filename.length <= 50
                && filename.isNotBlank()
                && filename.isNotEmpty()
                && filename.endsWith(".png")
    }


    fun shareImage(context: Context, bitmap: Bitmap, name: String, saveToPictures: Boolean = false) {
        val file: File = if (saveToPictures) {
            val picturesDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Rush"
            )
            picturesDir.mkdirs()
            File(picturesDir, name)
        } else {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            File(cachePath, name)
        }

        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }

        if (saveToPictures) {
            MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null, null)
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
}