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
package com.shub39.rush.billing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.revenuecat.purchases.ui.revenuecatui.Paywall
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions
import com.revenuecat.purchases.ui.revenuecatui.customercenter.CustomerCenter
import com.shub39.rush.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallPage(isProUser: Boolean, onDismissRequest: () -> Unit, modifier: Modifier = Modifier) {
    val paywallOptions = remember {
        PaywallOptions.Builder(dismissRequest = onDismissRequest)
            .setShouldDisplayDismissButton(true)
            .build()
    }

    Box(modifier = modifier) {
        if (!isProUser) {
            Paywall(paywallOptions)
        } else {
            CustomerCenter(onDismiss = onDismissRequest)
        }

        IconButton(
            onClick = onDismissRequest,
            colors =
                IconButtonDefaults.iconButtonColors(
                    contentColor = Color(0xFF282828),
                    containerColor = Color(0xfff2e3b1),
                ),
            modifier =
                Modifier.padding(vertical = 32.dp, horizontal = 16.dp).align(Alignment.TopEnd),
        ) {
            Icon(painter = painterResource(R.drawable.close), contentDescription = "Close")
        }
    }
}
