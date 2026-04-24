package dev.gardien.app.storage

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface AuditLogDao {
    @Insert
    suspend fun insert(entry: AuditLogEntity)
}
