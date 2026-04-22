# 🛡️ Gardien — IA On-Device for Child Safety

> **An Android app that protects children by analyzing messages and images locally — nothing leaves the phone except in case of danger.**

No cloud. No spying. No dashboard. Just an AI guardian that *understands* risk — and stays silent until it must act.

## 🚫 What This Is NOT

- ✖️ Not Bark, Qustodio, or mSpy — we do **not** send your child's messages to the cloud.
- ✖️ Not a parental spy tool — you **do not** read your child's messages.
- ✖️ Not a screen time tracker — we don't monitor how long they're on their phone.

## ✅ What This IS

- ✅ A **local LLM** running on the child's Android phone (Phi-3-mini 3.8B)
- ✅ Monitors **all messaging apps** (WhatsApp, Instagram, Snapchat, SMS) via Android Accessibility + Notification services
- ✅ Detects **grooming, self-harm, violence, sexual content** — using **fine-tuned Llama Guard 3**
- ✅ **Zero data leaves the device** unless an incident is detected
- ✅ In case of danger: **sends SMS alert to parents** + **locks the phone** (only calls to parents allowed)
- ✅ Generates **cryptographically signed evidence** (SHA-256 + RFC 3161 timestamp) for legal use
- ✅ **Privacy-first design**: child is informed, two-parent consent required, GDPR/FR compliant

## 📜 License

- **Code**: [AGPL-3.0 + Commons Clause](LICENSE) — Open source, **no commercial use**
- **Documentation**: [CC BY-NC-SA 4.0](LICENSE-DOCS) — Attribution required, non-commercial

## 📄 Documentation

- [Blueprint MVP](docs/BLUEPRINT.md)
- [Legal Framework (France)](docs/LEGAL.md)
- [Security Policy](SECURITY.md)
- [Contributing](CONTRIBUTING.md)

## 🚀 Roadmap

[Phase 0: Setup](#phase-0) → Phase 1: Capture → Phase 2: AI → Phase 3: Lock → Phase 4: Evidence → Phase 5: UX → Phase 6: Test

---

**Built by ClawJoy (Joëlle) for Paul Simon — Rouen, France — April 2026**

> "We protect their privacy, not their silence."

---

*This is a research blueprint. Not production software. Do not install on any device without legal counsel.*