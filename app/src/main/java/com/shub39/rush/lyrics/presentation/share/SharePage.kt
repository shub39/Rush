package com.shub39.rush.lyrics.presentation.share

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.CardColors
import com.shub39.rush.core.domain.enums.CardFit
import com.shub39.rush.core.domain.enums.CardTheme
import com.shub39.rush.core.domain.enums.CornerRadius
import com.shub39.rush.core.domain.enums.Fonts
import com.shub39.rush.core.presentation.ColorPickerDialog
import com.shub39.rush.core.presentation.PageFill
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.lyrics.presentation.share.component.ChatCard
import com.shub39.rush.lyrics.presentation.share.component.CoupletShareCard
import com.shub39.rush.lyrics.presentation.share.component.HypnoticShareCard
import com.shub39.rush.lyrics.presentation.share.component.ListSelect
import com.shub39.rush.lyrics.presentation.share.component.MessyCard
import com.shub39.rush.lyrics.presentation.share.component.QuoteShareCard
import com.shub39.rush.lyrics.presentation.share.component.RushedShareCard
import com.shub39.rush.lyrics.presentation.share.component.SpotifyShareCard
import com.shub39.rush.lyrics.presentation.share.component.VerticalShareCard
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowLeft
import compose.icons.fontawesomeicons.solid.Download
import compose.icons.fontawesomeicons.solid.Image
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharePage(
    onDismiss: () -> Unit,
    state: SharePageState,
    action: (SharePageAction) -> Unit,
    share: Boolean,
    zoomEnabled: Boolean = true,
    density: Density = Density(2.5f, 1f)
) = PageFill {
    val coroutineScope = rememberCoroutineScope()
    val cardGraphicsLayer = rememberGraphicsLayer()
    val zoomState = rememberZoomState()

    var editSheet by remember { mutableStateOf(false) }
    var colorPicker by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf("content") }
    var selectedImage: PlatformFile? by remember { mutableStateOf(null) }
    var saveImage: ImageBitmap? by remember { mutableStateOf(null) }

    val imagePicker = rememberFilePickerLauncher(
        type = FileKitType.Image
    ) { image -> selectedImage = image }

    val imageSaver = rememberFileSaverLauncher { file ->
        if (saveImage != null) {
            coroutineScope.launch(Dispatchers.IO) {
                file?.write(saveImage!!.encodeToByteArray(
                    format = ImageFormat.PNG
                ))
            }
        }
    }

    val modifier = Modifier
        .width(360.dp)
        .zoomable(
            zoomState = zoomState,
            zoomEnabled = zoomEnabled
        )
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.ArrowLeft,
                            contentDescription = "Navigate Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(
                LocalDensity provides density
            ) {
                RushTheme(
                    state = Theme(fonts = state.cardFont),
                    fontScale = density.fontScale
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
                            selectedImage = selectedImage
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

                        CardTheme.MESSY -> MessyCard(
                            modifier = modifier,
                            song = state.songDetails,
                            sortedLines = state.selectedLines,
                            cardColors = cardColor,
                            cardCorners = cardCorners,
                            fit = state.cardFit
                        )

                        CardTheme.CHAT -> ChatCard(
                            modifier = modifier,
                            song = state.songDetails,
                            sortedLines = state.selectedLines,
                            cardColors = cardColor,
                            cardCorners = cardCorners,
                            fit = state.cardFit
                        )
                    }
                }
            }

            HorizontalFloatingToolbar(
                expanded = true,
                trailingContent = {
                    FloatingActionButton(
                        onClick = { editSheet = true },
                        shape = MaterialTheme.shapes.extraLarge,
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "Edit"
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                AnimatedVisibility(
                    visible = state.cardColors == CardColors.CUSTOM
                ) {
                    Row {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    editTarget = "content"
                                    colorPicker = true
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(state.cardContent)
                            ),
                            shape = MaterialTheme.shapes.extraLarge,
                            content = {}
                        )

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    editTarget = "background"
                                    colorPicker = true
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(state.cardBackground)
                            ),
                            shape = MaterialTheme.shapes.extraLarge,
                            content = {}
                        )
                    }
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            saveImage = cardGraphicsLayer.toImageBitmap()
                            imageSaver.launch(
                                suggestedName = "${state.songDetails.artist}-${state.songDetails.title}",
                                extension = "png"
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Download,
                        contentDescription = "Save",
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (share) {
                    ShareButton(coroutineScope, cardGraphicsLayer)
                }

                AnimatedVisibility(
                    visible = state.cardTheme == CardTheme.RUSHED
                ) {
                    IconButton(
                        onClick = { imagePicker.launch() }
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Image,
                            contentDescription = "Image",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
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
                            text = stringResource(it.stringRes)
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
                            text = stringResource(it.stringRes)
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
                            text = stringResource(it.stringRes)
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
                            text = stringResource(it.stringRes)
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