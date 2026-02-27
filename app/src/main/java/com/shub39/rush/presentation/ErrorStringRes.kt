/*
 * Copyright (C) 2026  Shubham Gorai
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
package com.shub39.rush.presentation

import com.shub39.rush.R
import com.shub39.rush.domain.Error
import com.shub39.rush.domain.SourceError

fun errorStringRes(error: Error): Int {
    return when (error) {
        SourceError.Data.NO_RESULTS -> R.string.no_results
        SourceError.Data.PARSE_ERROR -> R.string.parse_error
        SourceError.Data.IO_ERROR -> R.string.io_error
        SourceError.Data.UNKNOWN -> R.string.unknown_error
        SourceError.Network.NO_INTERNET -> R.string.no_internet
        SourceError.Network.REQUEST_FAILED -> R.string.request_failed
    }
}
