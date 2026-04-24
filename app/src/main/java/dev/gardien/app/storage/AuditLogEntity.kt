package dev.gardien.app.storage

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val action: String,
    val sourcePackage: String?,
    val createdAtEpochMillis: Long,
    val metadata: String,
)
