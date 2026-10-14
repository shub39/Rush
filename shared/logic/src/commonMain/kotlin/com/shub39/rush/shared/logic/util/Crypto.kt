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
package com.shub39.rush.shared.logic.util

/** MD5 digest of [input] as lowercase hex. */
internal expect fun md5Hex(input: ByteArray): String

/** AES/ECB/PKCS5Padding encryption of [input] with a 128 bits [key]. */
internal expect fun aesEcbEncrypt(input: ByteArray, key: ByteArray): ByteArray

internal fun ByteArray.toHex(): String =
    joinToString("") { byte -> (byte.toInt() and 0xFF).toString(16).padStart(2, '0') }
