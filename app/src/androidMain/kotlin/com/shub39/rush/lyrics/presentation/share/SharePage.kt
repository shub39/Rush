package com.shub39.rush.lyrics.presentation.share

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.data.Theme
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.domain.CardFit
import com.shub39.rush.core.domain.CardTheme
import com.shub39.rush.core.domain.CornerRadius
import com.shub39.rush.core.domain.Fonts
import com.shub39.rush.core.presentation.ColorPickerDialog
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.core.presentation.RushDialog
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.lyrics.presentation.share.component.CoupletShareCard
import com.shub39.rush.lyrics.presentation.share.component.HypnoticShareCard
import com.shub39.rush.lyrics.presentation.share.component.ListSelect
import com.shub39.rush.lyrics.presentation.share.component.QuoteShareCard
import com.shub39.rush.lyrics.presentation.share.component.RushedShareCard
import com.shub39.rush.lyrics.presentation.share.component.SpotifyShareCard
import com.shub39.rush.lyrics.presentation.share.component.VerticalShareCard
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePage(
    onDismiss: () -> Unit,
    state: SharePageState,
    action: (SharePageAction) -> Unit,
) = PageFill {
    val coroutineScope = rememberCoroutineScope()
    val cardGraphicsLayer = rememberGraphicsLayer()
    val zoomState = rememberZoomState()
    val context = LocalContext.current

    var namePicker by remember { mutableStateOf(false) }
    var editSheet by remember { mutableStateOf(false) }
    var colorPicker by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf("content") }
    var selectedUri: Uri? by remember { mutableStateOf(null) }

    val launcher = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        selectedUri = uri
    }

    val modifier = Modifier
        .width(360.dp)
        .zoomable(zoomState)
        .drawWithContent {
            cardGraphicsLayer.record {
                this@drawWithContent.drawContent()
            }
            drawLayer(cardGraphicsLayer)
        }
        .let {
            if (state.cardFit == CardFit.FIT) {
                it.heightIn(max = 960.dp)
            } else it.height(640.dp)
        }

    val cornerRadius by animateDpAsState(
        targetValue = when (state.cardRoundness) {
            CornerRadius.DEFAULT -> 0.dp
            CornerRadius.ROUNDED -> 16.dp
        }, label = "corners"
    )
    val containerColor by animateColorAsState(
        targetValue = when (state.cardColors) {
            CardColors.MUTED -> state.extractedColors.cardBackgroundMuted
            CardColors.VIBRANT -> state.extractedColors.cardBackgroundDominant
            CardColors.CUSTOM -> Color(state.cardBackground)
        }, label = "container"
    )
    val contentColor by animateColorAsState(
        targetValue = when (state.cardColors) {
            CardColors.MUTED -> state.extractedColors.cardContentMuted
            CardColors.VIBRANT -> state.extractedColors.cardContentDominant
            CardColors.CUSTOM -> Color(state.cardContent)
        }, label = "content"
    )
    val cardColor = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
    val cardCorners = RoundedCornerShape(cornerRadius)

    BackHandler { onDismiss() }

    Scaffold(
        modifier = Modifier.widthIn(max = 500.dp)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            RushTheme(
                state = Theme(fonts = state.cardFont)
            ) {
                when (state.cardTheme) {
                    CardTheme.SPOTIFY -> SpotifyShareCard(
                        modifier = modifier,
                        song = state.songDetails,
                        sortedLines = state.selectedLines,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        fit = state.cardFit
                    )

                    CardTheme.RUSHED -> RushedShareCard(
                        modifier = modifier,
                        song = state.songDetails,
                        sortedLines = state.selectedLines,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        selectedUri = selectedUri
                    )

                    CardTheme.HYPNOTIC -> HypnoticShareCard(
                        modifier = modifier,
                        song = state.songDetails,
                        sortedLines = state.selectedLines,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        fit = state.cardFit
                    )

                    CardTheme.VERTICAL -> VerticalShareCard(
                        modifier = modifier,
                        song = state.songDetails,
                        sortedLines = state.selectedLines,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        fit = state.cardFit
                    )

                    CardTheme.QUOTE -> QuoteShareCard(
                        modifier = modifier,
                        song = state.songDetails,
                        sortedLines = state.selectedLines,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        fit = state.cardFit
                    )

                    CardTheme.COUPLET -> CoupletShareCard(
                        modifier = modifier,
                        song = state.songDetails,
                        sortedLines = state.selectedLines,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        fit = state.cardFit
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                AnimatedVisibility(
                    visible = state.cardColors == CardColors.CUSTOM
                ) {
                    Row {
                        FloatingActionButton(
                            onClick = {
                                coroutineScope.launch {
                                    editTarget = "content"
                                    colorPicker = true
                                }
                            },
                            containerColor = Color(state.cardContent),
                            shape = MaterialTheme.shapes.extraLarge,
                            content = {}
                        )

                        Spacer(modifier = Modifier.padding(4.dp))

                        FloatingActionButton(
                            onClick = {
                                coroutineScope.launch {
                                    editTarget = "background"
                                    colorPicker = true
                                }
                            },
                            containerColor = Color(state.cardBackground),
                            shape = MaterialTheme.shapes.extraLarge,
                            content = {}
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))

                FloatingActionButton(
                    onClick = { namePicker = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_download_done_24),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.padding(4.dp))

                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            val bitmap = cardGraphicsLayer.toImageBitmap().asAndroidBitmap()

                            shareImage(
                                context,
                                bitmap,
                                "${state.songDetails.artist}-${state.songDetails.title}.png"
                            )
                        }
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_share_24),
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.padding(4.dp))

                FloatingActionButton(
                    onClick = { editSheet = true },
                    shape = MaterialTheme.shapes.extraLarge,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        painterResource(R.drawable.baseline_edit_square_24),
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.padding(4.dp))

                AnimatedVisibility(
                    visible = state.cardTheme == CardTheme.RUSHED
                ) {
                    FloatingActionButton(
                        onClick = {
                            launcher.launch(
                                PickVisualMediaRequest(
                                    PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        shape = MaterialTheme.shapes.extraLarge,
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_photo_library_24),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }

    if (namePicker) {
        RushDialog(
            onDismissRequest = { namePicker = false }
        ) {
            var name by remember { mutableStateOf("${state.songDetails.artist}-${state.songDetails.title}.png") }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(32.dp)
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        namePicker = false
                        val bitmap = cardGraphicsLayer.toImageBitmap().asAndroidBitmap()
                        shareImage(context, bitmap, name, true)
                    }
                },
                enabled = isValidFilename(name),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        }
    }

    if (editSheet) {
        ModalBottomSheet(
            onDismissRequest = { editSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ListSelect(
                    title = stringResource(R.string.card_theme),
                    options = CardTheme.entries.toList(),
                    selected = state.cardTheme,
                    onSelectedChange = {
                        action(SharePageAction.OnUpdateCardTheme(it))
                    },
                    labelProvider = {
                        Text(
                            text = stringResource(it.title)
                        )
                    }
                )

                ListSelect(
                    title = stringResource(R.string.card_color),
                    options = CardColors.entries.toList(),
                    selected = state.cardColors,
                    onSelectedChange = {
                        action(SharePageAction.OnUpdateCardColor(it))
                    },
                    labelProvider = {
                        Text(
                            text = stringResource(it.title)
                        )
                    }
                )

                ListSelect(
                    title = stringResource(R.string.card_size),
                    options = CardFit.entries.toList(),
                    selected = state.cardFit,
                    onSelectedChange = {
                        action(SharePageAction.OnUpdateCardFit(it))
                    },
                    labelProvider = {
                        Text(
                            text = stringResource(it.title)
                        )
                    }
                )

                ListSelect(
                    title = stringResource(R.string.card_corners),
                    options = CornerRadius.entries.toList(),
                    selected = state.cardRoundness,
                    onSelectedChange = {
                        action(SharePageAction.OnUpdateCardRoundness(it))
                    },
                    labelProvider = {
                        Text(
                            text = stringResource(it.title)
                        )
                    }
                )

                ListSelect(
                    title = stringResource(R.string.card_font),
                    options = Fonts.entries.toList(),
                    selected = state.cardFont,
                    onSelectedChange = {
                        action(SharePageAction.OnUpdateCardFont(it))
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

    if (colorPicker) {
        ColorPickerDialog(
            initialColor = if (editTarget == "content") Color(state.cardContent) else Color(state.cardBackground),
            onSelect = {
                if (editTarget == "content") {
                    action(SharePageAction.OnUpdateCardContent(it.toArgb()))
                } else {
                    action(SharePageAction.OnUpdateCardBackground(it.toArgb()))
                }
            },
            onDismiss = {
                colorPicker = false
            }
        )
    }
}