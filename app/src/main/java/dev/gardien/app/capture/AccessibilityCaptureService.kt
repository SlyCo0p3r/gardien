package dev.gardien.app.capture

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AccessibilityCaptureService : AccessibilityService() {
    private val lastCaptureByPackage = mutableMapOf<String, Long>()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || !CaptureSettings.isResearchCaptureEnabled(this)) return

        val sourcePackage = event.packageName?.toString() ?: return
        if (!CaptureAllowlist.isMessagingPackage(sourcePackage)) return

        val now = System.currentTimeMillis()
        val previous = lastCaptureByPackage[sourcePackage] ?: 0L
        if (now - previous < RATE_LIMIT_MILLIS) return
        lastCaptureByPackage[sourcePackage] = now

        val text = extractWindowText(rootInActiveWindow).trim()
        if (text.isBlank()) return

        CaptureSubmission.submit(
            this,
            CaptureNormalizer.fromText(
                sourcePackage = sourcePackage,
                captureType = CaptureType.ACCESSIBILITY,
                text = text,
                capturedAtEpochMillis = now,
            ),
        )
    }

    override fun onInterrupt() = Unit

    private fun extractWindowText(root: AccessibilityNodeInfo?): String {
        if (root == null) return ""

        val fragments = mutableListOf<String>()
        fun walk(node: AccessibilityNodeInfo?) {
            if (node == null) return
            node.text?.toString()?.takeIf { it.isNotBlank() }?.let(fragments::add)
            for (index in 0 until node.childCount) {
                walk(node.getChild(index))
            }
        }

        walk(root)
        return fragments.joinToString(separator = "\n")
    }

    private companion object {
        const val RATE_LIMIT_MILLIS = 2_000L
    }
}
