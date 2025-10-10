package io.gitlab.bpavuk.viz

import android.media.audiofx.Visualizer
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import kotlinx.coroutines.delay

private const val TAG = "VisualizerState"

/**
 * Initializes the [Visualizer] and takes care of Visualizer management.
 * It assumes that you handle the [android.Manifest.permission.RECORD_AUDIO] permission
 * as well as specify the [android.Manifest.permission.MODIFY_AUDIO_SETTINGS] permission in Manifest.
 *
 * Restarts if any of [keys] change.
 */
@Composable
fun rememberVisualizerState(vararg keys: Any?): VisualizerState {
    val inPreview = LocalInspectionMode.current
    var waveData by remember { mutableStateOf<VisualizerState>(VisualizerState.Uninitialized) }
    var visualizer: Visualizer

    if (inPreview) {
        LaunchedEffect(*keys) {
            val captureSize = 128
            val waveform = List(captureSize) { 0.toByte() }.toMutableList()
            val fft = List(captureSize) { 0.toByte() }.toMutableList()
            while (true) {
                for (i in 0 until captureSize) {
                    waveform[i] = (-128 + (0..255).random()).toByte()
                    fft[i] = (-128 + (0..255).random()).toByte()
                }
                waveData = VisualizerState.Ready(
                    waveform = waveform.toList(),
                    fft = fft.toList()
                )
                // A realistic capture rate is way faster, but this is enough for a preview
                delay(100)
            }
        }
    } else {
        DisposableEffect(*keys) {
            try { // some devices straight-up reject the Visualizer API initialization on output mix
                visualizer = Visualizer(0)
            } catch (e: UnsupportedOperationException) {
                Log.wtf(TAG, "Device is unsupported!", e)
                waveData = VisualizerState.Unsupported
                return@DisposableEffect onDispose {  }
            } catch (e: RuntimeException) {
                Log.wtf(TAG, "Something extremely terrible happened!", e)
                waveData = VisualizerState.Unsupported
                return@DisposableEffect onDispose {  }
            }


            val samplingRate = Visualizer.getMaxCaptureRate() / 2 // half of max rate for the sake of performance
            Log.d(TAG, "sampling rate: ${samplingRate / 1000} times per second")
            val captureSize = Visualizer.getCaptureSizeRange()[1]
            Log.d(TAG, "capture size range: $captureSize")
            val listener = object : Visualizer.OnDataCaptureListener {
                override fun onFftDataCapture(
                    visualizer: Visualizer?,
                    fft: ByteArray?,
                    samplingRate: Int
                ) {
                    waveData = if (waveData is VisualizerState.Ready) {
                        (waveData as VisualizerState.Ready).copy(fft = fft?.toList())
                    } else {
                        VisualizerState.Ready(fft = fft?.toList(), waveform = null)
                    }
                }

                override fun onWaveFormDataCapture(
                    visualizer: Visualizer?,
                    waveform: ByteArray?,
                    samplingRate: Int
                ) {
                    waveData = if (waveData is VisualizerState.Ready) {
                        (waveData as VisualizerState.Ready).copy(waveform = waveform?.toList())
                    } else {
                        VisualizerState.Ready(waveform = waveform?.toList(), fft = null)
                    }
                }
            }

            try {
                visualizer.captureSize = captureSize
                visualizer.setDataCaptureListener(
                    listener,
                    /* rate = */ samplingRate,
                    /* waveform = */ true,
                    /* fft = */ true
                )
                visualizer.enabled = true
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing Visualizer!")
                waveData = VisualizerState.Error(e)
                return@DisposableEffect onDispose {  }
            }

            Log.d(TAG, "Visualizer successfully initialized")

            onDispose {
                Log.d(TAG, "Disposing of Visualizer")
                visualizer.enabled = false
                visualizer.release()
            }
        }
    }

    return waveData
}

sealed interface VisualizerState {
    data object Unsupported : VisualizerState
    data object Uninitialized : VisualizerState
    data class Error(val e: Exception) : VisualizerState
    data class Ready(val waveform: VisualizerData?, val fft: VisualizerData?) : VisualizerState
}

typealias VisualizerData = List<Byte>

