package dev.gardien.app.capture

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

object CaptureSettings {
    private const val PREFS = "gardien_capture_settings"
    private const val KEY_RESEARCH_CAPTURE = "research_capture_enabled"

    fun isResearchCaptureEnabled(context: Context): Boolean =
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_RESEARCH_CAPTURE, false)

    fun setResearchCaptureEnabled(context: Context, enabled: Boolean) {
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_RESEARCH_CAPTURE, enabled)
            .apply()
    }

    fun canReadSms(context: Context): Boolean =
        context.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
}
