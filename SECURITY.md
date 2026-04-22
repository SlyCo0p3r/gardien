# Security Policy

## Reporting a Vulnerability

Gardien handles sensitive data related to the protection of minors. We take security extremely seriously.

If you discover a security vulnerability, **please do NOT open a public issue.**

Instead, report it privately:
- Email: **mail@paulsi.mn**
- Subject: `[SECURITY] Gardien - <brief description>`

We will acknowledge receipt within 48 hours and provide a detailed response within 7 days.

## Scope

Security issues include but are not limited to:
- Bypass of the device lock mechanism
- Unauthorized access to the encrypted local database
- Evidence tampering (breaking SHA-256/RFC 3161 integrity chain)
- Data exfiltration (any data leaving the device without incident detection)
- Bypass of parental PIN protection
- Privilege escalation within the app

## Supported Versions

| Version | Supported |
|---------|:---------:|
| MVP (dev) | ✅ |

## Responsible Disclosure

We follow responsible disclosure practices. We ask that you:
1. Give us reasonable time to fix the issue before public disclosure
2. Do not exploit the vulnerability beyond what is necessary to demonstrate it
3. Do not access or modify data belonging to other users

Thank you for helping keep children safe.
