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
package com.shub39.rush.presentation.share.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shub39.rush.presentation.share.SharePage
import com.shub39.rush.viewmodels.ShareVM
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShareRoute(isProUser: Boolean, onDismiss: () -> Unit, onShowPaywall: () -> Unit) {
    val shareVM: ShareVM = koinViewModel()
    val shareState by shareVM.state.collectAsStateWithLifecycle()

    SharePage(
        onDismiss = onDismiss,
        state = shareState,
        onAction = shareVM::onAction,
        isProUser = isProUser,
        onShowPaywall = onShowPaywall,
    )
}
