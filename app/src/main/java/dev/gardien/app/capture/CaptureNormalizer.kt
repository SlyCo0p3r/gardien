package dev.gardien.app.capture

import java.util.UUID

object CaptureNormalizer {
    fun fromText(
        sourcePackage: String,
        captureType: CaptureType,
        text: String,
        capturedAtEpochMillis: Long = System.currentTimeMillis(),
        synthetic: Boolean = false,
    ): CapturedEvent {
        val normalized = text.trim()
        return CapturedEvent(
            id = UUID.randomUUID().toString(),
            sourcePackage = sourcePackage,
            captureType = captureType,
            capturedAtEpochMillis = capturedAtEpochMillis,
            expiresAtEpochMillis = capturedAtEpochMillis + CapturedEvent.RETENTION_MILLIS,
            contentLength = normalized.length,
            synthetic = synthetic,
            payload = normalized.ifBlank { null },
        )
    }
}
