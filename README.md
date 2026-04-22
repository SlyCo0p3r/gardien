<p align="center">
  <img src="assets/banner.png" alt="Gardien — Protéger aujourd'hui. Construire demain." />
</p>

<h1 align="center">🛡️ Gardien</h1>
<p align="center"><strong>On-Device AI for Child Safety</strong></p>
<p align="center">
  An Android app that protects children by analyzing messages and images locally —<br/>
  <em>nothing leaves the phone except in case of danger.</em>
</p>

<p align="center">
  <a href="LICENSE"><img src="https://img.shields.io/badge/code-AGPL--3.0%20%2B%20Commons%20Clause-blue" alt="License: AGPL-3.0 + Commons Clause"></a>
  <a href="LICENSE-DOCS"><img src="https://img.shields.io/badge/docs-CC%20BY--NC--SA%204.0-green" alt="License: CC BY-NC-SA 4.0"></a>
  <img src="https://img.shields.io/badge/platform-Android-brightgreen" alt="Platform: Android">
  <img src="https://img.shields.io/badge/AI-on--device-orange" alt="AI: On-Device">
  <img src="https://img.shields.io/badge/privacy-zero%20cloud-critical" alt="Privacy: Zero Cloud">
</p>

---

> 🇬🇧 **English** — [🇫🇷 Version française ci-dessous](#-gardien-1)

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

> ⚠️ **Not open-source.** The AGPL-3.0 + Commons Clause license makes this project **source-available** — you can read, fork, and contribute, but **commercial use is prohibited**. This is a deliberate choice: child safety tools should not be monetized by third parties.

- **Code**: [AGPL-3.0 + Commons Clause](LICENSE) — Source-available, **no commercial use**
- **Documentation**: [CC BY-NC-SA 4.0](LICENSE-DOCS) — Attribution required, non-commercial

## 📄 Documentation

- [Blueprint MVP](docs/BLUEPRINT.md