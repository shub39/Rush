package com.shub39.rush.presentation.share

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.shub39.rush.domain.dataclasses.SongDetails
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AlbumArtShape.Companion.toShape
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.enums.CardColors
import com.shub39.rush.domain.enums.CardFit
import com.shub39.rush.domain.enums.CardTheme
import com.shub39.rush.domain.enums.CornerRadius
import com.shub39.rush.presentation.components.ColorPickerDialog
import com.shub39.rush.presentation.components.RushTheme
import com.shub39.rush.presentation.share.component.SharePageSheet
import com.shub39.rush.presentation.share.component.cards.AlbumArt
import com.shub39.rush.presentation.share.component.cards.ChatCard
import com.shub39.rush.presentation.share.component.cards.CoupletShareCard
import com.shub39.rush.presentation.share.component.cards.HypnoticShareCard
import com.shub39.rush.presentation.share.component.cards.MessyCard
import com.shub39.rush.presentation.share.component.cards.QuoteShareCard
import com.shub39.rush.presentation.share.component.cards.RushedShareCard
import com.shub39.rush.presentation.share.component.cards.SpotifyShareCard
import com.shub39.rush.presentation.share.component.cards.VerticalShareCard
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberShareFileLauncher
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharePage(
    onDismiss: () -> Unit,
    state: SharePageState,
    onAction: (SharePageAction) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val cardGraphicsLayer = rememberGraphicsLayer()

    var selectedImage: PlatformFile? by remember { mutableStateOf(null) }
    var saveImage: ImageBitmap? by remember { mutableStateOf(null) }

    val imagePicker = rememberFilePickerLauncher(
        type = FileKitType.Image
    ) { image -> selectedImage = image }

    val imageSaver = rememberFileSaverLauncher { file ->
        if (saveImage != null) {
            coroutineScope.launch(Dispatchers.IO) {
                file?.write(
                    saveImage!!.encodeToByteArray(
                        format = ImageFormat.PNG
                    )
                )
            }
        }
    }

    val shareLauncher = rememberShareFileLauncher()

    SharePageContent(
        state = state,
        onDismiss = onDismiss,
        selectedImage = selectedImage,
        onAction = onAction,
        coroutineScope = coroutineScope,
        cardGraphicsLayer = cardGraphicsLayer,
        onSaveImage = {
            saveImage = it
            imageSaver.launch(
                suggestedName = "${state.songDetails.title} - ${state.songDetails.artist}",
                extension = "png"
            )
        },
        onLaunchImagePicker = { imagePicker.launch() },
        onShareImage = {
            coroutineScope.launch(Dispatchers.IO) {
                val imageBitmap = cardGraphicsLayer.toImageBitmap().asAndroidBitmap()

                val cachePath = File(context.cacheDir, "images")
                cachePath.mkdirs()
                val file = File(cachePath, "image.png")

                val stream = FileOutputStream(file)
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()

                val contentUri: Uri =
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        file
                    )

                shareLauncher.launch(PlatformFile(contentUri))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SharePageContent(
    state: SharePageState,
    onDismiss: () -> Unit,
    cardGraphicsLayer: GraphicsLayer,
    selectedImage: PlatformFile?,
    onAction: (SharePageAction) -> Unit,
    coroutineScope: CoroutineScope,
    onSaveImage: (ImageBitmap) -> Unit,
    onLaunchImagePicker: () -> Unit,
    onShareImage: () -> Unit
) {
    val zoomState = rememberZoomState(initialScale = 1.5f)
    var editSheet by remember { mutableStateOf(false) }
    var colorPicker by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf("content") }



    val cornerRadius by animateDpAsState(
        targetValue = when (state.cardRoundness) {
            CornerRadius.DEFAULT -> pxToDp(0)
            CornerRadius.ROUNDED -> pxToDp(32)
        }, label = "corners"
    )
    val containerColor by animateColorAsState(
        targetValue = when (state.cardColors) {
            CardColors.MUTED -> Color(state.extractedColors.cardBackgroundMuted)
            CardColors.VIBRANT -> Color(state.extractedColors.cardBackgroundDominant)
            CardColors.CUSTOM -> Color(state.cardBackground)
        }, label = "container"
    )
    val contentColor by animateColorAsState(
        targetValue = when (state.cardColors) {
            CardColors.MUTED -> Color(state.extractedColors.cardContentMuted)
            CardColors.VIBRANT -> Color(state.extractedColors.cardContentDominant)
            CardColors.CUSTOM -> Color(state.cardContent)
        }, label = "content"
    )
    val cardColor = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
    val cardCorners = RoundedCornerShape(cornerRadius)

    val modifier = Modifier
        .width(pxToDp(720))
        .zoomable(zoomState = zoomState)
        .drawWithContent {
            cardGraphicsLayer.record {
                this@drawWithContent.drawContent()
            }
            drawLayer(cardGraphicsLayer)
        }
        .padding(pxToDp(32))
        .let {
            if (state.cardFit == CardFit.FIT) {
                it.heightIn(max = pxToDp(1920))
            } else it.height(pxToDp(1280))
        }

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
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Navigate Back",
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
            RushTheme(
                theme = Theme(font = state.cardFont)
            ) {
                when (state.cardTheme) {
                    CardTheme.SPOTIFY -> SpotifyShareCard(
                        modifier = modifier,
                        song = state.songDetails,
                        sortedLines = state.selectedLines,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        fit = state.cardFit,
                        albumArtShape = state.albumArtShape.toShape(),
                        rushBranding = state.rushBranding
                    )

                    CardTheme.RUSHED -> RushedShareCard(
                        modifier = modifier,
                        song = state.songDetails,
                        sortedLines = state.selectedLines,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        selectedImage = selectedImage,
                        albumArtShape = state.albumArtShape.toShape(),
                        rushBranding = state.rushBranding
                    )

                    CardTheme.HYPNOTIC -> HypnoticShareCard(
                        modifier = modifier,
                        song = state.songDetails,
                        sortedLines = state.selectedLines,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        fit = state.cardFit,
                        albumArtShape = state.albumArtShape.toShape(),
                        rushBranding = state.rushBranding
                    )

                    CardTheme.VERTICAL -> VerticalShareCard(
                        modifier = modifier,
                        song = state.songDetails,
                        sortedLines = state.selectedLines,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        fit = state.cardFit,
                        albumArtShape = state.albumArtShape.toShape(),
                        rushBranding = state.rushBranding
                    )

                    CardTheme.QUOTE -> QuoteShareCard(
                        modifier = modifier,
                        song = state.songDetails,
                        sortedLines = state.selectedLines,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        fit = state.cardFit,
                        albumArtShape = state.albumArtShape.toShape(),
                        rushBranding = state.rushBranding
                    )

                    CardTheme.COUPLET -> CoupletShareCard(
                        modifier = modifier,
                        song = state.songDetails,
                        sortedLines = state.selectedLines,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        fit = state.cardFit,
                        albumArtShape = state.albumArtShape.toShape(),
                        rushBranding = state.rushBranding
                    )

                    CardTheme.MESSY -> MessyCard(
                        modifier = modifier,
                        song = state.songDetails,
                        sortedLines = state.selectedLines,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        fit = state.cardFit,
                        albumArtShape = state.albumArtShape.toShape(),
                        rushBranding = state.rushBranding
                    )

                    CardTheme.CHAT -> ChatCard(
                        modifier = modifier,
                        song = state.songDetails,
                        sortedLines = state.selectedLines,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        fit = state.cardFit,
                        albumArtShape = state.albumArtShape.toShape(),
                        rushBranding = state.rushBranding
                    )

                    CardTheme.ALBUM_ART -> AlbumArt(
                        modifier = modifier,
                        song = state.songDetails,
                        cardColors = cardColor,
                        cardCorners = cardCorners,
                        fit = state.cardFit,
                        selectedImage = selectedImage,
                        albumArtShape = state.albumArtShape.toShape(),
                        rushBranding = state.rushBranding
                    )
                }
            }

            HorizontalFloatingToolbar(
                expanded = true,
                floatingActionButton = {
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
                IconButton(
                    onClick = {
                        if (state.isProUser || !CardTheme.premiumCards.contains(state.cardTheme)) {
                            coroutineScope.launch {
                                onSaveImage(cardGraphicsLayer.toImageBitmap())
                            }
                        } else {
                            onAction(SharePageAction.OnShowPaywall)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Download,
                        contentDescription = "Save",
                    )
                }

                IconButton(
                    onClick = {
                        if (state.isProUser || !CardTheme.premiumCards.contains(state.cardTheme)) {
                            onShareImage()
                        } else {
                            onAction(SharePageAction.OnShowPaywall)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(24.dp)
                    )
                }

                AnimatedVisibility(
                    visible = state.cardTheme in listOf(CardTheme.RUSHED, CardTheme.ALBUM_ART)
                ) {
                    IconButton(
                        onClick = onLaunchImagePicker
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Image,
                            contentDescription = "Image",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }

    if (editSheet) {
        SharePageSheet(
            state = state,
            onAction = onAction,
            onDismissRequest = { editSheet = false },
            onLaunchColorPicker = {
                editTarget = it
                colorPicker = true
            },
        )
    }

    if (colorPicker) {
        ColorPickerDialog(
            initialColor = if (editTarget == "content") Color(state.cardContent) else Color(state.cardBackground),
            onSelect = {
                if (editTarget == "content") {
                    onAction(SharePageAction.OnUpdateCardContent(it.toArgb()))
                } else {
                    onAction(SharePageAction.OnUpdateCardBackground(it.toArgb()))
                }
            },
            onDismiss = {
                colorPicker = false
            }
        )
    }
}

@Preview(device = "spec:width=1080px,height=2340px,dpi=480", showSystemUi = false,
    showBackground = false
)
@Composable
private fun Preview() {
    var state by remember { mutableStateOf(
        SharePageState(
            cardTheme = CardTheme.RUSHED,
            songDetails = SongDetails(
                title = "Satan in the wait",
                artist = "Daughters",
                null, ""
            ),
            selectedLines = (1..5).associateWith {
                "This is line no $it"
            }
        )
    ) }

    RushTheme(
        theme = Theme(
            appTheme = AppTheme.DARK,
        )
    ) {
        SharePageContent(
            state = state,
            onDismiss = { },
            selectedImage = null,
            onAction = {},
            coroutineScope = rememberCoroutineScope(),
            onSaveImage = { },
            onLaunchImagePicker = {  },
            onShareImage = {  },
            cardGraphicsLayer = rememberGraphicsLayer()
        )
    }
}