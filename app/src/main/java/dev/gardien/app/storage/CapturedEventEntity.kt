package dev.gardien.app.storage

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.gardien.app.capture.CaptureType
import dev.gardien.app.capture.CapturedEvent

@Entity(tableName = "captured_events")
data class CapturedEventEntity(
    @PrimaryKey val id: String,
    val sourcePackage: String,
    val captureType: String,
    val capturedAtEpochMillis: Long,
    val expiresAtEpochMillis: Long,
    val contentLength: Int,
    val synthetic: Boolean,
    val payload: String?,
)

fun CapturedEvent.toEntity(): CapturedEventEntity =
    CapturedEventEntity(
        id = id,
        sourcePackage = sourcePackage,
        captureType = captureType.name,
        capturedAtEpochMillis = capturedAtEpochMillis,
        expiresAtEpochMillis = expiresAtEpochMillis,
        contentLength = contentLength,
        synthetic = synthetic,
        payload = payload,
    )

fun CapturedEventEntity.toDomain(): CapturedEvent =
    CapturedEvent(
        id = id,
        sourcePackage = sourcePackage,
        captureType = CaptureType.valueOf(captureType),
        capturedAtEpochMillis = capturedAtEpochMillis,
        expiresAtEpochMillis = expiresAtEpochMillis,
        contentLength = contentLength,
        synthetic = synthetic,
        payload = payload,
    )
