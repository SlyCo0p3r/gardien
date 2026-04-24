package dev.gardien.app

import android.app.Application
import dev.gardien.app.storage.AppContainer
import dev.gardien.app.storage.PurgeExpiredCapturesWorker

class GardienApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.from(this)
        PurgeExpiredCapturesWorker.enqueue(this)
    }
}
