package dev.gardien.app.storage

import android.content.Context

class AppContainer private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val database: GardienDatabase by lazy {
        GardienDatabase.create(
            context = appContext,
            passphrase = DatabaseKeyProvider(appContext).getOrCreatePassphrase(),
        )
    }

    val captureRepository: CaptureRepository by lazy {
        CaptureRepository(
            captureDao = database.captureDao(),
            auditLogDao = database.auditLogDao(),
        )
    }

    companion object {
        @Volatile
        private var instance: AppContainer? = null

        fun from(context: Context): AppContainer =
            instance ?: synchronized(this) {
                instance ?: AppContainer(context).also { instance = it }
            }
    }
}
