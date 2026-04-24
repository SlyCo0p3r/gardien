package dev.gardien.app.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(
    entities = [
        CapturedEventEntity::class,
        AuditLogEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class GardienDatabase : RoomDatabase() {
    abstract fun captureDao(): CaptureDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        fun create(context: Context, passphrase: ByteArray): GardienDatabase {
            System.loadLibrary("sqlcipher")
            return Room.databaseBuilder(
                context.applicationContext,
                GardienDatabase::class.java,
                "gardien_capture.db",
            )
                .openHelperFactory(SupportOpenHelperFactory(passphrase))
                .build()
        }
    }
}
