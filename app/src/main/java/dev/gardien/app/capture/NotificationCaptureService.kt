package dev.gardien.app.capture

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationCaptureService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null || !CaptureSettings.isResearchCaptureEnabled(this)) return
        if (!CaptureAllowlist.isMessagingPackage(sbn.packageName)) return

        val text = notificationText(sbn.notification)
        if (text.isBlank()) return

        CaptureSubmission.submit(
            this,
            CaptureNormalizer.fromText(
                sourcePackage = sbn.packageName,
                captureType = CaptureType.NOTIFICATION,
                text = text,
                capturedAtEpochMillis = sbn.postTime,
            ),
        )
    }

    private fun notificationText(notification: Notification): String {
        val extras = notification.extras
        return listOfNotNull(
            extras.getCharSequence(Notification.EXTRA_TITLE)?.toString(),
            extras.getCharSequence(Notification.EXTRA_TEXT)?.toString(),
            extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString(),
            extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString(),
        ).joinToString(separator = "\n").trim()
    }
}
