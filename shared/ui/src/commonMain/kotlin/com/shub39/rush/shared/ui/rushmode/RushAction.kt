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
package com.shub39.rush.shared.ui.rushmode

sealed interface RushAction {
    /** Flips the persisted rush mode preference. */
    data object Toggle : RushAction

    data object Enable : RushAction

    data object Disable : RushAction

    /** Retries resolving the track that is currently playing. */
    data object Retry : RushAction

    /** Re-reads the media access permission from the platform. */
    data object RefreshMediaAccess : RushAction

    /** Sends the user to the system settings and polls until access is granted. */
    data object OnRequestMediaAccess : RushAction
}
