package com.shub39.rush.share

import androidx.activity.compose.BackHandler
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
import com.shub39.rush.core.data.RushDatastore
import com.shub39.rush.share.component.ListSelect
import com.shub39.rush.share.component.RushedShareCard
import com.shub39.rush.share.component.SpotifyShareCard
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePage(
    onDismiss: () -> Unit,
    state: SharePageState,
    paddingValues: PaddingValues,
    datastore: RushDatastore = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    val cardGraphicsLayer = rememberGraphicsLayer()
    val colorPickerController = rememberColorPickerController()
    val context = LocalContext.current

    val cardFitFlow = remember { datastore.getCardFitFlow() }
    val cardThemeFlow = remember { datastore.getCardThemeFlow() }
    val cardColorFlow = remember { datastore.getCardColorFlow() }
    val cardCornersFlow = remember { datastore.getCardRoundnessFlow() }
    val mutableCardContent = remember { datastore.getCardContentFlow() }
    val mutableCardBackground = remember { datastore.getCardBackgroundFlow() }

    val cardFit by cardFitFlow.collectAsState(initial = CardFit.FIT.type)
    val cardTheme by cardThemeFlow.collectAsState(initial = CardTheme.RUSHED.type)
    val cardColorType by cardColorFlow.collectAsState(initial = CardColors.MUTED.color)
    val cardCornersType by cardCornersFlow.collectAsState(initial = CornerRadius.DEFAULT.type)
    val mCardContent by mutableCardContent.collectAsState(initial = Color.White.toArgb())
    val mCardBackground by mutableCardBackground.collectAsState(initial = Color.Black.toArgb())

    var namePicker by remember { mutableStateOf(false) }
    var editSheet by remember { mutableStateOf(false) }
    var colorPicker by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf("content") }

    val modifier = if (cardFit == CardFit.FIT.type) {
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
        targetValue = when (cardCornersType) {
            CornerRadius.DEFAULT.type -> 0.dp
            CornerRadius.ROUNDED.type -> 16.dp
            else -> 0.dp
        }, label = "corners"
    )
    val containerColor by animateColorAsState(
        targetValue = when (cardColorType) {
            CardColors.MUTED.color -> state.extractedColors.cardBackgroundMuted
            CardColors.VIBRANT.color -> state.extractedColors.cardBackgroundDominant
            CardColors.CUSTOM.color -> Color(mCardBackground)
            else -> MaterialTheme.colorScheme.primaryContainer
        }, label = "container"
    )
    val contentColor by animateColorAsState(
        targetValue = when (cardColorType) {
            CardColors.MUTED.color -> state.extractedColors.cardContentMuted
            CardColors.VIBRANT.color -> state.extractedColors.cardContentDominant
            CardColors.CUSTOM.color -> Color(mCardContent)
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
            targetState = cardTheme, label = "cardTheme"
        ) {
            when (it) {
                CardTheme.SPOTIFY.type -> SpotifyShareCard(
                    modifier = modifier,
                    song = state.songDetails,
                    sortedLines = state.selectedLines,
                    cardColors = cardColor,
                    cardCorners = cardCorners,
                    fit = cardFit
                )

                CardTheme.RUSHED.type -> RushedShareCard(
                    modifier = modifier,
                    song = state.songDetails,
                    sortedLines = state.selectedLines,
                    cardColors = cardColor,
                    cardCorners = cardCorners
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            AnimatedVisibility(
                visible = cardColorType == CardColors.CUSTOM.color
            ) {
                Row {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                editTarget = "content"
                                colorPicker = true
                            }
                        },
                        containerColor = Color(mCardContent),
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
                        containerColor = Color(mCardBackground),
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
                    selected = cardTheme,
                    onSelectedChange = {
                        coroutineScope.launch {
                            datastore.updateCardTheme(it)
                        }
                    }
                )

                ListSelect(
                    title = stringResource(R.string.card_color),
                    options = CardColors.entries.map { it.color }.toList(),
                    selected = cardColorType,
                    onSelectedChange = {
                        coroutineScope.launch {
                            datastore.updateCardColor(it)
                        }
                    }
                )

                ListSelect(
                    title = stringResource(R.string.card_size),
                    options = CardFit.entries.map { it.type }.toList(),
                    selected = cardFit,
                    onSelectedChange = {
                        coroutineScope.launch {
                            datastore.updateCardFit(it)
                        }
                    }
                )

                ListSelect(
                    title = stringResource(R.string.card_corners),
                    options = CornerRadius.entries.map { it.type }.toList(),
                    selected = cardCornersType,
                    onSelectedChange = {
                        coroutineScope.launch {
                            datastore.updateCardRoundness(it)
                        }
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
                        initialColor = if (editTarget == "content") Color(mCardContent) else Color(
                            mCardBackground
                        ),
                        controller = colorPickerController
                    )

                    BrightnessSlider(
                        modifier = Modifier
                            .padding(10.dp)
                            .height(35.dp),
                        initialColor = if (editTarget == "content") Color(mCardContent) else Color(
                            mCardBackground
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
                            coroutineScope.launch {
                                if (editTarget == "content") {
                                    datastore.updateCardContent(
                                        colorPickerController.selectedColor.value.toArgb()
                                    )
                                } else {
                                    datastore.updateCardBackground(
                                        colorPickerController.selectedColor.value.toArgb()
                                    )
                                }
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