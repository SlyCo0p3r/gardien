package dev.gardien.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import dev.gardien.app.capture.CaptureSettings
import dev.gardien.app.capture.SyntheticCaptureHarness

class MainActivity : Activity() {
    private lateinit var statusView: TextView

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
                addView(Button(this@MainActivity).apply {
                    text = getString(R.string.synthetic_capture_button)
                    setOnClickListener {
                        SyntheticCaptureHarness.injectSample(this@MainActivity)
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.synthetic_capture_toast),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                })
            }
        }

        setContentView(
            layout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )
        updateStatus()
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

    private companion object {
        const val SMS_PERMISSION_REQUEST = 101
    }
}
