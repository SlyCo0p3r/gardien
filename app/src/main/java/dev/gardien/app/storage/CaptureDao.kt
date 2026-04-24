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

    @Query("SELECT COUNT(*) FROM captured_events")
    suspend fun count(): Int
}
