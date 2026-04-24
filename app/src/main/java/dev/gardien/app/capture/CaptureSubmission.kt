package dev.gardien.app.capture

import android.content.Context
import android.util.Log
import dev.gardien.app.storage.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object CaptureSubmission {
    private const val TAG = "GardienCapture"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun submit(context: Context, event: CapturedEvent) {
        if (!event.synthetic && !CaptureSettings.isResearchCaptureEnabled(context)) {
            return
        }

        Log.i(TAG, event.toMetadataLogLine())
        scope.launch {
            AppContainer.from(context).captureRepository.save(event)
        }
    }
}
