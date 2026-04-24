package dev.gardien.app.capture

import android.content.Context
import dev.gardien.app.BuildConfig

object SyntheticCaptureHarness {
    private const val SAFE_SYNTHETIC_MESSAGE = "synthetic benign fixture message"

    fun injectSample(context: Context) {
        check(BuildConfig.DEBUG) { "Synthetic capture harness is debug-only." }

        CaptureSubmission.submit(
            context,
            CaptureNormalizer.fromText(
                sourcePackage = "dev.gardien.synthetic",
                captureType = CaptureType.SYNTHETIC,
                text = SAFE_SYNTHETIC_MESSAGE,
                synthetic = true,
            ),
        )
    }
}
