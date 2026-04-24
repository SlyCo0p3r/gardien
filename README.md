<p align="center">
  <img src="assets/banner.png" alt="Gardien Banner" width="100%">
</p>

<h1 align="center">Gardien</h1>
<h3 align="center">Protection Android des mineurs, IA on-device, respect de la vie privée</h3>

<p align="center">
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-AGPL--3.0%20%2B%20Commons%20Clause-blue" alt="License"></a>
  <a href="LICENSE-DOCS"><img src="https://img.shields.io/badge/docs-CC%20BY--NC--SA%204.0-green" alt="Docs License"></a>
  <a href="https://github.com/SlyCo0p3r/gardien/actions"><img src="https://github.com/SlyCo0p3r/gardien/actions/workflows/ci.yml/badge.svg" alt="CI"></a>
  <img src="https://img.shields.io/badge/platform-Android-brightgreen" alt="Platform">
  <img src="https://img.shields.io/badge/status-Phase%201%20scaffold-orange" alt="Phase 1 scaffold">
</p>

<p align="center"><em>Source-available · Android · IA locale · Zéro cloud sauf incident</em></p>

---

Gardien est un projet Android de recherche pour protéger les enfants et adolescents contre certains risques en ligne tout en évitant le modèle de surveillance parentale permanente. L'objectif produit reste simple : analyser localement les messages et images sur le téléphone de l'enfant, ne pas exposer les conversations aux parents, et ne sortir des données de l'appareil qu'en cas d'incident sérieux.

**Important : Gardien n'est pas un logiciel de production.** Le dépôt contient aujourd'hui un scaffold Android de Phase 1 et des fondations de test. Il ne doit pas être installé sur un appareil réel d'enfant sans revue juridique, validation de sécurité, consentements nécessaires et tests terrain documentés.

## Positionnement

Gardien vise une approche différente des outils de contrôle parental classiques :

- **On-device d'abord** : l'analyse prévue se fait localement sur Android, sans tableau de bord cloud.
- **Pas d'espionnage parental** : le parent ne consulte pas les conversations ; il reçoit uniquement une alerte si un seuil de risque est atteint.
- **Minimisation** : la capture de recherche Phase 1 ne journalise que des métadonnées de validation, pas le contenu des messages.
- **Incident seulement** : les sorties prévues hors appareil concernent les alertes et, plus tard, les paquets de preuves liés à un incident.
- **Source-available** : le code est publié sous AGPL-3.0 + Commons Clause, avec une documentation non commerciale.

## État actuel

Le scaffold Phase 1 est en place dans [`app/`](app/README.md) :

- projet Android Kotlin/Gradle, min SDK 28, target SDK 35 ;
- services de capture NotificationListener, AccessibilityService et lecteur SMS structurés pour la recherche ;
- capture désactivée par défaut et protégée par un toggle de recherche dans l'app debug ;
- stockage local Room + SQLCipher, repository de capture, purge des événements expirés ;
- harnais synthétique qui enregistre uniquement source, type, longueur, timestamp et indicateur synthétique ;
- tests unitaires pour normalisation de capture, stockage et dataset synthétique.

La Phase 1.2 prépare le dataset synthétique : taxonomie, schéma, générateur déterministe et validation anti-PII. Elle ne constitue pas un entraînement de modèle.

Les capacités suivantes restent dans la roadmap et ne doivent pas être présentées comme livrées : LLM embarqué, modèle image, alertes parentales SMS, verrouillage d'urgence, paquets de preuves signés, UX de consentement complète, distribution publique.

## Build et CI

Commande locale depuis la racine du dépôt :

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug --no-daemon
```

La CI GitHub Actions exécute la même commande avec JDK 17 et Android SDK 35. Elle se déclenche sur push vers `main` et `development`, ainsi que sur pull request vers `main`. Les erreurs de tests, lint ou assemble doivent faire échouer le build.

## Workflow de développement

- `development` est la branche de travail courante.
- Créez des branches courtes de type `feature/...` depuis `development`.
- Ouvrez une pull request et attendez la CI avant merge.
- `main` reste la branche stabilisée ; aucune release publique n'est publiée depuis ce dépôt pour l'instant.

## Distribution

Il n'existe pas encore de package public, de release utilisateur ou de publication Play Store/F-Droid.

Les APK Phase 1 sont des builds de recherche uniquement. S'ils sont partagés pour validation, ils doivent être distribués directement avec un checksum et l'empreinte de signature. La documentation F-Droid est prévue pour le chemin MVP ; la publication Play Store n'est pas dans le périmètre Phase 1 à cause du risque de politique Android AccessibilityService.

## Documentation

- [`docs/BLUEPRINT.md`](docs/BLUEPRINT.md) : architecture MVP, risques, roadmap.
- [`docs/PHASE1_TESTING.md`](docs/PHASE1_TESTING.md) : checklist de test Phase 1 et distribution de recherche.
- [`docs/DATASET.md`](docs/DATASET.md) : dataset synthétique, taxonomie et règles de sécurité.
- [`app/README.md`](app/README.md) : détails du scaffold Android.
- [`docs/LEGAL.md`](docs/LEGAL.md) : cadre juridique français et RGPD.
- [`CONTRIBUTING.md`](CONTRIBUTING.md) : contributions et règles éthiques.
- [`SECURITY.md`](SECURITY.md) : politique de sécurité.

## Licence

- Code : [`AGPL-3.0 + Commons Clause`](LICENSE) — source-available, usage commercial interdit sans accord.
- Documentation : [`CC BY-NC-SA 4.0`](LICENSE-DOCS) — attribution requise, usage non commercial.

---

## English

Gardien is a source-available Android research project for child protection with an on-device, privacy-first architecture. The current repository contains the Phase 1 Android scaffold and synthetic testing foundations only. It is not production software, there is no public release package yet, and future AI, alerting, lock, and evidence features must not be treated as shipped.

> On protège leur vie privée, pas leur silence.
