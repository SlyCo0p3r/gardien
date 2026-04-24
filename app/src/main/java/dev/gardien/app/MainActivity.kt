package dev.gardien.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import dev.gardien.app.capture.CaptureSettings
import dev.gardien.app.capture.SyntheticCaptureHarness
import dev.gardien.app.storage.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : Activity() {
    private val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var statusView: TextView
    private var debugCaptureView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        statusView = TextView(this)
        val gateSwitch = Switch(this).apply {
            text = getString(R.string.research_capture_toggle)
            isChecked = CaptureSettings.isResearchCaptureEnabled(this@MainActivity)
            setOnCheckedChangeListener { _, enabled ->
                CaptureSettings.setResearchCaptureEnabled(this@MainActivity, enabled)
                updateStatus()
            }
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            addView(statusView)
            addView(gateSwitch)
            addView(settingsButton(getString(R.string.notification_access_button), Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            addView(settingsButton(getString(R.string.accessibility_button), Settings.ACTION_ACCESSIBILITY_SETTINGS))
            addView(Button(this@MainActivity).apply {
                text = getString(R.string.sms_permission_button)
                setOnClickListener {
                    requestPermissions(arrayOf(Manifest.permission.READ_SMS), SMS_PERMISSION_REQUEST)
                }
            })
            if (BuildConfig.DEBUG) {
                addView(TextView(this@MainActivity).apply {
                    text = getString(R.string.debug_captures_title)
                })
                addView(Button(this@MainActivity).apply {
                    text = getString(R.string.refresh_debug_captures_button)
                    setOnClickListener { refreshDebugCaptures() }
                })
                addView(Button(this@MainActivity).apply {
                    text = getString(R.string.synthetic_capture_button)
                    setOnClickListener {
                        val saveJob = SyntheticCaptureHarness.injectSample(this@MainActivity)
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.synthetic_capture_toast),
                            Toast.LENGTH_SHORT,
                        ).show()
                        saveJob?.invokeOnCompletion {
                            mainScope.launch { refreshDebugCaptures() }
                        } ?: refreshDebugCaptures()
                    }
                })
                debugCaptureView = TextView(this@MainActivity).also { view ->
                    addView(view)
                }
            }
        }

        setContentView(
            ScrollView(this).apply { addView(layout) },
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )
        updateStatus()
        refreshDebugCaptures()
    }

    override fun onDestroy() {
        mainScope.cancel()
        super.onDestroy()
    }

    private fun settingsButton(label: String, action: String): Button =
        Button(this).apply {
            text = label
            setOnClickListener { startActivity(Intent(action)) }
        }

    private fun updateStatus() {
        val gate = if (CaptureSettings.isResearchCaptureEnabled(this)) {
            getString(R.string.capture_gate_enabled)
        } else {
            getString(R.string.capture_gate_disabled)
        }
        statusView.text = getString(R.string.capture_gate_status, gate)
    }

    private fun refreshDebugCaptures() {
        val targetView = debugCaptureView ?: return
        targetView.text = getString(R.string.debug_captures_loading)

        mainScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    AppContainer.from(this@MainActivity)
                        .captureRepository
                        .recentMetadata(DEBUG_CAPTURE_LIMIT)
                }
            }.onSuccess { captures ->
                targetView.text = if (captures.isEmpty()) {
                    getString(R.string.debug_captures_empty)
                } else {
                    captures.joinToString(separator = "\n") { it.toDisplayLine() }
                }
            }.onFailure { error ->
                targetView.text = getString(R.string.debug_captures_error, error.javaClass.simpleName)
            }
        }
    }

    private companion object {
        const val SMS_PERMISSION_REQUEST = 101
        const val DEBUG_CAPTURE_LIMIT = 20
    }
}
