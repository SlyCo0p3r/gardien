# 📜 Cadre Juridique — Gardien

> **⚠️ Avertissement :** Ce document est une analyse juridique préliminaire et ne constitue pas un avis juridique. Il vise à identifier les enjeux légaux du projet Gardien et à orienter les décisions de conception. **Consultez un avocat spécialisé en droit du numérique et protection des mineurs avant tout déploiement.**

---

## 1. Cadre juridique applicable

Le projet Gardien se situe à l'intersection de plusieurs corpus juridiques :

| Texte | Pertinence |
|-------|----------|
| **RGPD** (Règlement UE 2016/679) | Traitement de données personnelles de mineurs |
| **Loi Informatique et Libertés** (Loi n°78-17 modifiée) | Transposition nationale, spécificités mineurs |
| **Code civil** (Art. 371-1 et s.) | Autorité parentale, devoir de protection |
| **Code pénal** (Art. 226-1, 226-15) | Secret des correspondances, vie privée |
| **Directive ePrivacy** (2002/58/CE) | Confidentialité des communications électroniques |
| **CEDH** (Art. 8) | Droit au respect de la vie privée |
| **Convention internationale des droits de l'enfant** (CIDE) | Intérêt supérieur de l'enfant (Art. 3), vie privée (Art. 16) |
| **Loi n°2024-449** (SREN, 21 mai 2024) | Protection des mineurs en ligne |

---

## 2. Base légale du traitement

### 2.1 Analyse au regard de l'Art. 6 RGPD

Gardien traite des données personnelles (contenu de messages, métadonnées de communication). Plusieurs bases légales sont envisageables :

**a) Consentement (Art. 6.1.a)**
- Le consentement des parents (titulaires de l'autorité parentale) constitue la base légale la plus robuste.
- **Art. 8 RGPD** : pour les services de la société de l'information, le consentement d'un mineur de moins de 16 ans doit être donné ou autorisé par le titulaire de l'autorité parentale.
- **Art. 45 de la Loi Informatique et Libertés** : en France, ce seuil est abaissé à **15 ans**.
- Pour les 10-14 ans : consentement parental **obligatoire**.
- Pour les 15-17 ans : l'enfant peut théoriquement consentir seul, mais Gardien exige le double consentement parental par design (protection renforcée).

**b) Intérêt légitime (Art. 6.1.f)**
- L'intérêt légitime des parents à protéger leur enfant est reconnu.
- Cependant, cette base légale est **plus fragile** pour un traitement aussi intrusif (analyse de contenu conversationnel).
- La CNIL recommande le consentement pour les traitements touchant les mineurs.

**➡️ Choix de Gardien : double consentement parental explicite** (Art. 6.1.a) comme base légale principale, documenté et révocable.

### 2.2 Catégories de données traitées

| Catégorie | Données | Stockage |
|-----------|---------|----------|
| Contenu textuel | Messages entrants (notifications + accessibility) | Mémoire vive uniquement (pas de persistance sauf incident) |
| Contenu image | Images reçues dans les conversations | Analyse en mémoire, pas de stockage |
| Métadonnées | App source, horodatage | Stocké localement si incident |
| Données d'alerte | Hash du contenu, timestamp RFC 3161 | Stocké localement, transmis aux parents uniquement si incident |

---

## 3. Autorité parentale et devoir de protection

### 3.1 Fondement : Art. 371-1 Code civil

> *« L'autorité parentale est un ensemble de droits et de devoirs ayant pour finalité l'intérêt de l'enfant. Elle appartient aux parents jusqu'à la majorité ou l'émancipation de l'enfant pour le protéger dans sa sécurité, sa santé et sa moralité, pour assurer son éducation et permettre son développement, dans le respect dû à sa personne. »*

L'installation de Gardien relève du **devoir de protection** des parents. La jurisprudence reconnaît le droit des parents à exercer une surveillance proportionnée des activités numériques de leurs enfants mineurs.

### 3.2 Double consentement

- Gardien exige le consentement des **deux** titulaires de l'autorité parentale (Art. 372 Code civil : exercice conjoint).
- En cas de désaccord entre parents, le juge aux affaires familiales est compétent (Art. 373-2-6 Code civil).
- Le consentement est recueilli lors de l'installation, avec information claire sur le fonctionnement de l'app.

### 3.3 Proportionnalité

Le principe de proportionnalité impose que la surveillance soit :
- **Adaptée** à l'âge de l'enfant (un adolescent de 17 ans a plus droit à l'intimité qu'un enfant de 10 ans)
- **Limitée** à la détection de dangers réels (pas de surveillance du contenu "normal")
- **Transparente** — l'enfant sait que l'app est installée

Gardien respecte ce principe :
- ✅ Pas de dashboard parental (les parents ne lisent PAS les messages)
- ✅ Seules les alertes de danger sont remontées
- ✅ L'enfant est informé de la présence de l'app
- ✅ Le traitement est purement local

---

## 4. Droits de l'enfant

### 4.1 Droit à la vie privée (Art. 8 CEDH, Art. 16 CIDE)

L'enfant mineur dispose d'un droit à la vie privée, y compris vis-à-vis de ses parents. Ce droit est **limité** par l'autorité parentale mais **non supprimé**.

La CEDH a jugé (arrêt *K.U. c. Finlande*, 2008) que les États ont une obligation positive de protéger les enfants contre les abus en ligne, ce qui peut justifier certaines restrictions à la vie privée.

### 4.2 Intérêt supérieur de l'enfant (Art. 3 CIDE)

> *« Dans toutes les décisions qui concernent les enfants, l'intérêt supérieur de l'enfant doit être une considération primordiale. »*

Gardien est conçu pour servir l'intérêt supérieur de l'enfant :
- Protection contre le grooming, le harcèlement, l'exposition à du contenu dangereux
- Préservation maximale de la vie privée (traitement local, pas de lecture parentale)
- Transparence (l'enfant sait que l'app est active)

### 4.3 Obligation de transparence envers l'enfant

- L'enfant doit être informé, dans un langage adapté à son âge, de :
  - La présence de l'app sur son téléphone
  - Ce que l'app fait et ne fait pas (détecte les dangers, ne transmet pas les messages)
  - Ses droits (accès, effacement — cf. section 11)
- **Recommandation** : écran d'information intégré à l'app, accessible par l'enfant à tout moment

---

## 5. Analyse d'impact (DPIA)

### 5.1 Obligation (Art. 35 RGPD)

Une DPIA est **obligatoire** car le traitement :
- Concerne des **données de mineurs** (personnes vulnérables)
- Implique une **surveillance systématique** (monitoring continu des communications)
- Traite des données à **grande échelle** sur l'appareil
- Utilise des **technologies innovantes** (IA on-device)

La CNIL classe explicitement ce type de traitement dans sa [liste des traitements nécessitant une DPIA](https://www.cnil.fr/fr/ce-quil-faut-savoir-sur-lanalyse-dimpact-relative-la-protection-des-donnees-dpia).

### 5.2 Points d'attention pour la DPIA

| Risque | Mesure d'atténuation Gardien |
|--------|------------------------------|
| Surveillance disproportionnée | Pas de dashboard, alertes seules, enfant informé |
| Fuite de données | Traitement 100% on-device, aucun cloud |
| Faux positifs (alerte injustifiée) | Seuils de confiance élevés, catégorisation fine |
| Accès non autorisé aux preuves | Chiffrement local, PIN parental |
| Détournement (espionnage conjugal) | Double consentement parental, info enfant |
| Biais de l'IA | Tests sur datasets diversifiés, audit régulier |

### 5.3 Consultation préalable CNIL

Si la DPIA révèle un risque résiduel élevé que les mesures n'arrivent pas à atténuer, une **consultation préalable** de la CNIL est obligatoire (Art. 36 RGPD).

---

## 6. Interception de communications

### 6.1 Le risque : Art. 226-15 Code pénal

> *« Le fait, commis de mauvaise foi, d'intercepter, de détourner, d'utiliser ou de divulguer des correspondances émises, transmises ou reçues par la voie électronique [...] est puni d'un an d'emprisonnement et de 45 000 euros d'amende. »*

### 6.2 Pourquoi Gardien n'est PAS une interception illicite

Plusieurs arguments juridiques soutiennent la légalité de Gardien :

**a) Autorité parentale**
- Les parents exercent un devoir légal de surveillance (Art. 371-1 Code civil). L'installation d'un dispositif de protection sur le téléphone de leur enfant mineur relève de ce devoir.
- La jurisprudence distingue la surveillance parentale légitime de l'interception frauduleuse entre adultes.

**b) Absence de "mauvaise foi"**
- L'Art. 226-15 exige la **mauvaise foi**. L'installation d'un dispositif de protection, avec le consentement parental et la connaissance de l'enfant, ne constitue pas un acte de mauvaise foi.

**c) Pas de "divulgation"**
- Gardien ne divulgue pas le contenu des messages. Il analyse localement et ne remonte qu'une **alerte** (pas le contenu lui-même) en cas de danger détecté.
- Les preuves cryptographiques ne sont accessibles qu'aux parents et, le cas échéant, aux autorités judiciaires.

**d) Analogie avec le contrôle parental existant**
- Les dispositifs de contrôle parental sont encouragés par la loi (Loi SREN 2024, Art. L. 34-9-3 CPCE). Gardien va plus loin techniquement mais reste dans le même cadre de finalité.

**⚠️ Zone grise** : Pour les adolescents de 16-17 ans, la proportionnalité est plus délicate. Recommandation : paramètres adaptables selon l'âge.

---

## 7. Privacy by design (Art. 25 RGPD)

Gardien est un cas d'école de privacy by design :

| Principe | Implémentation |
|----------|---------------|
| **Minimisation** (Art. 5.1.c) | Seuls les messages entrants sont analysés, en mémoire vive, sans persistance |
| **Limitation de stockage** (Art. 5.1.e) | Aucun stockage sauf incident détecté |
| **Intégrité et confidentialité** (Art. 5.1.f) | Chiffrement local, PIN parental, preuves signées |
| **Protection par défaut** (Art. 25.2) | Configuration la plus protectrice par défaut |
| **Pas de transfert** (Art. 44+) | Aucune donnée ne quitte l'appareil (pas de problématique de transfert international) |

Le traitement **entièrement on-device** élimine les risques liés au cloud : pas de fuite de données en transit, pas de stockage sur des serveurs tiers, pas de juridiction étrangère.

---

## 8. Preuves numériques

### 8.1 Admissibilité en droit français

Le droit français admet la preuve numérique sous conditions :
- **Art. 1366 Code civil** : *« L'écrit électronique a la même force probante que l'écrit sur support papier, sous réserve que puisse être dûment identifiée la personne dont il émane et qu'il soit établi et conservé dans des conditions de nature à en garantir l'intégrité. »*

### 8.2 Dispositif de preuve Gardien

| Élément | Fonction |
|---------|---------|
| **SHA-256** | Empreinte cryptographique garantissant l'intégrité du contenu capturé |
| **RFC 3161 Timestamp** | Horodatage certifié par une autorité tierce (TSA), prouvant l'existence à un instant T |
| **Chaîne de preuves** | Chaque paquet de preuves lie : contenu hashé + timestamp + métadonnées (app source, device ID) |

### 8.3 Valeur probante

- Le hash SHA-256 seul ne prouve pas l'authenticité (n'importe qui peut hasher un contenu).
- Le timestamp RFC 3161, délivré par une TSA accréditée, apporte une **présomption d'intégrité et d'antériorité**.
- En combinaison, le dispositif offre une valeur probante **solide mais non irréfutable** — un expert judiciaire pourrait la valider.
- **Recommandation** : utiliser une TSA qualifiée eIDAS (ex : Universign, DocuSign France) pour maximiser la force probante.

---

## 9. Verrouillage d'urgence

### 9.1 Mécanisme

En cas de danger détecté (grooming actif, contenu sexuel explicite reçu par un mineur), Gardien :
1. Envoie un SMS d'alerte aux parents
2. Verrouille le téléphone (seuls les appels vers les numéros parentaux restent possibles)

### 9.2 Proportionnalité

- Le verrouillage est une mesure **temporaire** et **réversible** (déverrouillage par PIN parental).
- Il est **proportionné** à la gravité de la situation (danger immédiat pour un mineur).
- L'enfant conserve la possibilité d'appeler ses parents (pas d'isolation totale).
- **Analogie** : similaire aux fonctions de contrôle parental existantes (Family Link, Screen Time).

---

## 10. Obligations du développeur

### 10.1 Registre des traitements (Art. 30 RGPD)

Même si le traitement est on-device, le développeur (responsable de la conception) doit tenir un registre décrivant :
- Finalité : protection des mineurs contre les dangers en ligne
- Base légale : consentement parental
- Catégories de données : contenu textuel, images, métadonnées de communication
- Destinataires : parents (alertes seules), pas de tiers
- Durée de conservation : mémoire vive (temps réel) / preuves locales (jusqu'à suppression parentale)
- Mesures de sécurité : chiffrement, traitement on-device, PIN

### 10.2 Délégué à la protection des données (DPO)

- Un DPO n'est pas obligatoire pour un développeur individuel.
- **Recommandation** : désigner un référent RGPD dès que le projet implique des utilisateurs réels.

### 10.3 Notification CNIL

- Pas de déclaration préalable obligatoire depuis le RGPD (2018).
- La DPIA (section 5) remplace l'ancienne déclaration.
- En cas de violation de données : notification CNIL dans les 72h (Art. 33 RGPD) et notification des personnes concernées (Art. 34 RGPD).

---

## 11. Droits des personnes concernées

| Droit | Application Gardien |
|-------|-------------------|
| **Accès** (Art. 15) | L'enfant et ses parents peuvent consulter les données stockées localement |
| **Rectification** (Art. 16) | Non applicable (données brutes non modifiables) |
| **Effacement** (Art. 17) | Suppression des preuves locales sur demande parentale |
| **Limitation** (Art. 18) | Pause possible de la surveillance |
| **Portabilité** (Art. 20) | Non applicable (pas de transfert) |
| **Opposition** (Art. 21) | Désinstallation de l'app par les parents |

**Cas particulier** : l'enfant peut-il demander la désinstallation contre l'avis des parents ?
- Avant 15 ans : non, l'autorité parentale prime.
- 15-17 ans : zone grise — le principe de proportionnalité pourrait jouer en faveur de l'enfant selon les circonstances.
- À 18 ans : l'app doit se désactiver automatiquement.

---

## 12. Checklist de conformité pré-déploiement

| # | Action | Statut |
|---|--------|--------|
| 1 | Réaliser une DPIA complète (Art. 35 RGPD) | ⬜ |
| 2 | Rédiger la politique de confidentialité (langage clair, adapté aux mineurs) | ⬜ |
| 3 | Implémenter le recueil du double consentement parental | ⬜ |
| 4 | Implémenter l'écran d'information pour l'enfant | ⬜ |
| 5 | Choisir une TSA qualifiée eIDAS pour les timestamps RFC 3161 | ⬜ |
| 6 | Tenir le registre des traitements (Art. 30) | ⬜ |
| 7 | Implémenter la désactivation automatique à 18 ans | ⬜ |
| 8 | Paramètres de sensibilité adaptables selon l'âge | ⬜ |
| 9 | Audit de sécurité indépendant avant release | ⬜ |
| 10 | Consultation préalable CNIL si risque résiduel élevé (Art. 36) | ⬜ |
| 11 | Consulter un avocat spécialisé pour validation finale | ⬜ |

---

## 13. Références

- [RGPD — Texte intégral](https://eur-lex.europa.eu/legal-content/FR/TXT/?uri=CELEX%3A32016R0679)
- [Loi Informatique et Libertés](https://www.legifrance.gouv.fr/loda/id/JORFTEXT000000886460)
- [Code civil — Autorité parentale](https://www.legifrance.gouv.fr/codes/section_lc/LEGITEXT000006070721/LEGISCTA000006150073/)
- [Code pénal — Art. 226-15](https://www.legifrance.gouv.fr/codes/article_lc/LEGIARTI000006417933)
- [CNIL — Guide DPIA](https://www.cnil.fr/fr/ce-quil-faut-savoir-sur-lanalyse-dimpact-relative-la-protection-des-donnees-dpia)
- [CIDE — Convention internationale des droits de l'enfant](https://www.unicef.fr/convention-droits-enfants/)
- [Loi SREN 2024](https://www.legifrance.gouv.fr/jorf/id/JORFTEXT000049563689)

---

*Document rédigé en avril 2026 pour le projet Gardien — Paul Simon / Rouen, France.*
*Licence : [CC BY-NC-SA 4.0](../LICENSE-DOCS)*
