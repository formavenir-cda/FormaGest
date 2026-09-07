# Analyse

**Projet :** Gestion d'un service pédagogique
**Statut :** en cours de rédaction — voir § 6 pour les points à arbitrer

---

## 1. Description du besoin

Form'Avenir, organisme de formation dans l'informatique, souhaite optimiser la gestion de son service pédagogique au moyen d'une application web.

L'organisme propose plusieurs **filières de formation**, parmi lesquelles Systèmes et Réseaux et Développement. Chaque filière regroupe plusieurs **cursus** : DWWM, CDA, EADL, TSSR, ASR, ASD et ESD.

Un cursus est constitué de cours organisés selon un ordre pédagogique. Cet ordre traduit à la fois les prérequis entre les cours et la progression attendue de l'apprenant : le cursus DWWM, par exemple, enchaîne algorithmique, Java, HTML/CSS, JavaScript, SQL, Spring Boot, Angular, PHP, Symfony et WordPress. Les cursus CDA, EADL et ESD disposent de leurs propres ensembles de cours.

L'application doit permettre de décrire cette organisation pédagogique, puis de la **planifier dans le temps**. Lorsqu'un cursus est planifié sur une période donnée, une **promotion** est créée : elle est rattachée à un cursus et contient les cours de ce cursus positionnés à des dates précises. Un même cours du catalogue peut donc être planifié plusieurs fois, dans des promotions différentes.

Les promotions planifiées doivent pouvoir être consultées, avec pour chacune le cursus associé, la période couverte et les cours planifiés.

La référente administrative gère les inscriptions. Un élève peut être inscrit soit à une promotion complète, soit à un cours à l'unité. À l'inscription, l'application doit contrôler la cohérence du parcours et empêcher qu'un élève soit inscrit deux fois au même cours, ou qu'il soit inscrit à des cours dans un ordre différent de celui défini par le cursus. La référente doit toutefois pouvoir passer outre ce second contrôle et forcer une inscription lorsque la situation le justifie.

Chaque élève dispose d'un calendrier personnel regroupant les cours de sa promotion et ceux qu'il suit à l'unité. Son accès se limite à la consultation : il ne peut modifier ni son calendrier ni ses inscriptions.

L'accès à l'application passe par une authentification sécurisée. Les utilisateurs se connectent avec un identifiant et un mot de passe, les échanges avec le serveur sont protégés par un token, et les fonctionnalités accessibles dépendent du rôle de l'utilisateur.

Le projet sera développé sous forme d'une application web avec un front-end Angular et un back-end Java.


## 2. Identification des acteurs

Le cahier des charges identifie quatre acteurs. Tous accèdent à l'application via un compte utilisateur authentifié ; leurs droits diffèrent selon leur rôle.

### 2.1 Élève

Acteur en consultation uniquement. Il se connecte à l'application, accède à son calendrier personnel et y consulte les cours liés à sa promotion ainsi que ceux auxquels il est inscrit à l'unité. Il ne dispose d'aucun droit de modification, ni sur son calendrier, ni sur ses inscriptions.

Le cahier des charges emploie indifféremment les termes « élève » et « stagiaire ». Le terme retenu pour l'ensemble du dossier est **élève**.

### 2.2 Référente administrative

Acteur principal de la gestion pédagogique et des inscriptions. Elle peut :

- créer et modifier les filières, les cursus et les cours ;
- planifier un cursus, ce qui crée une promotion ;
- planifier les cours d'une promotion ;
- consulter les promotions ;
- inscrire un élève à une promotion ou à un cours spécifique ;
- forcer une inscription qui ne respecte pas l'ordre pédagogique.

### 2.3 Formateur

Identifié comme acteur dans le cahier des charges, mais aucune fonctionnalité ne lui est associée dans l'énoncé. L'hypothèse de travail retenue est qu'il consulte les promotions dont il a la charge et les cours qu'il anime, sans droit de modification. À confirmer (§ 6).

### 2.4 Administrateur

Également identifié comme acteur sans fonctionnalité décrite. Deux pistes sont ouvertes : lui confier la gestion des comptes utilisateurs et des rôles, ce qui est cohérent avec l'exigence d'authentification, ou le fusionner avec la référente administrative. À arbitrer (§ 6).

![Diagramme de cas d'utilisation système](/docs/diagrams/system-use-case-diagram.svg)
---

## 3. Règles métier

### Structure pédagogique

**RG01 — Organisation des filières**
Une filière regroupe plusieurs cursus. Chaque cursus est rattaché à une seule filière.

**RG02 — Composition d'un cursus**
Un cursus est composé de plusieurs cours, ordonnés selon une progression pédagogique définie. Cet ordre matérialise les prérequis entre les cours.

### Planification

**RG03 — Planification d'un cursus**
Un cursus peut être planifié sur une période donnée. Cette planification donne naissance à une promotion.

**RG04 — Composition d'une promotion**
Une promotion est associée à un seul cursus et couvre une période déterminée. Elle contient les cours de ce cursus planifiés à des dates précises. Un cours du catalogue peut être planifié dans plusieurs promotions.

**RG05 — Consultation des promotions**
L'application permet de consulter les promotions planifiées. Le détail d'une promotion présente le cursus associé, la période et les cours planifiés.

### Inscriptions

**RG06 — Inscription à une promotion**
Un élève peut être inscrit à une promotion complète. Les cours planifiés de cette promotion deviennent alors accessibles depuis son calendrier personnel.

**RG07 — Inscription à un cours à l'unité**
Un élève peut être inscrit à un cours planifié isolé, sans être inscrit à la promotion qui le contient. Ce cours apparaît dans son calendrier personnel.

**RG08 — Interdiction des doubles inscriptions**
Un élève ne peut pas être inscrit deux fois au même cours. Le système vérifie cette règle avant de valider toute inscription. Aucune dérogation n'est possible.

**RG09 — Respect de l'ordre pédagogique**
Un élève ne peut pas être inscrit à un cours si les cours qui le précèdent dans l'ordre du cursus ne font pas déjà l'objet d'une inscription. Le système contrôle cette cohérence à chaque inscription et refuse celles qui ne la respectent pas.

**RG10 — Forçage d'une inscription**
La référente administrative peut forcer une inscription qui enfreint RG09. Le forçage est une action explicite : sans lui, l'inscription est refusée. Il ne s'applique pas à RG08.

### Consultation et sécurité

**RG11 — Calendrier personnel**
Le calendrier personnel d'un élève regroupe les cours planifiés de sa promotion et les cours auxquels il est inscrit à l'unité.

**RG12 — Authentification**
L'accès aux fonctionnalités protégées nécessite une authentification par identifiant et mot de passe. Les échanges avec le serveur sont ensuite protégés par un token.

**RG13 — Droits selon le rôle**
Les fonctionnalités accessibles dépendent du rôle de l'utilisateur. Un utilisateur ne peut accéder qu'à celles autorisées pour son rôle. En particulier, l'élève dispose d'un accès en lecture seule sur son calendrier et ses inscriptions.

---

## 4. Dictionnaire des données

| Nom | Description | Type | Commentaires | Contraintes, règles de calcul |
|---|---|---|---|---|
| Utilisateur | Personne disposant d'un compte et accédant à l'application | Entité | Identifiant, mot de passe, rôle. Généralise Élève, Formateur, Référente administrative et Administrateur | Authentification obligatoire (RG12) ; droits déterminés par le rôle (RG13) |
| Élève | Utilisateur suivant des cours et consultant son calendrier | Entité | Spécialisation d'Utilisateur. Nom, prénom, date de naissance. Synonyme non retenu : stagiaire | Accès en lecture seule (RG13) |
| Formateur | Utilisateur assurant les cours | Entité | Spécialisation d'Utilisateur. | — |
| Référente administrative | Utilisateur en charge de la structure pédagogique et des inscriptions | Entité | Spécialisation d'Utilisateur | Seule habilitée à forcer une inscription (RG10) |
| Administrateur | Utilisateur aux droits étendus | Entité | Spécialisation d'Utilisateur. | — |
| Filière | Thématique principale de formation | Entité | Regroupe plusieurs cursus | RG01 |
| Cursus | Parcours de formation composé de cours ordonnés | Entité | Rattaché à une filière | RG01, RG02 |
| Cours | Matière enseignée, élément du catalogue | Entité | Appartient à un cursus et y occupe un rang | L'ordre traduit les prérequis (RG02) |
| Promotion | Cursus planifié sur une période | Entité | Rattachée à un cursus. Date de début, date de fin | Naît de la planification d'un cursus (RG03, RG04) |
| Cours planifié | Occurrence d'un cours à une date précise dans une promotion | Entité | Rattaché à un cours et à une promotion. Date, horaires | Un cours peut être planifié dans plusieurs promotions (RG04). C'est cette entité qui alimente le calendrier |
| Inscription | Rattachement d'un élève à une promotion ou à un cours planifié | Entité | Date d'inscription, indicateur de forçage | Unicité par élève et par cours (RG08) ; contrôle d'ordre (RG09) ; forçage possible (RG10) |
| Calendrier personnel | Vue des cours planifiés d'un élève | — | Résultat de l'agrégation des inscriptions, pas une donnée stockée | RG11 |

---