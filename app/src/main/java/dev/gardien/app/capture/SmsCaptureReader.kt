package dev.gardien.app.capture

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.Telephony

class SmsCaptureReader {
    @SuppressLint("MissingPermission")
    fun readInboxSince(context: Context, sinceEpochMillis: Long): List<CapturedEvent> {
        if (!CaptureSettings.canReadSms(context)) return emptyList()

        val resolver = context.contentResolver
        val inboxUri = Uri.parse("content://sms/inbox")
        val projection = arrayOf(Telephony.Sms._ID, Telephony.Sms.DATE, Telephony.Sms.BODY)
        val captures = mutableListOf<CapturedEvent>()

        resolver.query(
            inboxUri,
            projection,
            "${Telephony.Sms.DATE} > ?",
            arrayOf(sinceEpochMillis.toString()),
            "${Telephony.Sms.DATE} ASC",
        )?.use { cursor ->
            val dateIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)
            val bodyIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
            while (cursor.moveToNext()) {
                val body = cursor.getString(bodyIndex).orEmpty()
                val timestamp = cursor.getLong(dateIndex)
                if (body.isBlank()) continue
                captures += CaptureNormalizer.fromText(
                    sourcePackage = CaptureAllowlist.SMS_SOURCE,
                    captureType = CaptureType.SMS,
                    text = body,
                    capturedAtEpochMillis = timestamp,
                )
            }
        }

        return captures
    }
}
