package com.shub39.rush.presentation.share.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AlbumArtShape
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.enums.CardColors
import com.shub39.rush.domain.enums.CardFit
import com.shub39.rush.domain.enums.CardTheme
import com.shub39.rush.domain.enums.CornerRadius
import com.shub39.rush.domain.enums.Fonts
import com.shub39.rush.presentation.components.ListSelect
import com.shub39.rush.presentation.components.RushTheme
import com.shub39.rush.presentation.share.SharePageAction
import com.shub39.rush.presentation.share.SharePageState
import com.shub39.rush.presentation.toFontRes
import com.shub39.rush.presentation.toFullName
import com.shub39.rush.presentation.toShape
import com.shub39.rush.presentation.toStringRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePageSheet(
    state: SharePageState,
    onAction: (SharePageAction) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onLaunchColorPicker: (String) -> Unit,
    isProUser: Boolean,
    onShowPaywall: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        HorizontalDivider()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ListSelect(
                    title = stringResource(R.string.card_theme),
                    options = CardTheme.entries.toList(),
                    selected = state.cardTheme,
                    onSelectedChange = {
                        onAction(SharePageAction.OnUpdateCardTheme(it))
                    },
                    labelProvider = {
                        Text(
                            text = stringResource(it.toStringRes())
                        )
                    }
                )
            }

            item {
                ListSelect(
                    title = stringResource(R.string.card_color),
                    options = CardColors.entries.toList(),
                    selected = state.cardColors,
                    onSelectedChange = {
                        onAction(SharePageAction.OnUpdateCardColor(it))
                    },
                    labelProvider = {
                        Text(
                            text = stringResource(it.toStringRes())
                        )
                    }
                )

                AnimatedVisibility(
                    visible = state.cardColors == CardColors.CUSTOM,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 32.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { onLaunchColorPicker("content") },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(state.cardContent),
                                contentColor = Color(state.cardBackground)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.edit),
                                contentDescription = "Select Color",
                            )
                        }

                        IconButton(
                            onClick = { onLaunchColorPicker("background") },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(state.cardBackground),
                                contentColor = Color(state.cardContent)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.edit),
                                contentDescription = "Select Color"
                            )
                        }
                    }
                }
            }

            item {
                ListSelect(
                    title = stringResource(R.string.card_size),
                    options = CardFit.entries.toList(),
                    selected = state.cardFit,
                    onSelectedChange = {
                        onAction(SharePageAction.OnUpdateCardFit(it))
                    },
                    labelProvider = {
                        Text(
                            text = stringResource(it.toStringRes())
                        )
                    }
                )
            }

            item {
                ListSelect(
                    title = stringResource(R.string.card_corners),
                    options = CornerRadius.entries.toList(),
                    selected = state.cardRoundness,
                    onSelectedChange = {
                        onAction(SharePageAction.OnUpdateCardRoundness(it))
                    },
                    labelProvider = {
                        Text(
                            text = stringResource(it.toStringRes())
                        )
                    }
                )
            }

            item {
                ListSelect(
                    title = stringResource(R.string.album_art_shape),
                    options = AlbumArtShape.entries.toList(),
                    selected = state.albumArtShape,
                    onSelectedChange = {
                        onAction(SharePageAction.OnUpdateAlbumArtShape(it))
                    },
                    labelProvider = {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = it.toShape()
                                )
                        )
                    }
                )
            }

            item {
                ListSelect(
                    title = stringResource(R.string.card_font),
                    options = Fonts.entries.toList(),
                    selected = state.cardFont,
                    onSelectedChange = {
                        onAction(SharePageAction.OnUpdateCardFont(it))
                    },
                    labelProvider = { font ->
                        Text(
                            text = font.toFullName(),
                            fontFamily = font.toFontRes()?.let { FontFamily(Font(it)) } ?: FontFamily.Default
                        )
                    }
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Show Rush Branding",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        modifier = Modifier
                            .basicMarquee()
                            .weight(1f)
                    )

                    Switch(
                        checked = state.rushBranding,
                        onCheckedChange = {
                            if (isProUser) {
                                onAction(SharePageAction.OnToggleRushBranding(it))
                            } else {
                                onShowPaywall()
                            }
                        }
                    )
                }
            }
        }
        HorizontalDivider()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview() {
    RushTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        SharePageSheet(
            state = SharePageState(
                cardColors = CardColors.CUSTOM
            ),
            onAction = { },
            onDismissRequest = { },
            sheetState = rememberStandardBottomSheetState(),
            onLaunchColorPicker = {},
            isProUser = true,
            onShowPaywall = {},
        )
    }
}