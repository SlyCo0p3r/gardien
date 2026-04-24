package dev.gardien.app.capture

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CaptureNormalizerTest {
    @Test
    fun `normalizes text capture into metadata and payload`() {
        val event = CaptureNormalizer.fromText(
            sourcePackage = "com.whatsapp",
            captureType = CaptureType.NOTIFICATION,
            text = "  safe synthetic text  ",
            capturedAtEpochMillis = 1_000L,
        )

        assertEquals("com.whatsapp", event.sourcePackage)
        assertEquals(CaptureType.NOTIFICATION, event.captureType)
        assertEquals("safe synthetic text".length, event.contentLength)
        assertEquals("safe synthetic text", event.payload)
        assertFalse(event.synthetic)
        assertEquals(1_000L + CapturedEvent.RETENTION_MILLIS, event.expiresAtEpochMillis)
    }

    @Test
    fun `metadata log line never includes payload`() {
        val event = CaptureNormalizer.fromText(
            sourcePackage = "dev.gardien.synthetic",
            captureType = CaptureType.SYNTHETIC,
            text = "payload must not appear",
            capturedAtEpochMillis = 2_000L,
            synthetic = true,
        )

        assertTrue(event.toMetadataLogLine().contains("source=dev.gardien.synthetic"))
        assertTrue(event.toMetadataLogLine().contains("length=23"))
        assertFalse(event.toMetadataLogLine().contains("payload must not appear"))
    }
}
