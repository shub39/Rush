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
package com.shub39.rush.shared.ui.share

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.blend
import com.shub39.rush.shared.core.dataclasses.SongDetails
import com.shub39.rush.shared.core.enums.CardTheme
import com.shub39.rush.shared.ui.LocalWindowSizeClass
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.WindowSize.Companion.isExpanded
import com.shub39.rush.shared.ui.pxToDp
import com.shub39.rush.shared.ui.share.component.cards.AlbumArt
import com.shub39.rush.shared.ui.share.component.cards.BratShareCard
import com.shub39.rush.shared.ui.share.component.cards.CoupletShareCard
import com.shub39.rush.shared.ui.share.component.cards.HypnoticShareCard
import com.shub39.rush.shared.ui.share.component.cards.MessyCard
import com.shub39.rush.shared.ui.share.component.cards.QuoteShareCard
import com.shub39.rush.shared.ui.share.component.cards.RushedShareCard
import com.shub39.rush.shared.ui.share.component.cards.SpotifyShareCard
import com.shub39.rush.shared.ui.share.component.cards.VerticalShareCard
import com.shub39.rush.shared.ui.toMaterialShape
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import rush.shared.ui.generated.resources.*

@Composable
expect fun SharePage(
    onDismiss: () -> Unit,
    state: SharePageState,
    onAction: (SharePageAction) -> Unit,
    onOpenEdit: () -> Unit,
    isEditing: Boolean = false,
)

@Composable expect fun RowScope.ShareButton(onClick: () -> Unit, modifier: Modifier = Modifier)

@Composable
fun SharePageContent(
    state: SharePageState,
    onDismiss: () -> Unit,
    cardGraphicsLayer: GraphicsLayer,
    fullScreenGraphicsLayer: GraphicsLayer,
    selectedImage: PlatformFile?,
    onAction: (SharePageAction) -> Unit,
    onSaveImage: (ImageBitmap) -> Unit,
    onLaunchImagePicker: () -> Unit,
    onShareImage: () -> Unit,
    onOpenEdit: () -> Unit,
    isEditing: Boolean,
) {
    val scope = rememberCoroutineScope()

    var messyCardSeed by remember { mutableLongStateOf(0) }

    val cornerRadius by
        animateDpAsState(
            targetValue =
                when (state.cardRoundness) {
                    DEFAULT -> pxToDp(0)
                    ROUNDED -> pxToDp(32)
                },
            label = "corners",
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        )
    val containerColor by
        animateColorAsState(
            targetValue =
                when (state.cardColors) {
                    MUTED -> Color(state.extractedColors.cardBackgroundMuted)
                    VIBRANT -> Color(state.extractedColors.cardBackgroundDominant)
                    CUSTOM -> Color(state.cardBackground)
                },
            label = "container",
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        )
    val contentColor by
        animateColorAsState(
            targetValue =
                when (state.cardColors) {
                    MUTED -> Color(state.extractedColors.cardContentMuted)
                    VIBRANT -> Color(state.extractedColors.cardContentDominant)
                    CUSTOM -> Color(state.cardContent)
                },
            label = "content",
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        )
    val cardColor =
        CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor)
    val cardCorners = RoundedCornerShape(cornerRadius)

    val cardModifier =
        Modifier.requiredWidth(pxToDp(720))
            .drawWithContent {
                cardGraphicsLayer.record { this@drawWithContent.drawContent() }
                drawLayer(cardGraphicsLayer)
            }
            .heightIn(max = pxToDp(1900))

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_back),
                            contentDescription = "Navigate Back",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            val cardHeight = pxToDp(1920)
            val cardWidth = pxToDp(1080)

            val fitScale = minOf(maxWidth / cardWidth, maxHeight / cardHeight).coerceAtMost(1f)
            val zoomState = rememberZoomState()

            Box(
                modifier = Modifier.fillMaxSize().zoomable(zoomState),
                contentAlignment = Alignment.Center,
            ) {
                Box(modifier = Modifier.size(cardWidth * fitScale, cardHeight * fitScale)) {
                    Surface(
                        modifier =
                            Modifier.wrapContentSize(Alignment.TopStart, unbounded = true)
                                .requiredSize(height = cardHeight, width = cardWidth)
                                .graphicsLayer {
                                    scaleX = fitScale
                                    scaleY = fitScale
                                    transformOrigin = TransformOrigin(0f, 0f)
                                }
                                .drawWithContent {
                                    fullScreenGraphicsLayer.record {
                                        this@drawWithContent.drawContent()
                                    }
                                    drawLayer(fullScreenGraphicsLayer)
                                },
                        color =
                            if (state.fullScreen)
                                containerColor.blend(MaterialTheme.colorScheme.surface)
                            else Color.Transparent,
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            when (state.cardTheme) {
                                SPOTIFY ->
                                    SpotifyShareCard(
                                        modifier = cardModifier,
                                        song = state.songDetails,
                                        sortedLines = state.selectedLines,
                                        cardColors = cardColor,
                                        cardCorners = cardCorners,
                                        albumArtShape = state.albumArtShape.toMaterialShape(),
                                    )

                                RUSHED ->
                                    RushedShareCard(
                                        modifier = cardModifier,
                                        song = state.songDetails,
                                        sortedLines = state.selectedLines,
                                        cardColors = cardColor,
                                        cardCorners = cardCorners,
                                        selectedImage = selectedImage,
                                        albumArtShape = state.albumArtShape.toMaterialShape(),
                                    )

                                HYPNOTIC ->
                                    HypnoticShareCard(
                                        modifier = cardModifier,
                                        song = state.songDetails,
                                        sortedLines = state.selectedLines,
                                        cardColors = cardColor,
                                        cardCorners = cardCorners,
                                        albumArtShape = state.albumArtShape.toMaterialShape(),
                                    )

                                VERTICAL ->
                                    VerticalShareCard(
                                        modifier = cardModifier,
                                        song = state.songDetails,
                                        sortedLines = state.selectedLines,
                                        cardColors = cardColor,
                                        cardCorners = cardCorners,
                                        albumArtShape = state.albumArtShape.toMaterialShape(),
                                    )

                                QUOTE ->
                                    QuoteShareCard(
                                        modifier = cardModifier,
                                        song = state.songDetails,
                                        sortedLines = state.selectedLines,
                                        cardColors = cardColor,
                                        cardCorners = cardCorners,
                                        albumArtShape = state.albumArtShape.toMaterialShape(),
                                    )

                                COUPLET ->
                                    CoupletShareCard(
                                        modifier = cardModifier,
                                        song = state.songDetails,
                                        sortedLines = state.selectedLines,
                                        cardColors = cardColor,
                                        cardCorners = cardCorners,
                                        albumArtShape = state.albumArtShape.toMaterialShape(),
                                    )

                                MESSY ->
                                    MessyCard(
                                        modifier = cardModifier,
                                        song = state.songDetails,
                                        sortedLines = state.selectedLines,
                                        cardColors = cardColor,
                                        cardCorners = cardCorners,
                                        albumArtShape = state.albumArtShape.toMaterialShape(),
                                        seed = messyCardSeed,
                                    )

                                ALBUM_ART ->
                                    AlbumArt(
                                        modifier = cardModifier,
                                        song = state.songDetails,
                                        cardColors = cardColor,
                                        cardCorners = cardCorners,
                                        selectedImage = selectedImage,
                                        albumArtShape = state.albumArtShape.toMaterialShape(),
                                    )

                                BRAT ->
                                    BratShareCard(
                                        modifier = cardModifier,
                                        song = state.songDetails,
                                        sortedLines = state.selectedLines,
                                        cardColors = cardColor,
                                        cardCorners = cardCorners,
                                    )
                            }
                        }
                    }
                }
            }
            val windowSizeClass = LocalWindowSizeClass.current
            AnimatedVisibility(
                visible = !isEditing,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier =
                    Modifier.align(
                            if (!windowSizeClass.isExpanded()) {
                                Alignment.BottomCenter
                            } else {
                                Alignment.BottomEnd
                            }
                        )
                        .padding(32.dp),
            ) {
                HorizontalFloatingToolbar(
                    expanded = true,
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = onOpenEdit,
                            shape = MaterialTheme.shapes.extraLarge,
                            containerColor = MaterialTheme.colorScheme.primary,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.edit),
                                contentDescription = "Edit",
                            )
                        }
                    },
                ) {
                    IconButton(onClick = { onAction(SharePageAction.OnRandomize) }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.dice),
                            contentDescription = null,
                        )
                    }

                    IconButton(
                        onClick = {
                            val graphicsLayer =
                                if (state.fullScreen) fullScreenGraphicsLayer else cardGraphicsLayer
                            scope.launch { onSaveImage(graphicsLayer.toImageBitmap()) }
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.save),
                            contentDescription = "Save",
                        )
                    }

                    ShareButton(onClick = { onShareImage() })

                    AnimatedVisibility(
                        visible = state.cardTheme in listOf(CardTheme.RUSHED, CardTheme.ALBUM_ART),
                        enter = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()),
                        exit = fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()),
                    ) {
                        IconButton(onClick = onLaunchImagePicker) {
                            Icon(
                                painter = painterResource(Res.drawable.image),
                                contentDescription = "Image",
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = state.cardTheme == CardTheme.MESSY,
                        enter = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()),
                        exit = fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()),
                    ) {
                        IconButton(onClick = { messyCardSeed += 1 }) {
                            Icon(
                                painter = painterResource(Res.drawable.refresh),
                                contentDescription = "Image",
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@PreviewWrapper(RushPreviewWrapper::class)
@Preview(
    device = "spec:width=1080px,height=2340px,dpi=480",
    showSystemUi = false,
    showBackground = false,
)
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            SharePageState(
                cardTheme = CardTheme.RUSHED,
                songDetails =
                    SongDetails(title = "Satan in the wait", artist = "Daughters", null, ""),
                selectedLines = (1..5).associateWith { "This is line no $it" },
            )
        )
    }

    SharePageContent(
        state = state,
        onDismiss = {},
        selectedImage = null,
        onAction = {},
        onSaveImage = {},
        onLaunchImagePicker = {},
        onShareImage = {},
        cardGraphicsLayer = rememberGraphicsLayer(),
        fullScreenGraphicsLayer = rememberGraphicsLayer(),
        onOpenEdit = {},
        isEditing = false,
    )
}
