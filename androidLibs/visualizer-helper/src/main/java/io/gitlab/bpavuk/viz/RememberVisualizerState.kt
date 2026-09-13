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
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "VisualizerState"

/**
 * Initializes the [Visualizer] and takes care of Visualizer management. It assumes that you handle
 * the [android.Manifest.permission.RECORD_AUDIO] permission as well as specify the
 * [android.Manifest.permission.MODIFY_AUDIO_SETTINGS] permission in Manifest.
 *
 * Restarts if any of keys change.
 */
private object GlobalVisualizerManager {
    private var visualizer: Visualizer? = null
    var globalState by mutableStateOf<VisualizerState>(VisualizerState.Uninitialized)
    private var activeListenersCount = 0

    fun acquire(): () -> Unit {
        activeListenersCount++
        if (visualizer == null) {
            try {
                val viz = Visualizer(0)
                val samplingRate = Visualizer.getMaxCaptureRate() / 2
                val captureSize = Visualizer.getCaptureSizeRange()[1]
                
                val listener = object : Visualizer.OnDataCaptureListener {
                    override fun onFftDataCapture(v: Visualizer?, fft: ByteArray?, rate: Int) {
                        globalState = if (globalState is VisualizerState.Ready) {
                            (globalState as VisualizerState.Ready).copy(fft = fft?.toList())
                        } else {
                            VisualizerState.Ready(fft = fft?.toList(), waveform = null)
                        }
                    }
                    override fun onWaveFormDataCapture(v: Visualizer?, waveform: ByteArray?, rate: Int) {
                        globalState = if (globalState is VisualizerState.Ready) {
                            (globalState as VisualizerState.Ready).copy(waveform = waveform?.toList())
                        } else {
                            VisualizerState.Ready(waveform = waveform?.toList(), fft = null)
                        }
                    }
                }
                
                viz.captureSize = captureSize
                viz.setDataCaptureListener(listener, samplingRate, true, true)
                viz.enabled = true
                visualizer = viz
                Log.d(TAG, "Global Visualizer successfully initialized")
            } catch (e: UnsupportedOperationException) {
                Log.wtf(TAG, "Device is unsupported!", e)
                globalState = VisualizerState.Unsupported
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing Global Visualizer!", e)
                globalState = VisualizerState.Error(e)
            }
        }
        
        return {
            activeListenersCount--
            if (activeListenersCount <= 0) {
                Log.d(TAG, "Disposing of Global Visualizer")
                visualizer?.enabled = false
                visualizer?.release()
                visualizer = null
                globalState = VisualizerState.Uninitialized
                activeListenersCount = 0
            }
        }
    }
}

/**
 * Initializes the [Visualizer] and takes care of Visualizer management. It assumes that you handle
 * the [android.Manifest.permission.RECORD_AUDIO] permission as well as specify the
 * [android.Manifest.permission.MODIFY_AUDIO_SETTINGS] permission in Manifest.
 *
 * Restarts if any of [keys] change.
 */
@Composable
fun rememberVisualizerState(enabled: Boolean = true, vararg keys: Any?): VisualizerState {
    val inPreview = LocalInspectionMode.current
    var previewState by remember { mutableStateOf<VisualizerState>(VisualizerState.Uninitialized) }

    if (inPreview) {
        LaunchedEffect(enabled, *keys) {
            if (!enabled) {
                previewState = VisualizerState.Uninitialized
                return@LaunchedEffect
            }
            val captureSize = 128
            val waveform = List(captureSize) { 0.toByte() }.toMutableList()
            val fft = List(captureSize) { 0.toByte() }.toMutableList()
            while (true) {
                for (i in 0 until captureSize) {
                    waveform[i] = (-128 + (0..255).random()).toByte()
                    fft[i] = (-128 + (0..255).random()).toByte()
                }
                previewState = VisualizerState.Ready(waveform = waveform.toList(), fft = fft.toList())
                delay(100.milliseconds)
            }
        }
        return previewState
    } else {
        DisposableEffect(enabled, *keys) {
            if (!enabled) {
                return@DisposableEffect onDispose {}
            }
            val release = GlobalVisualizerManager.acquire()
            onDispose {
                release()
            }
        }
        return if (enabled) GlobalVisualizerManager.globalState else VisualizerState.Uninitialized
    }
}

sealed interface VisualizerState {
    data object Unsupported : VisualizerState

    data object Uninitialized : VisualizerState

    data class Error(val e: Exception) : VisualizerState

    data class Ready(val waveform: VisualizerData?, val fft: VisualizerData?) : VisualizerState
}

typealias VisualizerData = List<Byte>
