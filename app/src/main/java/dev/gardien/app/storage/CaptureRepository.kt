package dev.gardien.app.storage

import dev.gardien.app.capture.CapturedEvent

class CaptureRepository(
    private val captureDao: CaptureDao,
    private val auditLogDao: AuditLogDao,
) {
    suspend fun save(event: CapturedEvent) {
        captureDao.insert(event.toEntity())
        auditLogDao.insert(
            AuditLogEntity(
                action = "capture_saved",
                sourcePackage = event.sourcePackage,
                createdAtEpochMillis = System.currentTimeMillis(),
                metadata = event.toMetadataLogLine(),
            ),
        )
    }

    suspend fun purgeExpired(nowEpochMillis: Long = System.currentTimeMillis()): Int {
        val deleted = captureDao.deleteExpired(nowEpochMillis)
        auditLogDao.insert(
            AuditLogEntity(
                action = "capture_purged",
                sourcePackage = null,
                createdAtEpochMillis = nowEpochMillis,
                metadata = "deleted=$deleted timestamp=$nowEpochMillis",
            ),
        )
        return deleted
    }

    suspend fun recentMetadata(limit: Int = 20): List<CaptureMetadataRow> =
        captureDao.recentMetadata(limit)
}
