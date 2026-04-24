package dev.gardien.app.capture

data class CapturedEvent(
    val id: String,
    val sourcePackage: String,
    val captureType: CaptureType,
    val capturedAtEpochMillis: Long,
    val expiresAtEpochMillis: Long,
    val contentLength: Int,
    val synthetic: Boolean,
    val payload: String?,
) {
    fun isExpired(nowEpochMillis: Long): Boolean = expiresAtEpochMillis <= nowEpochMillis

    fun toMetadataLogLine(): String =
        "source=$sourcePackage type=$captureType length=$contentLength timestamp=$capturedAtEpochMillis synthetic=$synthetic"

    companion object {
        const val RETENTION_MILLIS: Long = 24L * 60L * 60L * 1000L
    }
}
