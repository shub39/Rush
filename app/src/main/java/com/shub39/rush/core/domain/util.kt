package com.shub39.rush.core.domain

import android.content.Context
import android.content.Intent
import android.net.Uri

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

fun getMainArtist(artists: String): String {
    return artists.split(",")[0].trim()
}

fun getMainTitle(songTitle: String): String {
    val regex = Regex("\\s*\\(.*?\\)\\s*$")
    return songTitle.replace(regex, "").trim()
}