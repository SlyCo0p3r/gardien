package dev.gardien.app.capture

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CapturedEventTest {
    @Test
    fun `events expire after twenty four hours`() {
        val event = CaptureNormalizer.fromText(
            sourcePackage = "com.discord",
            captureType = CaptureType.ACCESSIBILITY,
            text = "synthetic fixture",
            capturedAtEpochMillis = 10_000L,
        )

        assertFalse(event.isExpired(10_000L + CapturedEvent.RETENTION_MILLIS - 1L))
        assertTrue(event.isExpired(10_000L + CapturedEvent.RETENTION_MILLIS))
    }
}
