package dev.gardien.app.storage

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class PurgeExpiredCapturesWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val purged = AppContainer.from(applicationContext)
            .captureRepository
            .purgeExpired(System.currentTimeMillis())

        return Result.success(workDataOf(KEY_PURGED_COUNT to purged))
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "gardien_capture_purge"
        private const val KEY_PURGED_COUNT = "purged_count"

        fun enqueue(context: Context) {
            val request = PeriodicWorkRequestBuilder<PurgeExpiredCapturesWorker>(
                6,
                TimeUnit.HOURS,
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request,
            )
        }
    }
}
