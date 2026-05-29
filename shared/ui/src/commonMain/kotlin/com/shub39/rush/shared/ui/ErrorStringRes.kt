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
package com.shub39.rush.shared.ui

import com.shub39.rush.shared.core.Error
import com.shub39.rush.shared.core.SourceError
import org.jetbrains.compose.resources.StringResource
import rush.shared.ui.generated.resources.*

fun errorStringRes(error: Error): StringResource {
    return when (error) {
        SourceError.Data.NO_RESULTS -> Res.string.no_results
        SourceError.Data.PARSE_ERROR -> Res.string.parse_error
        SourceError.Data.IO_ERROR -> Res.string.io_error
        SourceError.Data.UNKNOWN -> Res.string.unknown_error
        SourceError.Network.NO_INTERNET -> Res.string.no_internet
        SourceError.Network.REQUEST_FAILED -> Res.string.request_failed
    }
}
