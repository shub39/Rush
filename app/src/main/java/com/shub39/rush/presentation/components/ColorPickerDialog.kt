package com.shub39.rush.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.shub39.rush.R

/**
 * A composable dialog that allows the user to select a color.
 * to provide a rich color selection experience, including an HSV color picker,
 * a brightness slider, and an alpha tile.
 *
 * @param initialColor The color that is initially selected in the picker.
 * @param onSelect The callback that is invoked when the user confirms their color selection.
 *                 The selected [Color] is passed as an argument.
 * @param onDismiss The callback that is invoked when the user dismisses the dialog
 *                  without making a selection, or after a selection is made.
 * @param modifier The [Modifier] to be applied to the dialog.
 */
@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onSelect: (Color) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val controller = rememberColorPickerController()

    RushDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier.padding(horizontal = 32.dp)
    ) {
        if (!windowSizeClass.isWidthAtLeastBreakpoint(840)) {
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
                        .padding(top = 10.dp),
                    initialColor = initialColor,
                    controller = controller
                )

                BrightnessSlider(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .height(35.dp),
                    initialColor = initialColor,
                    controller = controller
                )

                AlphaTile(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(vertical = 10.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    controller = controller
                )

                Button(
                    onClick = {
                        onSelect(controller.selectedColor.value)
                        onDismiss()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.done),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // landscape ui
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HsvColorPicker(
                    modifier = Modifier
                        .width(250.dp)
                        .height(200.dp),
                    initialColor = initialColor,
                    controller = controller
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BrightnessSlider(
                        modifier = Modifier.height(35.dp),
                        initialColor = initialColor,
                        controller = controller
                    )

                    AlphaTile(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        controller = controller
                    )

                    Button(
                        onClick = {
                            onSelect(controller.selectedColor.value)
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.done),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
