package dev.gardien.app.storage

import dev.gardien.app.capture.CaptureNormalizer
import dev.gardien.app.capture.CaptureType
import dev.gardien.app.capture.CapturedEvent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CaptureRepositoryTest {
    @Test
    fun `save writes capture and metadata-only audit log`() = runTest {
        val captureDao = FakeCaptureDao()
        val auditLogDao = FakeAuditLogDao()
        val repository = CaptureRepository(captureDao, auditLogDao)
        val event = CaptureNormalizer.fromText(
            sourcePackage = "com.whatsapp",
            captureType = CaptureType.NOTIFICATION,
            text = "content must stay out of audit logs",
            capturedAtEpochMillis = 1_000L,
        )

        repository.save(event)

        assertEquals(1, captureDao.events.size)
        assertEquals("content must stay out of audit logs", captureDao.events.single().payload)
        assertEquals(1, auditLogDao.entries.size)
        assertFalse(auditLogDao.entries.single().metadata.contains("content must stay out"))
    }

    @Test
    fun `purge removes expired captures and records count`() = runTest {
        val captureDao = FakeCaptureDao()
        val auditLogDao = FakeAuditLogDao()
        val repository = CaptureRepository(captureDao, auditLogDao)
        val expired = CaptureNormalizer.fromText(
            sourcePackage = "com.discord",
            captureType = CaptureType.ACCESSIBILITY,
            text = "expired",
            capturedAtEpochMillis = 0L,
        )
        val active = CaptureNormalizer.fromText(
            sourcePackage = "com.discord",
            captureType = CaptureType.ACCESSIBILITY,
            text = "active",
            capturedAtEpochMillis = CapturedEvent.RETENTION_MILLIS,
        )
        captureDao.insert(expired.toEntity())
        captureDao.insert(active.toEntity())

        val purged = repository.purgeExpired(CapturedEvent.RETENTION_MILLIS)

        assertEquals(1, purged)
        assertEquals(1, captureDao.events.size)
        assertEquals(active.id, captureDao.events.single().id)
        assertTrue(auditLogDao.entries.last().metadata.contains("deleted=1"))
    }

    @Test
    fun `recent metadata excludes payload content`() = runTest {
        val captureDao = FakeCaptureDao()
        val auditLogDao = FakeAuditLogDao()
        val repository = CaptureRepository(captureDao, auditLogDao)
        val event = CaptureNormalizer.fromText(
            sourcePackage = "com.instagram.android",
            captureType = CaptureType.NOTIFICATION,
            text = "payload must not reach debug metadata",
            capturedAtEpochMillis = 2_000L,
        )
        captureDao.insert(event.toEntity())

        val metadata = repository.recentMetadata(limit = 10).single()

        assertEquals("com.instagram.android", metadata.sourcePackage)
        assertEquals("NOTIFICATION", metadata.captureType)
        assertEquals("payload must not reach debug metadata".length, metadata.contentLength)
        assertFalse(metadata.toDisplayLine().contains("payload must not reach"))
    }

    private class FakeCaptureDao : CaptureDao {
        val events = mutableListOf<CapturedEventEntity>()

        override suspend fun insert(event: CapturedEventEntity) {
            events.removeAll { it.id == event.id }
            events += event
        }

        override suspend fun deleteExpired(nowEpochMillis: Long): Int {
            val before = events.size
            events.removeAll { it.expiresAtEpochMillis <= nowEpochMillis }
            return before - events.size
        }

        override suspend fun recentMetadata(limit: Int): List<CaptureMetadataRow> =
            events
                .sortedByDescending { it.capturedAtEpochMillis }
                .take(limit)
                .map {
                    CaptureMetadataRow(
                        id = it.id,
                        sourcePackage = it.sourcePackage,
                        captureType = it.captureType,
                        capturedAtEpochMillis = it.capturedAtEpochMillis,
                        expiresAtEpochMillis = it.expiresAtEpochMillis,
                        contentLength = it.contentLength,
                        synthetic = it.synthetic,
                    )
                }

        override suspend fun count(): Int = events.size
    }

    private class FakeAuditLogDao : AuditLogDao {
        val entries = mutableListOf<AuditLogEntity>()

        override suspend fun insert(entry: AuditLogEntity) {
            entries += entry
        }
    }
}
