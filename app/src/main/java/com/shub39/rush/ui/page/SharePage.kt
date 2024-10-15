package com.shub39.rush.ui.page

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.shub39.rush.R
import com.shub39.rush.ui.component.RushedShareCard
import com.shub39.rush.ui.component.SpotifyShareCard
import com.shub39.rush.database.SettingsDataStore
import com.shub39.rush.logic.UILogic.isValidFilename
import com.shub39.rush.logic.UILogic.shareImage
import com.shub39.rush.logic.UILogic.sortMapByKeys
import com.shub39.rush.viewmodel.RushViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePage(
    onDismiss: () -> Unit,
    rushViewModel: RushViewModel,
    imageLoader: ImageLoader = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    val cardGraphicsLayer = rememberGraphicsLayer()
    val colorPicker = rememberColorPickerController()
    val context = LocalContext.current
    val song = rushViewModel.currentSong.collectAsState().value!!
    val selectedLines = rushViewModel.shareLines.collectAsState().value
    val sortedLines = sortMapByKeys(selectedLines)

    val cardThemeFlow = remember { SettingsDataStore.getCardThemeFlow(context) }
    val cardColorFlow = remember { SettingsDataStore.getCardColorFlow(context) }
    val cardCornersFlow = remember { SettingsDataStore.getCardRoundnessFlow(context) }
    val mutableCardContent = remember { SettingsDataStore.getCardContentFlow(context) }
    val mutableCardBackground = remember { SettingsDataStore.getCardBackgroundFlow(context) }
    val cardTheme by cardThemeFlow.collectAsState(initial = "Default")
    val cardColorType by cardColorFlow.collectAsState(initial = "")
    val cardCornersType by cardCornersFlow.collectAsState(initial = "")
    val mCardContent by mutableCardContent.collectAsState(initial = Color.White.toArgb())
    val mCardBackground by mutableCardBackground.collectAsState(initial = Color.Black.toArgb())

    var cardBackgroundDominant by remember { mutableStateOf(Color.DarkGray) }
    var cardContentDominant by remember { mutableStateOf(Color.White) }
    var cardBackgroundMuted by remember { mutableStateOf(Color.DarkGray) }
    var cardContentMuted by remember { mutableStateOf(Color.LightGray) }

    var namePicker by remember { mutableStateOf(false) }
    var colorPickerOpen by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf("") }

    val modifier = Modifier
        .height(640.dp)
        .width(360.dp)
        .drawWithContent {
            cardGraphicsLayer.record {
                this@drawWithContent.drawContent()
            }
            drawLayer(cardGraphicsLayer)
        }

    LaunchedEffect(song) {
        val request = ImageRequest.Builder(context)
            .data(song.artUrl)
            .allowHardware(false)
            .build()
        val result = (imageLoader.execute(request) as? SuccessResult)?.drawable

        result.let { drawable ->
            if (drawable != null) {
                Palette.from(drawable.toBitmap()).generate { palette ->
                    palette?.let {
                        cardBackgroundDominant =
                            Color(
                                it.vibrantSwatch?.rgb ?: it.lightVibrantSwatch?.rgb
                                ?: it.darkVibrantSwatch?.rgb ?: it.dominantSwatch?.rgb
                                ?: Color.DarkGray.toArgb()
                            )
                        cardContentDominant =
                            Color(
                                it.vibrantSwatch?.bodyTextColor
                                    ?: it.lightVibrantSwatch?.bodyTextColor
                                    ?: it.darkVibrantSwatch?.bodyTextColor
                                    ?: it.dominantSwatch?.bodyTextColor
                                    ?: Color.White.toArgb()
                            )
                        cardBackgroundMuted =
                            Color(
                                it.mutedSwatch?.rgb ?: it.darkMutedSwatch?.rgb
                                ?: it.lightMutedSwatch?.rgb ?: Color.DarkGray.toArgb()
                            )
                        cardContentMuted =
                            Color(
                                it.mutedSwatch?.bodyTextColor ?: it.darkMutedSwatch?.bodyTextColor
                                ?: it.lightMutedSwatch?.bodyTextColor ?: Color.White.toArgb()
                            )
                    }
                }
            }
        }
    }

    val cornerRadius by animateDpAsState(
        targetValue = when (cardCornersType) {
            "Rounded" -> 16.dp
            else -> 0.dp
        }, label = "corners"
    )
    val containerColor by animateColorAsState(
        targetValue = when (cardColorType) {
            "Muted" -> cardBackgroundMuted
            "Vibrant" -> cardBackgroundDominant
            "Custom" -> Color(mCardBackground)
            else -> MaterialTheme.colorScheme.primaryContainer
        }, label = "container"
    )
    val contentColor by animateColorAsState(
        targetValue = when (cardColorType) {
            "Muted" -> cardContentMuted
            "Vibrant" -> cardContentDominant
            "Custom" -> Color(mCardContent)
            else -> MaterialTheme.colorScheme.onPrimaryContainer
        }, label = "content"
    )
    val cardColor = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
    val cardCorners = RoundedCornerShape(cornerRadius)

    BackHandler { onDismiss() }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (cardTheme) {
            "Spotify" -> SpotifyShareCard(
                modifier = modifier,
                song = song,
                sortedLines = sortedLines,
                cardColors = cardColor,
                cardCorners = cardCorners,
            )

            "Rushed" -> RushedShareCard(
                modifier = modifier,
                song = song,
                sortedLines = sortedLines,
                cardColors = cardColor,
                cardCorners = cardCorners
            )
        }

        Spacer(modifier = Modifier.padding(8.dp))

        SingleChoiceSegmentedButtonRow {
            listOf("Spotify", "Rushed").forEachIndexed { index, style ->
                SegmentedButton(
                    label = { Text(text = style) },
                    selected = cardTheme == style,
                    onClick = {
                        coroutineScope.launch {
                            SettingsDataStore.updateCardTheme(context, style)
                        }
                    },
                    shape = when (index) {
                        0 -> RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                        1 -> RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                        else -> RoundedCornerShape(0.dp)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Row {
            AnimatedVisibility(visible = cardColorType == "Custom") {
                Row {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                editTarget = "content"
                                colorPickerOpen = true
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
                                colorPickerOpen = true
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
                onClick = {
                    coroutineScope.launch {
                        namePicker = true
                    }
                },
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
                        shareImage(context, bitmap, "${song.artists}-${song.title}.png")
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
        }
    }

    if (namePicker) {
        BasicAlertDialog(
            onDismissRequest = { namePicker = false }
        ) {
            var name by remember { mutableStateOf("${song.artists}-${song.title}.png") }

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

    if (colorPickerOpen) {
        BasicAlertDialog(
            onDismissRequest = {
                colorPickerOpen = false
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
                        controller = colorPicker
                    )

                    BrightnessSlider(
                        modifier = Modifier
                            .padding(10.dp)
                            .height(35.dp),
                        initialColor = if (editTarget == "content") Color(mCardContent) else Color(
                            mCardBackground
                        ),
                        controller = colorPicker
                    )

                    AlphaTile(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        controller = colorPicker
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (editTarget == "content") {
                                    SettingsDataStore.updateCardContent(
                                        context,
                                        colorPicker.selectedColor.value.toArgb()
                                    )
                                } else {
                                    SettingsDataStore.updateCardBackground(
                                        context,
                                        colorPicker.selectedColor.value.toArgb()
                                    )
                                }
                            }
                            colorPickerOpen = false
                        }
                    ) {
                        Text(
                            text = when (editTarget) {
                                "content" -> "Set Content Color"
                                else -> "Set Background Color"
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

}