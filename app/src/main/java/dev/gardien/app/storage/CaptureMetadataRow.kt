package dev.gardien.app.storage

data class CaptureMetadataRow(
    val id: String,
    val sourcePackage: String,
    val captureType: String,
    val capturedAtEpochMillis: Long,
    val expiresAtEpochMillis: Long,
    val contentLength: Int,
    val synthetic: Boolean,
) {
    fun toDisplayLine(): String =
        "$captureType source=$sourcePackage length=$contentLength capturedAt=$capturedAtEpochMillis synthetic=$synthetic"
}
