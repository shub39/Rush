package com.shub39.rush.share

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.shub39.rush.R
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.domain.CardFit
import com.shub39.rush.core.domain.CardTheme
import com.shub39.rush.core.domain.CornerRadius
import com.shub39.rush.core.data.Settings
import com.shub39.rush.share.component.ImageShareCard
import com.shub39.rush.share.component.ListSelect
import com.shub39.rush.share.component.RushedShareCard
import com.shub39.rush.share.component.SpotifyShareCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePage(
    onDismiss: () -> Unit,
    state: SharePageState,
    action: (SharePageAction) -> Unit,
    paddingValues: PaddingValues,
    settings: Settings
) {
    val coroutineScope = rememberCoroutineScope()
    val cardGraphicsLayer = rememberGraphicsLayer()
    val colorPickerController = rememberColorPickerController()
    val context = LocalContext.current

    var namePicker by remember { mutableStateOf(false) }
    var editSheet by remember { mutableStateOf(false) }
    var colorPicker by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf("content") }
    var selectedUri: Uri? by remember { mutableStateOf(null) }

    val launcher = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        selectedUri = uri
    }

    val modifier = if (settings.cardFit == CardFit.FIT.type) {
        Modifier
            .width(360.dp)
            .drawWithContent {
                cardGraphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(cardGraphicsLayer)
            }
    } else {
        Modifier
            .height(640.dp)
            .width(360.dp)
            .drawWithContent {
                cardGraphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(cardGraphicsLayer)
            }
    }

    val cornerRadius by animateDpAsState(
        targetValue = when (settings.cardRoundness) {
            CornerRadius.DEFAULT.type -> 0.dp
            CornerRadius.ROUNDED.type -> 16.dp
            else -> 0.dp
        }, label = "corners"
    )
    val containerColor by animateColorAsState(
        targetValue = when (settings.cardColor) {
            CardColors.MUTED.color -> state.extractedColors.cardBackgroundMuted
            CardColors.VIBRANT.color -> state.extractedColors.cardBackgroundDominant
            CardColors.CUSTOM.color -> Color(settings.cardBackground)
            else -> MaterialTheme.colorScheme.primaryContainer
        }, label = "container"
    )
    val contentColor by animateColorAsState(
        targetValue = when (settings.cardColor) {
            CardColors.MUTED.color -> state.extractedColors.cardContentMuted
            CardColors.VIBRANT.color -> state.extractedColors.cardContentDominant
            CardColors.CUSTOM.color -> Color(settings.cardContent)
            else -> MaterialTheme.colorScheme.onPrimaryContainer
        }, label = "content"
    )
    val cardColor = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
    val cardCorners = RoundedCornerShape(cornerRadius)

    BackHandler { onDismiss() }

    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = settings.cardTheme, label = "cardTheme"
        ) {
            when (it) {
                CardTheme.SPOTIFY.type -> SpotifyShareCard(
                    modifier = modifier,
                    song = state.songDetails,
                    sortedLines = state.selectedLines,
                    cardColors = cardColor,
                    cardCorners = cardCorners,
                    fit = settings.cardFit
                )

                CardTheme.RUSHED.type -> RushedShareCard(
                    modifier = modifier,
                    song = state.songDetails,
                    sortedLines = state.selectedLines,
                    cardColors = cardColor,
                    cardCorners = cardCorners
                )

                CardTheme.IMAGE.type -> ImageShareCard(
                    modifier = modifier,
                    song = state.songDetails,
                    sortedLines = state.selectedLines,
                    cardColors = cardColor,
                    cardCorners = cardCorners,
                    selectedUri = selectedUri
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            AnimatedVisibility(
                visible = settings.cardColor == CardColors.CUSTOM.color
            ) {
                Row {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                editTarget = "content"
                                colorPicker = true
                            }
                        },
                        containerColor = Color(settings.cardContent),
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
                        containerColor = Color(settings.cardBackground),
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

                        onDismiss()
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
                visible = settings.cardTheme == CardTheme.IMAGE.type
            ) {
                FloatingActionButton(
                    onClick = { launcher.launch(
                        PickVisualMediaRequest(
                            PickVisualMedia.ImageOnly
                        )
                    ) },
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

    if (namePicker) {
        BasicAlertDialog(
            onDismissRequest = { namePicker = false }
        ) {
            var name by remember { mutableStateOf("${state.songDetails.artist}-${state.songDetails.title}.png") }

            Card(shape = RoundedCornerShape(32.dp)) {
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
                    options = CardTheme.entries.map { it.type }.toList(),
                    selected = settings.cardTheme,
                    onSelectedChange = {
                        action(SharePageAction.OnUpdateCardTheme(it))
                    }
                )

                ListSelect(
                    title = stringResource(R.string.card_color),
                    options = CardColors.entries.map { it.color }.toList(),
                    selected = settings.cardColor,
                    onSelectedChange = {
                        action(SharePageAction.OnUpdateCardColor(it))
                    }
                )

                ListSelect(
                    title = stringResource(R.string.card_size),
                    options = CardFit.entries.map { it.type }.toList(),
                    selected = settings.cardFit,
                    onSelectedChange = {
                        action(SharePageAction.OnUpdateCardFit(it))
                    }
                )

                ListSelect(
                    title = stringResource(R.string.card_corners),
                    options = CornerRadius.entries.map { it.type }.toList(),
                    selected = settings.cardRoundness,
                    onSelectedChange = {
                        action(SharePageAction.OnUpdateCardRoundness(it))
                    }
                )
            }
        }
    }

    if (colorPicker) {
        BasicAlertDialog(
            onDismissRequest = {
                colorPicker = false
            }
        ) {
            Card(
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    HsvColorPicker(
                        modifier = Modifier
                            .width(350.dp)
                            .height(300.dp)
                            .padding(10.dp),
                        initialColor = if (editTarget == "content") Color(settings.cardContent) else Color(
                            settings.cardBackground
                        ),
                        controller = colorPickerController
                    )

                    BrightnessSlider(
                        modifier = Modifier
                            .padding(10.dp)
                            .height(35.dp),
                        initialColor = if (editTarget == "content") Color(settings.cardContent) else Color(
                            settings.cardBackground
                        ),
                        controller = colorPickerController
                    )

                    AlphaTile(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        controller = colorPickerController
                    )

                    Button(
                        onClick = {
                            if (editTarget == "content") {
                                action(
                                    SharePageAction.OnUpdateCardContent(
                                        colorPickerController.selectedColor.value.toArgb()
                                    )
                                )
                            } else {
                                action(
                                    SharePageAction.OnUpdateCardBackground(
                                        colorPickerController.selectedColor.value.toArgb()
                                    )
                                )
                            }
                            colorPicker = false
                        }
                    ) {
                        Text(
                            text = "Set $editTarget",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

}