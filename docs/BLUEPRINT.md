# 🛡️ GARDIEN — Blueprint MVP

> Application Android de protection des mineurs par IA on-device
> Version : 0.1-draft — 22 avril 2026

---

## 1. RÉSUMÉ EXÉCUTIF

**Gardien** est une application Android de protection des mineurs (10-18 ans) qui fait tourner un LLM directement sur le téléphone pour analyser en temps réel les conversations et images reçues, **sans jamais envoyer de données vers le cloud**. Aucune solution commerciale existante ne combine IA on-device, respect de la vie privée et preuves à valeur juridique. Le MVP cible Android avec SMS + WhatsApp + Snapchat + Instagram + TikTok + Discord, utilise Phi-3-mini (3.8B Q4) via llama.cpp, et génère des paquets de preuves horodatés et signés cryptographiquement. Le cadre juridique français autorise ce dispositif sous conditions strictes : consentement des deux parents, proportionnalité, transparence envers l'enfant, et privacy by design.

---

## 2. POSITIONNEMENT

| Critère | Bark | Qustodio | mSpy | Family Link | **Gardien** |
|---|---|---|---|---|---|
| Analyse IA du contenu | ✅ Cloud | ❌ | ❌ | ❌ | ✅ **On-device** |
| Données envoyées au cloud | 🔴 Tout | 🔴 Partiel | 🔴 Tout | 🟡 Metadata | 🟢 **Rien** (sauf incident) |
| Messages éphémères | 🟡 Partiel | ❌ | 🟡 Partiel | ❌ | ✅ **Notification + A11y** (Snap, TikTok, Discord) |
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

### 3.2 Diagramme d'architecture

```
┌───────────────────────────────────────────────────────────────┐
│                    GARDIEN — Android App                      │
│                                                               │
│  ┌───────────────────  CAPTURE LAYER  ─────────────────────┐  │
│  │                                                         │  │
│  │  ┌──────────────┐  ┌────────────────┐  ┌──────────────┐ │  │
│  │  │ Notification │  │ Accessibility  │  │ SMS Content  │ │  │
│  │  │   Listener   │  │    Service     │  │   Provider   │ │  │
│  │  │              │  │                │  │              │ │  │
│  │  │ • WA notifs  │  │ • Chat bubbles │  │ • SMS/MMS    │ │  │
│  │  │ • Snap notifs│  │ • Screen text  │  │   inbox      │ │  │
│  │  │ • Insta notif│  │ • Image detect │  │              │ │  │
│  │  │ • TikTok DMs │  │ • UI elements  │  │              │ │  │
│  │  │ • Discord    │  │                │  │              │ │  │
│  │  │ • Éphémères ✓│  │ • UI elements  │  │              │ │  │
│  │  └──────┬───────┘  └───────┬────────┘  └──────┬───────┘ │  │
│  └─────────┼──────────────────┼──────────────────┼─────────┘  │
│            └──────────┬───────┘                   │            │
│                       ▼                           ▼            │
│  ┌────────────────────────────────────────────────────────────┐│
│  │         MESSAGE QUEUE (Room DB / SQLCipher)               ││
│  │         Buffer chiffré AES-256 — rétention 24h            ││
│  └──────────────────────────┬─────────────────────────────────┘│
│                             ▼                                  │
│  ┌────────────────  ANALYSIS ENGINE  ─────────────────────────┐│
│  │                                                            ││
│  │  ┌────────────────────┐  ┌───────────────────────────┐    ││
│  │  │  Phi-3-mini 3.8B   │  │  Image Safety Pipeline    │    ││
│  │  │  (llama.cpp / JNI) │  │                           │    ││
│  │  │                    │  │  Falconsai NSFW (ONNX)    │    ││
│  │  │  Llama Guard 3     │  │  +                        │    ││
│  │  │  LoRA adapter      │  │  pdq perceptual hash      │    ││
│  │  │  (child safety)    │  │  (CSAM hash DB locale)    │    ││
│  │  │                    │  │                           │    ││
│  │  │  Catégories :      │  │                           │    ││
│  │  │  • Harcèlement     │  │                           │    ││
│  │  │  • Prédation/groom.│  │                           │    ││
│  │  │  • Automutilation  │  │                           │    ││
│  │  │  • Contenu sexuel  │  │                           │    ││
│  │  │  • Violence        │  │                           │    ││
│  │  └─────────┬──────────┘  └──────────┬────────────────┘    ││
│  └────────────┼───────────────────────────┼──────────────────┘│
│               └──────────┬────────────────┘                    │
│                          ▼                                     │
│  ┌────────────  DECISION ENGINE  ─────────────────────────────┐│
│  │                                                            ││
│  │  Score de risque (0-100) :                                 ││
│  │                                                            ││
│  │  0-30  → [OK]       RAS — purge buffer                    ││
│  │  30-60 → [ATTENTION] Log local, pas d'alerte              ││
│  │  60-85 → [ALERTE]   ALERTE PARENT (SMS depuis tél. enfant)││
│  │  85+   → [LOCK]     LOCK DEVICE + ALERTE + PACKAGE PREUVES││
│  │                                                            ││
│  └────────┬─────────────────────────────┬─────────────────────┘│
│           ▼                             ▼                      │
│  ┌─────────────────┐  ┌──────────────────────────────────────┐│
│  │  ALERT MODULE   │  │       EVIDENCE PACKAGER              ││
│  │                 │  │                                      ││
│  │  • SMS au parent│  │  • SHA-256 hash messages             ││
│  │    depuis n° de │  │  • SHA-256 hash screenshots          ││
│  │    l'enfant     │  │  • RFC 3161 timestamp (Bouncy Castle)││
│  │  • Catégorie    │  │  • Android Keystore signature (HW)   ││
│  │    danger       │  │  • PDF rapport généré                ││
│  │  • Score        │  │  • Package .gardien (ZIP signé)      ││
│  └─────────────────┘  └──────────────────────────────────────┘│
│                                                               │
│  ┌────────────  LOCK MODULE  ─────────────────────────────────┐│
│  │  Device Policy Manager                                    ││
│  │  • Verrouillage écran total                               ││
│  │  • Whitelist : appels vers parents uniquement             ││
│  │  • Déverrouillage : PIN parental (6 digits)               ││
│  └────────────────────────────────────────────────────────────┘│
│                                                               │
│  ┌────────────  SETUP / CONFIG  ──────────────────────────────┐│
│  │  • Écran setup initial (PIN parental, n° parents)         ││
│  │  • Double consentement parental (signature numérique)     ││
│  │  • Apps à surveiller (sélection)                          ││
│  │  • Modèle LLM embarqué (download initial ~2.2 GB)        ││
│  └────────────────────────────────────────────────────────────┘│
└───────────────────────────────────────────────────────────────┘
```

### 3.3 Device cible MVP

- **Chipset minimum :** Snapdragon 700-series ou équivalent (Dimensity 7200+, Tensor G2+)
- **RAM minimum :** 6 GB (8 GB recommandé)
- **Stockage :** ~3 GB pour app + modèles + buffer local
- **Android :** 9+ (API 28)
- **Device de référence pour tests :** Pixel 7a (Tensor G2, 8 GB RAM) — bon ratio prix/perf, accès rapide aux betas Android

### 3.4 Scan périodique & messages éphémères

**Stratégie hybride :**

- **NotificationListenerService** : capture TOUTES les notifications en temps réel (y compris éphémères Snap/WhatsApp, DMs TikTok, messages Discord). Zéro délai, faible batterie. C'est la première ligne.
- **AccessibilityService** : scan actif quand une app de messaging est au premier plan. Capture le contenu écran (texte + détection d'éléments image). Intervalle : à chaque changement de fenêtre/scroll.
- **Analyse LLM** : batch processing toutes les 30-60 secondes sur le buffer accumulé. Pas de scan en continu = batterie préservée.
- **Éphémères Snap** : la notification est capturée AVANT ouverture par l'enfant. Si le contenu est suffisant dans la notif → analyse immédiate. Sinon → AccessibilityService capture le contenu quand l'enfant ouvre le message.

**Impact batterie estimé :** 5-8% supplémentaire/jour en usage normal (comparable à un antivirus Android).

---

## 4. CADRE JURIDIQUE — OBLIGATIONS & RECOMMANDATIONS

### 4.1 Obligations légales

- **RGPD s'applique** même pour traitement on-device (art. 4-2 : la collecte EST un traitement). Privacy by design obligatoire (art. 25).
- **Majorité numérique à 15 ans** (loi du 7 juillet 2023). Pour les 10-14 ans : consentement parental vérifié obligatoire. Pour les 15-17 ans : le mineur peut théoriquement refuser.
- **Les DEUX parents doivent consentir** à l'installation (art. 372, 373-2-6 Code civil, loi n° 2024-120). En cas de désaccord → juge aux affaires familiales.
- **Secret des correspondances** (art. 226-15 Code pénal) : l'interception est un délit, MAIS l'autorité parentale crée une exception si proportionné et dans l'intérêt supérieur de l'enfant.
- **Preuves recevables** si : horodatage fiable (RFC 3161), intégrité prouvée (SHA-256, NF Z42-013), chaîne de conservation documentée (art. 427 CPP, jurisprudence Crim. 5 avril 2010).

### 4.2 Recommandations d'implémentation

- **Écran de setup :** double validation parentale (2 signatures numériques distinctes) + information claire à l'enfant sur ce que fait l'app.
- **Minimisation :** NE PAS stocker le contenu des messages après analyse sauf en cas d'incident (score ≥ 60). Buffer 24h puis purge automatique.
- **Pas de collecte centralisée :** aucun serveur, aucun cloud, aucune télémétrie. L'app est un monolithe local.
- **Auditabilité :** log local chiffré des actions de l'app (quand elle a scanné, quand elle a purgé, quand elle a alerté). Consultable par les parents sur le device.
- **Droit à l'effacement :** l'enfant (ou le parent) peut désinstaller l'app à tout moment. Les preuves packagées restent si exportées.
- **AIPD (Analyse d'Impact) :** obligatoire avant mise en production (art. 35 RGPD). À inclure dans la documentation du blueprint.

---

## 5. RISQUES & LIMITATIONS

### 5.1 Risques techniques

| Risque | Impact | Mitigation |
|---|---|---|
| Faux positifs (ado drama normal classé comme harcèlement) | 🔴 Élevé | Seuils de confiance élevés (≥85 pour lock). Score 60-85 = alerte sans lock. Fine-tuning itératif. |
| Faux négatifs (grooming subtil non détecté) | 🔴 Élevé | LLM 3.8B a des limites de compréhension contextuelle. Mitigation : multi-messages context window. |
| Google Accessibility Service restrictions | 🟠 Moyen | Google durcit ses politiques Play Store. Risque : rejet du Play Store → distribution via APK direct / F-Droid. |
| Performance sur devices bas de gamme | 🟠 Moyen | Min 6GB RAM. Exclut ~30% du parc Android. |
| Batterie | 🟡 Faible | Scan périodique, pas continu. Estimé 5-8%/jour. |
| Mises à jour apps messaging | 🟠 Moyen | UI changes dans WhatsApp/Snap = AccessibilityService peut casser. Maintenance continue requise. |
| Contournement par l'enfant | 🟠 Moyen | Désactivation A11y Service, utilisation d'apps non surveillées, téléphone d'un ami. Limité mais réel. |

### 5.2 Risques juridiques

| Risque | Impact | Mitigation |
|---|---|---|
| Qualification d'espionnage si mal implémenté | 🔴 Élevé | Transparence totale envers l'enfant. Double consentement parental. Pas de dashboard lecture de messages. |
| CNIL / plainte | 🟠 Moyen | AIPD documentée, privacy by design, minimisation, purge auto. |
| Preuves irrecevables si chaîne de conservation rompue | 🟠 Moyen | Android Keystore HW-backed + RFC 3161 + logs d'intégrité. Consulter un avocat pour validation formelle. |
| Parent unique qui installe sans accord de l'autre | 🟠 Moyen | Setup impose double signature. Note légale explicite. |

---

## 6. LICENCE

- **Code source** → AGPL-3.0 + Commons Clause (see LICENSE)
- **Documentation** → CC BY-NC-SA 4.0 (see LICENSE-DOCS)
- **Protection INPI** → Enveloppe Soleau déposée (INPI, avril 2026)

---

## 7. ROADMAP MVP (~20 semaines)

### Phase 0 — Fondations (S1-2)
- [x] Repo GitHub + structure
- [x] Licences (AGPL-3.0 + Commons Clause + CC BY-NC-SA 4.0)
- [x] Enveloppe Soleau INPI
- [x] README + CONTRIBUTING + SECURITY
- [x] CI/CD (GitHub Actions)

### Phase 1 — Capture (S3-5)
- [ ] NotificationListenerService (WhatsApp, Snap, TikTok, Discord, Insta)
- [ ] AccessibilityService
- [ ] SMS Content Provider
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
