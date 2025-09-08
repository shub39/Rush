package com.shub39.rush.share.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.AlbumArtShape
import com.shub39.rush.core.domain.enums.AlbumArtShape.Companion.toShape
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.domain.enums.CardColors
import com.shub39.rush.core.domain.enums.CardFit
import com.shub39.rush.core.domain.enums.CardTheme
import com.shub39.rush.core.domain.enums.CornerRadius
import com.shub39.rush.core.domain.enums.Fonts
import com.shub39.rush.core.presentation.ListSelect
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.share.SharePageAction
import com.shub39.rush.share.SharePageState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePageSheet(
    state: SharePageState,
    onAction: (SharePageAction) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
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
                            text = stringResource(it.stringRes)
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
                            text = stringResource(it.stringRes)
                        )
                    }
                )
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
                            text = stringResource(it.stringRes)
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
                            text = stringResource(it.stringRes)
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
                    labelProvider = {
                        Text(
                            text = it.fullName,
                            fontFamily = FontFamily(Font(it.font))
                        )
                    }
                )
            }
        }
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
            state = SharePageState(),
            onAction = { },
            onDismissRequest = { },
            sheetState = rememberStandardBottomSheetState()
        )
    }
}