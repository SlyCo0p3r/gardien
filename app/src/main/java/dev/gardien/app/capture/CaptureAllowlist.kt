package dev.gardien.app.capture

object CaptureAllowlist {
    const val SMS_SOURCE = "android.provider.sms"

    val messagingPackages: Set<String> = setOf(
        "com.whatsapp",
        "com.snapchat.android",
        "com.zhiliaoapp.musically",
        "com.discord",
        "com.instagram.android",
    )

    fun isMessagingPackage(packageName: String?): Boolean =
        packageName != null && packageName in messagingPackages
}
