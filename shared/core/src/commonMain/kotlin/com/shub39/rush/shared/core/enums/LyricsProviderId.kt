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
package com.shub39.rush.shared.core.enums

/**
 * Lyrics sources that can be queried by the lyrics cascade.
 *
 * @property displayName Name shown in the UI.
 * @property maxQuality Best lyrics quality this source can return, used to stop the cascade early.
 */
enum class LyricsProviderId(val displayName: String, val maxQuality: LyricsQuality) {
    LRCLIB("LRCLIB", LyricsQuality.LINE),
    NETEASE("NetEase", LyricsQuality.SYLLABLE),
    MUSIXMATCH("Musixmatch", LyricsQuality.SYLLABLE),
    QQ_MUSIC("QQ Music", LyricsQuality.LINE),
}
