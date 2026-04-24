# Phase 1 Testing Checklist

## Automated Checks

Run from the repository root:

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug --no-daemon
```

The build must fail on test, lint, or assemble errors. CI must not use `continue-on-error`.

## Pixel 7a Manual Pass

Record:

- Android version and security patch.
- Gardien APK version.
- WhatsApp, Snapchat, TikTok, Discord, and Instagram versions tested.
- Notification access status.
- Accessibility service status.
- SMS permission status.
- Whether research capture gate is enabled.
- Any app-specific capture gaps.

Validate:

- Notification metadata capture works for supported apps where test accounts are available.
- Accessibility metadata capture works only for allowlisted packages.
- SMS capture requires runtime permission.
- Synthetic harness logs only source, type, length, timestamp, and synthetic flag.
- No message content appears in logcat metadata lines.
- Captured rows purge after 24 hours.
- No network permission is requested by the app.

## Distribution Notes

Phase 1 APKs are research builds only. Publish direct APKs with a checksum and signing fingerprint. F-Droid documentation should remain explicit that Play Store publication is out of scope for Phase 1 because AccessibilityService policy risk is unresolved.
