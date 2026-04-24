package dev.gardien.app.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CaptureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: CapturedEventEntity)

    @Query("DELETE FROM captured_events WHERE expiresAtEpochMillis <= :nowEpochMillis")
    suspend fun deleteExpired(nowEpochMillis: Long): Int

    @Query(
        """
        SELECT id, sourcePackage, captureType, capturedAtEpochMillis, expiresAtEpochMillis, contentLength, synthetic
        FROM captured_events
        ORDER BY capturedAtEpochMillis DESC
        LIMIT :limit
        """,
    )
    suspend fun recentMetadata(limit: Int): List<CaptureMetadataRow>

    @Query("SELECT COUNT(*) FROM captured_events")
    suspend fun count(): Int
}
