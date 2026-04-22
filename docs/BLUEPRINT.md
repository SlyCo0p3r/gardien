# 🛡️ GARDIEN — Blueprint MVP

> Application Android de protection des mineurs par IA on-device
> Version : 0.1-draft — 22 avril 2026

---

## 1. RÉSUMÉ EXÉCUTIF

**Gardien** est une application Android de protection des mineurs (10-18 ans) qui fait tourner un LLM directement sur le téléphone pour analyser en temps réel les conversations et images reçues, **sans jamais envoyer de données vers le cloud**. Aucune solution commerciale existante ne combine IA on-device, respect de la vie privée et preuves à valeur juridique. Le MVP cible Android avec SMS + WhatsApp + Snapchat + Instagram, utilise Phi-3-mini (3.8B Q4) via llama.cpp, et génère des paquets de preuves horodatés et signés cryptographiquement. Le cadre juridique français autorise ce dispositif sous conditions strictes : consentement des deux parents, proportionnalité, transparence envers l'enfant, et privacy by design.

---

## 2. POSITIONNEMENT

| Critère | Bark | Qustodio | mSpy | Family Link | **Gardien** |
|---|---|---|---|---|---|
| Analyse IA du contenu | ✅ Cloud | ❌ | ❌ | ❌ | ✅ **On-device** |
| Données envoyées au cloud | 🔴 Tout | 🔴 Partiel | 🔴 Tout | 🟡 Metadata | 🟢 **Rien** (sauf incident) |
| Messages éphémères | 🟡 Partiel | ❌ | 🟡 Partiel | ❌ | ✅ **Notification + A11y** |
| Respect vie privée enfant | 🟡 | 🟡 | 🔴 | 🟢 | 🟢 **By design** |
| Dashboard parental espion | ✅ (lit les msgs) | ✅ | ✅ (lit tout) | ❌ | ❌ **Alertes seules** |
| Preuves juridiques | ❌ | ❌ | ❌ | ❌ | ✅ **SHA-256 + RFC 3161** |
| Verrouillage d'urgence | ❌ | 🟡 | ❌ | 🟡 | ✅ **Sauf appels parents** |
| Conforme RGPD France | 🔴 US-based | 🟡 | 🔴 | 🟡 | 🟢 **Privacy by design** |
| Open-source | ❌ | ❌ | ❌ | ❌ | ✅ |

**Différenciateur clé :** Gardien est le seul projet qui traite TOUT localement. Le parent ne lit pas les messages de son enfant — il est alerté uniquement quand l'IA détecte un danger. C'est une différence philosophique fondamentale, pas juste technique.

---

## 3. ARCHITECTURE MVP RECOMMANDÉE

### 3.1 Stack technique

| Couche | Choix | Justification |
|---|---|---|
| **Langage** | Kotlin (app) + C++ (llama.cpp JNI) | Kotlin = standard Android moderne. C++ = performance LLM native. |
| **Runtime LLM** | **llama.cpp via JNI** | Plus mature, format GGUF universel, pas vendor-locked. MediaPipe est tentant mais lock Google. |
| **Modèle texte** | **Phi-3-mini 3.8B Q4_K_M** (~2.2 GB) | Meilleure précision safety dans sa catégorie. Fine-tunable pour modération. |
| **Classif. safety** | **Llama Guard 3 fine-tuné** (LoRA sur Phi-3) | Pas de modèle child-safety prêt à l'emploi → fine-tune obligatoire. Gros morceau R&D. |
| **Modèle image** | **Falconsai/nsfw_image_detection** + **pdq hash** (Meta) | NSFW detection léger + perceptual hashing open-source. PhotoDNA closed-source (pas une option). |
| **Image runtime** | ONNX Runtime Mobile | Meilleur support pour classifieurs image quantisés. |
| **Capture texte** | NotificationListenerService + AccessibilityService | Combo = couverture maximale, y compris messages éphémères. |
| **Crypto preuves** | Android Keystore (clés HW) + Bouncy Castle (RFC 3161) | SHA-256 natif + horodatage qualifié. Clés hardware-backed = non-extractibles. |
| **Stockage local** | Room (SQLite chiffré via SQLCipher) | Standard Android, chiffrement AES-256 au repos. |
| **Verrouillage** | Device Policy Manager (Device Admin) | Permet lock écran + whitelist appels. Pas besoin de root. |
| **Build** | Gradle + CMake (pour llama.cpp) | Standard. |
| **Min SDK** | API 28 (Android 9) | NotificationListenerService stable à partir de là. |

### 3.2 Device cible MVP

- **Chipset minimum :** Snapdragon 700-series ou équivalent (Dimensity 7200+, Tensor G2+)
- **RAM minimum :** 6 GB (8 GB recommandé)
- **Stockage :** ~3 GB pour app + modèles + buffer local
- **Android :** 9+ (API 28)
- **Device de référence pour tests :** Pixel 7a (Tensor G2, 8 GB RAM)

### 3.3 Scan périodique & messages éphémères

**Stratégie hybride :**
- **NotificationListenerService** : capture TOUTES les notifications en temps réel (y compris éphémères Snap/WhatsApp). Zéro délai, faible batterie.
- **AccessibilityService** : scan actif quand une app de messaging est au premier plan. Capture le contenu écran.
- **Analyse LLM** : batch processing toutes les 30-60 secondes sur le buffer accumulé.
- **Éphémères Snap** : la notification est capturée AVANT ouverture par l'enfant.

**Impact batterie estimé :** 5-8% supplémentaire/jour.

---

## 4. CADRE JURIDIQUE

### 4.1 Obligations légales

- **RGPD s'applique** même pour traitement on-device (art. 4-2). Privacy by design obligatoire (art. 25).
- **Majorité numérique à 15 ans** (loi du 7 juillet 2023).
- **Les DEUX parents doivent consentir** (art. 372, 373-2-6 Code civil, loi n° 2024-120).
- **Secret des correspondances** (art. 226-15 Code pénal) : exception autorité parentale si proportionné.
- **Preuves recevables** si : horodatage fiable (RFC 3161), intégrité prouvée (SHA-256, NF Z42-013).

### 4.2 Recommandations

- Double validation parentale (2 signatures numériques distinctes)
- Minimisation : pas de stockage post-analyse sauf incident (score ≥ 60)
- Aucun serveur, aucun cloud, aucune télémétrie
- Log local chiffré des actions de l'app
- Droit à l'effacement : désinstallation libre
- AIPD obligatoire avant mise en production (art. 35 RGPD)

---

## 5. RISQUES & LIMITATIONS

### 5.1 Risques techniques

| Risque | Impact | Mitigation |
|---|---|---|
| Faux positifs | 🔴 Élevé | Seuils élevés (≥85 pour lock). Fine-tuning itératif. |
| Faux négatifs | 🔴 Élevé | Multi-messages context window. |
| Google A11y restrictions | 🟠 Moyen | Distribution APK direct / F-Droid. |
| Performance bas de gamme | 🟠 Moyen | Min 6GB RAM. |
| Batterie | 🟡 Faible | Scan périodique. 5-8%/jour. |
| MAJ apps messaging | 🟠 Moyen | Maintenance continue requise. |
| Contournement enfant | 🟠 Moyen | Limité mais réel. |

### 5.2 Risques juridiques

| Risque | Impact | Mitigation |
|---|---|---|
| Qualification d'espionnage | 🔴 Élevé | Transparence totale + double consentement. |
| CNIL / plainte | 🟠 Moyen | AIPD documentée, privacy by design. |
| Preuves irrecevables | 🟠 Moyen | Keystore HW + RFC 3161. |
| Parent unique sans accord | 🟠 Moyen | Double signature imposée. |

---

## 6. LICENCE

- **Code source** → AGPL-3.0 + Commons Clause (see LICENSE + LICENSE-COMMONS-CLAUSE)
- **Documentation** → CC BY-NC-SA 4.0 (see LICENSE-DOCS)
- **Protection INPI** → Enveloppe Soleau recommandée (~15€)

---

## 7. ROADMAP MVP (~20 semaines)

### Phase 0 — Fondations (S1-2)
- [x] Repo GitHub + structure
- [x] Licences (AGPL-3.0 + Commons Clause + CC BY-NC-SA 4.0)
- [ ] Enveloppe Soleau INPI
- [x] README + CONTRIBUTING + SECURITY
- [x] CI/CD (GitHub Actions)

### Phase 1 — Capture (S3-5)
- [ ] NotificationListenerService
- [ ] AccessibilityService
- [ ] Buffer chiffré Room/SQLCipher + purge 24h
- [ ] Tests Pixel 7a

### Phase 2 — Analyse IA (S6-10)
- [ ] llama.cpp JNI
- [ ] Phi-3-mini Q4_K_M
- [ ] Fine-tune Llama Guard 3 LoRA (child safety)
- [ ] Pipeline analyse + scoring
- [ ] Falconsai NSFW + pdq hash
- [ ] Calibration seuils

### Phase 3 — Alerte & Lock (S11-13)
- [ ] SMS alerte parent
- [ ] Device Policy Manager lock + whitelist
- [ ] Tests end-to-end

### Phase 4 — Preuves & Légal (S14-16)
- [ ] Evidence Packager (SHA-256 + RFC 3161)
- [ ] Android Keystore HW
- [ ] PDF rapport + .gardien package
- [ ] AIPD

### Phase 5 — Setup & UX (S17-18)
- [ ] Configuration initiale
- [ ] Double consentement parental
- [ ] Information enfant
- [ ] Panel admin minimaliste

### Phase 6 — Tests & Publication (S19-20)
- [ ] Tests multi-devices
- [ ] Tests batterie (< 10%/jour)
- [ ] Tests faux positifs/négatifs
- [ ] Publication

---

## ANNEXES

### A. Références juridiques
- RGPD : Règlement (UE) 2016/679, art. 4-2, 8, 25, 35
- Code civil : art. 371-1, 372, 373-2-6, 1145, 1367
- Code pénal : art. 222-33-2-1, 226-15, 227-22-1
- Code procédure pénale : art. 427
- Loi du 7 juillet 2023 (majorité numérique 15 ans)
- Loi n° 2022-300 du 2 mars 2022, art. L.34-9-3 CPCE
- Loi n° 2024-120 du 19 février 2024
- NF Z42-013, NF Z42-026
- Convention des droits de l'enfant, art. 3, 16
- Règlement eIDAS
- Jurisprudence : Crim. 5 avril 2010

### B. Modèles et librairies
- [Phi-3-mini](https://huggingface.co/microsoft/Phi-3-mini-4k-instruct-gguf)
- [Llama Guard 3](https://huggingface.co/meta-llama/Llama-Guard-3-8B)
- [llama.cpp](https://github.com/ggerganov/llama.cpp)
- [Falconsai NSFW](https://huggingface.co/Falconsai/nsfw_image_detection)
- [pdq (Meta)](https://github.com/facebook/ThreatExchange)
- [Bouncy Castle](https://www.bouncycastle.org/)
- [SQLCipher](https://www.zetetic.net/sqlcipher/)

### C. Apps de référence
- [Bark](https://www.bark.us)
- [Qustodio](https://www.qustodio.com)
- [Google Family Link](https://families.google.com/familylink/)
- [WAMR](https://play.google.com/store/apps/details?id=com.drilens.wamr)