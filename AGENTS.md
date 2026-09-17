# Contexte projet — Gestion d'un service pédagogique

Document de contexte à lire avant toute intervention sur ce projet.

## Règles de collaboration

N'implémenter aucun code sans demande explicite. Par défaut : analyse, conception, relecture, conseil. Le développement ne démarre que sur consigne claire.

Seule exception : la mise à jour de ce `AGENTS.md`, qui peut se faire sans demande. Le tenir à jour dès qu'un fait le rend obsolète (renommage, décision d'arbitrage tranchée, changement de convention, avancement d'un livrable).

## Cadre

Projet certificatif du titre **Concepteur Développeur d'Applications (CDA)**, ENI.
Durée : 2 semaines. Réalisé en binôme.
Répartition attendue : environ 1/3 analyse et conception, 2/3 développement.

**Stack imposée** : back-end Java Spring Boot, front-end Angular, authentification par token.

Aucun des deux membres du binôme ne pratique Java ni Angular au quotidien. Le développement s'appuie directement sur les projets et démos des cours ENI (voir « Supports de cours — développement ») : on copie et on adapte les patterns existants plutôt que de repartir de zéro, en particulier pour la sécurité par token. Les estimations de tâches sont calées sur ce rythme d'apprentissage, pas sur celui d'un développeur expérimenté.

## Le sujet

Form'Avenir, organisme de formation en informatique, veut une application web pour gérer son service pédagogique.

Le domaine s'emboîte ainsi : une **filière** (Systèmes et Réseaux, Développement) regroupe plusieurs **cursus** (D2WM, CDA, EADL, TSSR, ASR, ASD, ESD) ; un cursus est composé de **cours** ordonnés selon une progression pédagogique traduisant les prérequis ; planifier un cursus sur une période crée une **promotion**, qui contient des **cours planifiés** à des dates précises.

Quatre acteurs : Élève (consultation seule de son calendrier), Référente administrative (gestion de la structure pédagogique et des inscriptions), Formateur, Administrateur.

Trois règles métier structurent tout le projet :
- un élève ne peut pas être inscrit deux fois au même cours (aucune dérogation) ;
- un élève ne peut pas être inscrit à des cours dans un ordre différent de celui du cursus ;
- la référente administrative peut **forcer** une inscription qui enfreint la règle d'ordre, mais pas celle d'unicité.

Le texte officiel du sujet est reproduit ci-dessous à la lettre. En cas de contradiction avec un résumé de ce document, c'est lui qui fait foi.

## Cahier des charges (texte officiel)

**Gestion d'un service pédagogique — Durée : 2 semaines**

### 1. Contexte

Un organisme de formation souhaite disposer d'une application web permettant de gérer son service pédagogique.

L'organisme propose plusieurs filières, notamment Systèmes et Réseaux, et Développement. Chaque filière regroupe différents cursus de formation : D2WM, CDA, EADL, TSSR, ASR, ASD, ESD.

Un cursus est composé de cours ordonnés respectant une progression pédagogique (prérequis, enchaînement logique).

Les cursus peuvent être planifiés dans le temps. Cette planification donne naissance à des promotions. Une promotion contient des cours planifiés à des dates précises.

Une référente administrative est chargée d'inscrire les élèves, soit à une promotion complète, soit à un cours à l'unité. On ne doit pas pouvoir inscrire un stagiaire deux fois au même cours, ni l'inscrire à des cours dans le désordre par rapport à ceux définis au niveau du cursus (sauf en forçant l'inscription).

### 2. Acteurs de l'application

Élève, Référente administrative, Formateur, Administrateur.

### 3. Authentification et sécurité

L'application doit intégrer un mécanisme d'authentification sécurisé par token : connexion par identifiant et mot de passe, gestion des accès selon le rôle de l'utilisateur, protection des échanges avec le serveur par un token.

### 4. Fonctionnalités attendues

**4.1 Consultation des promotions.** Visualiser l'ensemble des promotions planifiées. Consulter le détail d'une promotion : cursus associé, période, cours planifiés.

**4.2 Élève.** Accéder à son calendrier personnel. Visualiser les cours liés à sa promotion et les cours suivis à l'unité. Aucune action de modification autorisée.

**4.3 Référente administrative.** Créer et modifier filières, cursus, cours. Planifier un cursus pour créer une promotion. Planifier les cours d'une promotion. Inscrire un élève à une promotion ou à un cours spécifique.

### 5. Contraintes d'ergonomie (UX)

L'application doit être la plus simple et intuitive possible : navigation claire et cohérente, interfaces épurées, accès rapide aux informations essentielles, nombre de clics limité.

### 6. Analyse et conception (≈ 1/3 du projet)

**6.1 Analyse.** Description du besoin, identification des acteurs, règles métier principales.

**6.2 UML (minimum requis).** Diagramme de cas d'utilisation. Diagramme de classes métier. Diagrammes de séquence, au moins deux : authentification et consultation du calendrier d'un élève.

### 7. Développement (≈ 2/3 du projet)

Application web complète (front-end et back-end). Séparation claire des responsabilités. API sécurisée par token. Gestion des erreurs. Code lisible, structuré et maintenable.

### 8. Livrables attendus

Code source de l'application, documentation de lancement, dossier d'analyse et de conception, diagrammes UML, maquettes.

## Ressources — cursus et cours réels

Le fichier `docs/reference-formations.md` a été retiré du dépôt (2026-09-10) ; son contenu — liste réelle des cursus de l'organisme et de leurs cours dans l'ordre pédagogique, fournie avec le sujet — reste disponible dans la mémoire persistante de Codex (fichier « Données de référence des formations ») et est déjà exploité dans `backend/src/main/resources/data.sql`. Seuls D2WM et CDA y sont exploitables pour le projet (les autres cursus mélangent cybersécurité, cloud et anglais) ; TSSR, ASR et ASD sont cités par le sujet mais non détaillés.

## Où en est le projet

L'analyse (§6.1) est rédigée dans `docs/analyse-conception.md` : description du besoin, identification des acteurs, 13 règles métier numérotées (RG01 à RG13), dictionnaire des données, user stories (US01 à US18).

**Lire ce fichier en premier.** Il fait foi sur le vocabulaire, la numérotation des règles et les décisions déjà prises.

**UML, les quatre pièces exigées par le sujet sont faites** — à jour au 2026-09-11 :

- Cas d'utilisation **système** et **global** : chacun en deux formes, le schéma mermaid dans le `.md` (système dans `docs/analyse-conception.md` § 2.4, global dans `docs/diagrams/global-use-case-diagram.md`) et un lien vers le SVG exporté depuis le `.uxf` (`system-use-case-diagram.svg`, `global-use-case-diagram.svg`, tous deux versionnés). Le global porte l'acteur générique `Utilisateur`.
- Séquence **authentification** (`docs/diagrams/sequence-diagram-login.md`, `.md` seul versionné, SVG en local dans le `.gitignore`) : `LoginPage → AuthService` (front) puis `AuthController → AuthenticationManager → UserRepository → Database` (back).
- Séquence **consultation du calendrier d'un élève** (`docs/diagrams/diagramme-sequence-calendrier-eleve.md`, mermaid) : `CalendarPage → CalendarController → CalendarService`, une seule requête agrégeant cours de promotion et inscriptions individuelles, dédoublonnée et triée.
- **Classes métier** (`docs/diagrams/diagram_class.md` + export `diagramme-classes-metier.png`).

Le modèle de données est aussi fait, en trois niveaux : `docs/diagrams/modelisation-donnees.md` pointe vers le MCD (`MCD.png`), le MLD (`MLD.jpg`) et le MPD (`MPD.svg` / `MPD.sql`). `docs/glossaire-entites.md` (état au 2026-09-10) fait la correspondance classe JPA ↔ table ↔ terme métier pour les 14 entités du package `bo`.

**Divergence à trancher entre la conception et le code**, repérée le 2026-09-11 : le diagramme de classes et le MPD s'accordent tous les deux sur un `Utilisateur` typé **uniquement par héritage**, sans colonne de rôle (`diagram_class.md` l'écrit explicitement), et sur un `Formateur` **sans lien vers une filière**. Le code implémenté diverge sur ces deux points : `User` porte un champ persisté `role` (enum `UserRole`) **en plus** de l'héritage, et `Teacher` a une association obligatoire vers `Sector`. Autres écarts repérés en même temps, non instruits plus avant : le MPD sépare `COURS_PLANIFIE` d'une table `SEANCE` (dates, horaires, formateur) que le code fusionne dans `ScheduledCourse` ; `INSCRIPTION` dans le MPD porte des champs de désinscription (statut, date/motif d'annulation, référente de création/annulation) absents de `Enrollment`. À arbitrer avec le binôme : mettre le code en conformité avec la conception, ou faire évoluer les diagrammes pour documenter ces choix.

**US01 (connexion par token), back-end fait et vérifié — 2026-09-14.** Sécurité par JWT sur le modèle du cours Spring avancé (`fr.eni.demo`, dossiers `security/` et `security/jwt/`), adaptée à la hiérarchie `User` existante : `User implements UserDetails` directement (pas d'entité de sécurité séparée), `POST /api/auth/login` et `GET /api/auth/me`, `BCryptPasswordEncoder` explicite (les hachages du seed sont du BCrypt brut, sans préfixe), `AuthenticationEntryPoint` en 401 sur les routes protégées, filtre JWT tolérant aux tokens expirés/invalides (401, pas 500). Le rôle est renvoyé par le login et lisible côté front. Vérifié à chaud (curl) avec les quatre comptes du seed, y compris le formateur dont l'association `Teacher.sector` est `LAZY` (d'où le `@Transactional(readOnly = true)` sur `AuthenticationService`). Tests dans `backend/src/test/java/com/eni/formagest/security/TestAuthentification.java`, un par critère d'acceptation. Pas de route `/logout` : le token étant sans état côté serveur, la déconnexion se fait entièrement côté navigateur.

La divergence notée plus haut entre le diagramme de séquence (qui écrivait « token stocké dans cookie ») et l'implémentation est résolue : le diagramme a été corrigé pour dire `sessionStorage`, conforme au choix retenu (token en `sessionStorage`, envoyé en en-tête `Authorization: Bearer`), aucune protection CSRF n'étant nécessaire dans ce schéma.

Reste sur US01 : le front (service Angular, intercepteur `Authorization`, guard, page de connexion, tests Vitest).

## Ce qui reste à produire

**UML (en bonus, fortement recommandé)**

1. Diagramme de cas d'utilisation **détaillé** sur « Inscrire un élève à un cours » : `«include»` vérifier l'unicité (RG08), `«include»` vérifier l'ordre pédagogique (RG09), `«extend»` forcer l'inscription (RG10). C'est le cas qui porte toute la logique métier du sujet.
2. Fiche de cas d'utilisation textuelle correspondante (trame ENI : Cas d'utilisation / Résumé / Acteur principal / Préconditions / Postconditions / Déclencheur / Scénario nominal / Scénarios alternatifs).

**Puis** : maquettes, résorber la divergence conception/code ci-dessus, suite du développement (front de US01, puis contrôleurs et services des autres user stories).

## Socle technique et mono-repo

Le dépôt est un mono-repo. Structure d'accueil figée le 2026-09-09, avant que chacun pousse son squelette : `backend/` reçoit l'application Spring Boot avec son `Dockerfile` (build multi-étapes JDK 21 puis JRE 21) ; `frontend/` reçoit l'application Angular avec son `Dockerfile` (Node 22 puis `nginx:alpine`) et un `nginx.conf` qui fait le repli SPA vers `index.html` ; `docker-compose.yml` à la racine orchestre `db` (MySQL 8.4), `backend` et `frontend` ; `.env.example` sert de modèle, copié en `.env` non versionné et lu automatiquement par Compose (identifiants MySQL, ports, `APP_JWT_SECRET`). Le `README.md` documente les deux modes de lancement, tout Docker ou base en conteneur et back/front à la main.

Matrice de versions retenue. Le back-end est aligné sur le cours « Spring avancé » (projets `cave_a_vin` et `fr.eni.demo`, tous deux passés en Spring Boot 4). Pour le front, le binôme a acté de rester sur la version produite par `ng new` au moment du scaffold (Angular 22), plus récente que le TP de référence resté en Angular 21 : les patterns enseignés dans le cours — composants standalone, guards et interceptors fonctionnels, `inject()`, `app.config.ts`, signals, control flow `@if` / `@for`, nommage kebab-case — sont identiques de la 19 à la 22. Seul le thème Angular Material diffère : les API Sass Material 2 sont supprimées en v22, le thème FormaGest est donc écrit en Material 3 (`mat.theme()`).

| Brique | Version | Source |
|--------|---------|--------|
| JDK | 21 | toolchain de tous les projets des deux cours back-end |
| Build back-end | Gradle + wrapper 9.4.1 | `cave_a_vin` et `fr.eni.demo` — aucun projet en Maven, contrairement à ce que laisse entendre la note « conventions » plus bas qui parle de `pom.xml` |
| Spring Boot | 4.0.5 | `cave_a_vin` et `fr.eni.demo`. Boot 4 renomme le starter web : `spring-boot-starter-webmvc`, pas `-web` — piège au moment de recopier du code du cours back-end (resté en Boot 3) |
| jjwt | 0.13.0 | démo `fr.eni.demo` (`jjwt-api` / `-impl` / `-jackson` en 0.13.0) |
| MySQL | 8.4 | MPD (`docs/diagrams/MPD.sql`) |
| Node | 22 (≥ 22.22.3) | build du front ; 22.22.3 est le minimum exigé par le CLI Angular 22 |
| Angular | 22.1 | `ng new` (dernier stable au scaffold) ; le TP de réf. `youtube-playlist` est en 21, écart sans impact sur les patterns |
| TypeScript | 6.0 | imposé par Angular 22 |
| Angular Material / CDK | 22.1 | socle UI retenu (issue #33) ; thème applicatif en Material 3 |
| Test front | Vitest | runner par défaut d'Angular 22 (`@angular/build:unit-test`), remplace Karma/Jasmine |

Le TP Angular de référence est généré avec l'option SSR mais ne s'en sert pas (`main.server.ts` et `app.config.server.ts` vides, `outputMode: static`, hydratation non fournie). Le front FormaGest est donc une SPA sans SSR et l'image de production est un simple `nginx:alpine`. Pas d'outil d'administration de la base dans le compose : le binôme passe par l'outil intégré d'IntelliJ ou par DBeaver.

## Gestion du schéma de base

Décision du binôme (2026-09-10) : on suit l'approche des démos du cours Spring avancé, pas d'outil de migration façon Doctrine. Le schéma est généré par Hibernate au démarrage depuis les entités JPA. `application.properties` porte `spring.jpa.hibernate.ddl-auto=update` et `spring.jpa.show-sql=true`, comme `fr.eni.demo` et le TP `cave_a_vin`. Ni Flyway ni Liquibase — aucun des deux n'est enseigné, il n'y a pas de pattern à recopier.

Le cycle d'ajout d'une table : écrire la classe entité (`@Entity`, `@Table`, `@Id`, `@Column`, associations), écrire l'interface `XxxRepository extends JpaRepository<Xxx, TypeId>`, démarrer l'application. Pas de SQL à écrire, pas de table à créer à la main. Le jeu de données de départ passe par `src/main/resources/data.sql`.

Écarts par rapport aux projets du cours, qui tournent sur SQL Server : la datasource vise MySQL 8.4 (`jdbc:mysql://…`, driver `mysql-connector-j`) et le starter web de Boot 4 est `spring-boot-starter-webmvc`. Le `docs/diagrams/MPD.sql` ne crée plus les tables ; il reste une référence de conception et sert à rédiger le `data.sql`.

**Fixtures (2026-09-11).** `backend/src/main/resources/data.sql` porte le jeu de données de démarrage : 4 utilisateurs de démonstration (un par acteur, mot de passe commun `Formagest2026!` haché en BCrypt) et le catalogue de référence D2WM/CDA (filière, cursus, cours, progression pédagogique) issu de `docs/reference-formations.md`. Deux propriétés accompagnent le fichier dans `application.properties` : `spring.jpa.defer-datasource-initialization=true` (Hibernate crée le schéma avant que le script s'exécute) et `spring.sql.init.mode=always` (sans quoi Spring Boot n'exécute `data.sql` que sur base embarquée, jamais sur MySQL). Chaque insertion est en `INSERT IGNORE` : le volume Docker `db-data` persiste la base d'un `docker compose up` à l'autre, et le script est réexécuté à chaque démarrage — `INSERT IGNORE` le rend sans effet une fois les données déjà présentes, sans quoi les clés uniques (email, noms de cours, couples cursus/cours) feraient échouer le démarrage au deuxième lancement. Repartir d'une base vierge : `docker compose down -v`.

Le cursus CDA répète l'intitulé « Analyse et Conception / Oracle Data Modeler » aux positions 13 et 26 (cf. `reference-formations.md`, répétition volontaire). `TRACK_COURSE` (unicité cursus/cours) et `SCHEDULED_COURSE` (unicité promotion/cours) interdisant toutes deux qu'un même cours apparaisse deux fois dans une même progression, arbitrage binôme du 2026-09-11 : la seconde occurrence est désambiguïsée dans le seed en « Analyse et Conception / Oracle Data Modeler (approfondissement) » plutôt que d'assouplir ces contraintes.

## Points de vigilance sur la modélisation

**`Cours` et `Cours planifié` sont deux entités distinctes.** `Cours` est l'élément de catalogue, ordonné dans un cursus. `Cours planifié` est son occurrence à une date précise dans une promotion. Un même cours peut être planifié dans plusieurs promotions. C'est `Cours planifié` qui alimente le calendrier de l'élève. Confondre les deux casse tout le modèle.

**`Inscription` est une entité**, pas une simple association. Elle porte la date d'inscription et l'indicateur de forçage, et peut viser soit une promotion, soit un cours planifié isolé.

**Casse des identifiants SQL.** La stratégie de nommage physique de Spring Boot (`SpringPhysicalNamingStrategy`) passe tout nom de table et de colonne en snake_case minuscule au moment de la génération du schéma par Hibernate, même quand `@Table`/`@Column` donne un nom explicite en majuscules (`@Table(name = "APP_USER")` produit bien la table `app_user`). Sans incidence sur les colonnes, MySQL les traitant sans distinction de casse quelle que soit la plateforme, mais déterminant pour les noms de table sur ce projet (conteneur Linux, table casse sensible) : tout SQL écrit à la main contre ce schéma — `data.sql`, requêtes natives — doit utiliser les noms de table en minuscules effectivement créés, pas la casse indiquée dans l'entité. Piège rencontré en écrivant le seed (`data.sql`) : `INSERT INTO APP_USER` échoue avec « table doesn't exist », `INSERT INTO app_user` fonctionne.

Le calendrier personnel n'est pas une donnée stockée : c'est le résultat de l'agrégation des inscriptions.

## Outils et conventions

**UMLET** pour les diagrammes UML.
Binaire : `~/Library/Mobile Documents/com~apple~CloudDocs/Documents Elodie/Formation/Développement web/Formation Concepteur Développeur d'Applications/Analyse et conception/Analyse et conception/Umlet/umlet.jar`
Lancement : `java -jar umlet.jar` (Java 11 installé).
Export : `java -jar umlet.jar -action=convert -format=svg -filename=<nom>.uxf -output=<nom>` — **SVG uniquement** (vectoriel, net à tout zoom, cohérent avec les diagrammes déjà produits).

Stéréotypes : écrire `«include»` et `«extend»` (au singulier, avec guillemets), pas `includes` / `extends`.

**Langue des modèles.** Identifiants techniques (classes, méthodes, attributs, routes HTTP, valeurs d'enum) en **anglais** ; tout le reste — titres de diagrammes, libellés d'actions d'acteur, gardes, messages en langage naturel — en **français**. Choix assumé, à appliquer sur tous les diagrammes.

**Diagrammes de séquence (élément `UMLSequenceAllInOne` d'UMLet).** Pièges rencontrés :

- `combinedFragment=<type>~<id> <g> <d>` : `<g>` et `<d>` sont les **bornes** gauche et droite du cadre, pas la liste des lignes de vie. Lister toutes les lignes de vie fait échouer le parsing.
- Les deux branches d'un `alt` sont lues séquentiellement : impossible de fermer (`off=`) une même barre d'activation dans les deux branches. Ouvrir les activations à la descente (`on=`), ne fermer que celles équilibrées linéairement, laisser UMLet terminer les autres en bas de ligne de vie.
- Message réflexif : durée obligatoire, entier en fin de ligne (`a->>>a : texte 1`), sinon « duration must be greater than 0 ».
- Garde d'un compartiment : `<ligne de vie>:[texte]`, sans parenthèses ni ponctuation exotique dans les crochets.
- Zones front / back : rectangles `UMLClass` (ou `UMLFrame`) posés *derrière* l'élément séquence, bordure pointillée (`lt=.`), trait fin (`lw=0.5`), fond très pâle (`bg=#EDF2FA`). L'élément séquence étant transparent, la teinte passe sous les messages.

**Arborescence de documentation** :

```
docs/
├── analyse-conception.md
└── diagrams/    ← *.md (mermaid) + SVG exportés, nommage kebab-case anglais :
                    system-use-case-diagram, global-use-case-diagram,
                    sequence-diagram-login
```

**Sources `.uxf` non versionnées.** `*.uxf` est dans le `.gitignore` : les `.uxf` restent en local dans `docs/diagrams/`. Le SVG produit par UMLet (Batik) n'étant pas réouvrable dans UMLet, sauvegarder les `.uxf` par ailleurs (iCloud).

**Ce qui entre dans le dépôt selon le diagramme.** Les deux diagrammes de cas d'utilisation sont versionnés en `.md` (mermaid) **et** en `.svg` exporté ; après toute modif d'un `.uxf`, réexporter le SVG et le committer. Le diagramme de séquence n'est versionné qu'en `.md` : `sequence-diagram-login.svg` est explicitement dans le `.gitignore`, on ne le commite pas.

## Supports de cours — analyse et conception

Les cours ENI d'analyse et conception sont ici :
`~/Library/Mobile Documents/com~apple~CloudDocs/Documents Elodie/Formation/Développement web/Formation Concepteur Développeur d'Applications/Analyse et conception/Analyse et conception/Cours/`

Fichiers utiles :

- `M02/M02 - UML - Unified Modeling Language.pdf` — bases UML
- `M03/M03 - L'expression initiale du besoin.pdf` — dictionnaire des données, diagrammes de cas d'utilisation système et global, liste d'exigences, document de vision
- `M04/M04 - L'analyse détaillée des exigences.pdf` — cas d'utilisation détaillé, trame textuelle, scénarios nominal et alternatifs, maquettage, diagramme de classes d'analyse
- `M04/M04_TP01 - Solution/Proposition/` — exemples corrigés : fiches de cas d'utilisation en `.xlsx` et un diagramme `.uxf` réel
- `M06/M06 - Analyse des données.pdf` — MCD, MRD, MPD

En cas de doute sur un formalisme ou une trame attendue, se référer à ces supports plutôt qu'à une convention UML générique : c'est sur eux que le jury évalue.

## Supports de cours — développement

Trois cours ENI couvrent la partie technique et servent de modèle de code : le jury connaît ces conventions, s'en écarter sans raison est un mauvais calcul. Les deux premiers portent le back-end, le troisième le front Angular.

Le cours **back-end Java Spring Boot** est ici :
`~/Library/Mobile Documents/com~apple~CloudDocs/Documents Elodie/Formation/Développement web/Formation Concepteur Développeur d'Applications/Développement Web Back End Java Spring Boot/`

On y trouve un PDF par leçon (`M03` à `M07`), une `Spring Boot fiche revision.pdf`, et surtout un dossier `Projets Spring Boot/` avec un projet corrigé par notion : `m03*` pour le couplage et l'injection de dépendances, `m04*` pour le mapping des requêtes, `@PathVariable` et la validation, `m05*` pour l'accès aux données en JDBC et `jdbcTemplate`, `m06*` pour la couche métier et les transactions, `m07*` pour Spring Security. Ce cours enseigne toutefois l'accès aux données en `jdbcTemplate` et une sécurité **par session** (`JdbcUserDetailsManager`, formulaire de login) : ce n'est pas ce qu'il faut pour ce projet.

Le cours **Spring avancé** est ici :
`~/Library/Mobile Documents/com~apple~CloudDocs/Documents Elodie/Formation/Développement web/Formation Concepteur Développeur d'Applications/Spring avancé/`

Son dossier `Cours/` contient les PDF M01 rappels, M02 Spring Data (JPA), M03 Spring Data MongoDB, M04 Web Service, M06 Sécurité. `TP/` contient les énoncés Cave à Vin (Spring Data JPA) et Gestion Avis (MongoDB). `Démo/` contient les projets corrigés.

Le projet de référence est **`Spring avancé/Démo/demo/`** (package `fr.eni.demo`), le plus complet et le plus proche de ce qu'on doit produire. Il contient :

- `bo/` — entités JPA, avec `bo/association/` (exemples `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`), `bo/heritage/TestHeritageJoined.java` (héritage en stratégie `JOINED`, directement utile pour la hiérarchie utilisateur), `bo/pk/` (clés composites) ;
- `dal/` — interfaces `*Repository` Spring Data, plus des tests de requêtes JPQL et SQL natif ;
- `exception/AppExceptionHandler.java` — gestionnaire d'exceptions global (`@RestControllerAdvice`) ;
- `security/` et `security/jwt/` — implémentation complète de l'authentification par token JWT : `SecurityConfig` (chaîne de filtres `STATELESS`, autorisations par rôle et par verbe HTTP), `JwtAuthenticationFilter` (lecture de l'en-tête `Bearer`), `jwt/JwtService` (génération et validation du token avec la bibliothèque `jjwt` 0.13.0, clé lue dans `app.jwt.secret`), `jwt/AuthenticationService` et `AuthenticationController` (`POST .../auth`), `jwt/AuthenticationRequest` et `AuthenticationResponse`, `jwt/UserInfo` (`implements UserDetails`, `@Entity @Table(name="users")`), `jwt/UserInfoRepository`.

Pour la sécurité par token, ce sont ce dossier et le PDF `Spring avancé/Cours/M06 - Securité.pdf` qui font foi, pas le module M07 du cours back-end.

Conventions de code back-end, constantes dans tous ces projets :

- package racine `fr.eni.<projet>`, sous-packages `bo` (entités), `bll` (services : interface `XxxService` et implémentation `XxxServiceImpl`), `dal` (`XxxRepository` en Spring Data, ou `XxxDAO` et `XxxDAOImpl` en jdbcTemplate), `controller`, `security`, `exception` ;
- Lombok systématique : `@Data` seul, ou la combinaison `@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString @EqualsAndHashCode @Builder` ;
- une `BusinessException` maison pour signaler les violations de règles de gestion — c'est le mécanisme tout trouvé pour RG08, RG09 et RG10 ;
- les routes sont préfixées par un segment applicatif (`/eniecole/...` dans les démos).

Le cours **Angular** est ici :
`~/Library/Mobile Documents/com~apple~CloudDocs/Documents Elodie/Formation/Développement web/Formation Concepteur Développeur d'Applications/Angular/`

Les PDF de cours numérotés sont dans `Cours/` (composants, affichage dynamique, services, routes, formulaires, API REST) et `Bonus Support de cours/` (guards, RxJS, signals, validators custom, best practices). Les démos suivies en cours sont dans `Demo/M01` à `Demo/M03` et dans les dossiers `M01/` à `M05/`. Les TP corrigés les plus utiles pour le socle :

- `TP Guard/tp-guard/` — service d'authentification, guard fonctionnel, routes protégées, navbar. C'est le squelette login/guard le plus proche de ce qu'on doit faire. Projet en Angular 21.
- `TP Final Youtube Playlist/correction/` — application complète en Angular 19 : `AuthService` à base de `BehaviorSubject` et `user$`, `authGuard` qui renvoie un `Observable<boolean>`, appels `HttpClient` réels avec `HttpParams` et `.pipe(map())`, `environments/`, dossier `types/` pour les interfaces.
- `demo2/` — projet Angular 21 nu avec `.prettierrc` et `.editorconfig`, utile comme référence de configuration.

Conventions Angular du cours : composants **standalone** (pas de NgModule), guards et interceptors **fonctionnels** avec `inject()`, configuration dans `app.config.ts` (`provideRouter`, `provideHttpClient`), tableau de routes dans `app.routes.ts` avec `canActivate: [guard]`, `title`, et une route `**`. Le nommage de fichiers a changé entre Angular 19 (`auth.service.ts` → `AuthService`, `auth.guard.ts`, dossier `types/`) et Angular 20+ (`auth.ts` → `Auth`, `user-guard.ts`, sans suffixe) : choisir une des deux conventions et s'y tenir. Convention retenue pour FormaGest, celle du TP `Angular/mon TP/youtube-playlist/youtube-playlist` : fichiers en kebab-case avec suffixe de rôle (`auth-service.ts` exportant `AuthService`, `auth-guard.ts` exportant `authGuard`), dossiers `Services/`, `Guards/`, `Types/`, `Components/`, `Pipes/`. État d'authentification stocké côté client dans `sessionStorage` ou `localStorage`. Le squelette généré par `ng new` en v22 suit l'autre convention (sans suffixe : `app.ts` exporte `App`) ; les premiers fichiers créés sont donc à renommer pour coller à la convention retenue.

Ce que le cours Angular ne couvre pas : aucun exemple d'appel de login vers un vrai back-end (les TP utilisent `localStorage`), et aucun interceptor qui pose l'en-tête `Authorization: Bearer`. Ces deux points sont à écrire à partir de la doc Angular, ce sont les seules pièces du socle front sans modèle dans les supports.

## Style de rédaction

Le dossier est rédigé par une étudiante, pas généré. À éviter dans toute production écrite :

- les paragraphes d'une seule phrase enchaînés ;
- le gras systématique sur les mots-clés (le réserver au vocabulaire métier introduit pour la première fois) ;
- les traits de séparation entre chaque sous-partie ;
- les listes à puces parfaitement parallèles partout ;
- la redondance entre sections qui redisent la même règle sous trois formes.

Préférer des paragraphes construits, des tournures variées, et une liste à puces seulement quand le contenu est réellement une énumération.

## Points encore ouverts

Périmètre du Formateur et de l'Administrateur, validation du hors périmètre, question de la désinscription (absente du cahier des charges) : les user stories US16 à US18 de `docs/analyse-conception.md` renvoient à un « §6 » qui n'existe plus dans la version actuelle du fichier (celui-ci s'arrête à la section 5, User stories) — pointeur à corriger ou section à rétablir, à voir avec le binôme. Le nommage DWWM/D2WM est tranché par le texte officiel du sujet, qui écrit **D2WM** ; à répercuter partout, sous réserve de confirmation du binôme.

**Modélisation des utilisateurs — tranchée.** Le dictionnaire des données (§4 de `docs/analyse-conception.md`) présentait déjà la cible retenue : `Utilisateur` porte un attribut `rôle` **et** généralise Élève, Formateur, Référente administrative et Administrateur (héritage). C'est cette combinaison, pas l'alternative « entité unique + enum, sans héritage » un temps envisagée, qui est implémentée : `User` abstrait en stratégie `JOINED`, avec un champ persisté `role` (enum `UserRole`), et les quatre sous-types en classes filles (`bo/users/`). `Teacher` porte en plus une association obligatoire vers `Sector` (filière de rattachement du formateur) — absente du dictionnaire des données et de la fiche `§2.3 Formateur` actuels, à y ajouter.

Ne pas trancher seul ces points : ils relèvent d'un arbitrage avec le binôme.

## Retour de la prof (2026-09-14) — réouvre plusieurs points marqués « terminé »

Point d'étape avec la prof, quatre corrections à instruire avant de considérer US01 clos côté sécurité. Rien n'est encore implémenté.

1. **Stockage du token — fait, vérifié 2026-09-15.** Cible atteinte : cookie `HttpOnly` posé par le back, sur le modèle de https://www.bezkoder.com/angular-15-spring-boot-jwt-auth. `AuthController.login` pose le cookie via `JwtService.generateJwtCookie` (`ResponseEntity` + en-tête `Set-Cookie`), `POST /api/auth/logout` le vide via `generateCleanJwtCookie`, `JwtAuthenticationFilter.parseJwt` lit le cookie (nom en propriété `app.jwt.cookie-name`) au lieu de l'en-tête `Authorization`. Front : `auth.service.ts` ne gère plus le token (seul l'objet `User` renvoyé par le login est gardé en `sessionStorage`, pour l'état d'UI, pas pour l'auth), `auth.interceptor.ts` réduit à `withCredentials: true` sur chaque requête, `auth.guard.ts` route sur `user$`.
2. **CORS/CSRF — tranché le 2026-09-14, sous réserve de confirmation prof.** Le token étant passé en cookie `HttpOnly`, le raisonnement « pas de cookie, pas besoin de CSRF » ne tenait plus. Décision du binôme (choix « pragmatique » plutôt que l'implémentation complète `CookieCsrfTokenRepository` + filtre dédié, sans précédent dans les cours suivis) : `csrf.disable()` reste en l'état dans `SecurityConfig`, désormais justifié par un commentaire dans le code et par un paragraphe dans `docs/analyse-conception.md` (§3, sous RG12) — API JSON-only (un `<form>` HTML ne peut pas forger une requête JSON sans JS, donc bloqué par CORS) + cookie `SameSite=Lax`. Formulation de la prof toujours ambiguë (rapportée par l'étudiante, jamais confirmée mot pour mot) : si elle voulait explicitement voir le filtre CSRF de Spring réactivé, cette justification ne suffira pas — à vérifier avec elle.
3. **Confusion classe/rôle — reprécisé par le binôme le 2026-09-15 : portée plus restreinte que le point 4 ne le laissait supposer.** La remarque de la prof visait la lisibilité de `TestHeritageUser` (les repositories de sous-type inutiles), pas une remise en cause structurelle du champ persisté `User.role`. Pas de suppression de colonne engagée : `User.role` reste tel quel, `getAuthorities()` continue de le lire directement (`"ROLE_" + role.name()`). La divergence conception/code sur ce point (diagramme de classes et MPD sans colonne rôle, code avec) reste ouverte, documentée dans « Divergence à trancher entre la conception et le code » plus haut — un sujet d'arbitrage binôme séparé, pas une action issue du retour de la prof.
4. **`TestHeritageUser` à simplifier.** Injecte aujourd'hui `UserRepository` + 4 repositories de sous-type (`StudentRepository`, `TeacherRepository`, `AdministrativeManagerRepository`, `AdministratorRepository`), tous vides (aucune méthode custom, vérifié). Le test devrait prouver le polymorphisme via `UserRepository` seul (`instanceof`/cast sur les résultats) plutôt que d'interroger un repository par sous-classe. Les quatre repositories de sous-type n'ont pas de raison d'exister en l'état (aucune query qui en a besoin) — à supprimer ou à garder seulement le jour où une requête propre à un sous-type apparaît.
5. **Annotations de rôle sur les contrôleurs — pattern posé le 2026-09-15 sur `SectorController`, à répliquer.** `SecurityConfig` a maintenant `@EnableMethodSecurity`. `SectorController` : `findAll` (GET) reste ouvert (consultation §4.1), `create`/`update`/`delete` portent `@PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")` (l'Administrateur n'a pas ce droit, cf. `docs/analyse-conception.md` §2.4 — son périmètre se limite aux comptes utilisateurs, US15). Le matcher de `SecurityConfig` a été resserré à `HttpMethod.GET, "/api/sectors/**"` pour `permitAll()`, le reste de `/api/sectors` retombant sur `.anyRequest().authenticated()` avant filtrage par rôle. Le commentaire `// TODO: restreindre les permissions une fois qu'il y aura des users avec roles` est gardé volontairement au-dessus de ce matcher, comme pense-bête à reproduire pour les prochains contrôleurs de gestion (cursus, cours, promotions, inscriptions) au fur et à mesure qu'ils prennent des routes d'écriture.
6. **Annotations de validation sur les DTO — faites pour `SectorDto`/`SectorController` (`@NotBlank`, `@Size`, `@Valid`), à reproduire pour les DTO restants.** `TrackDto`, `CourseDto` (vérifiés le 2026-09-15) n'ont toujours aucune contrainte — normal, ils n'ont pas encore de contrôleur qui les consomme. À ajouter en même temps que l'écriture de chaque contrôleur de gestion, sur le même modèle que `SectorController` (`@Valid @RequestBody` + contraintes Jakarta sur le DTO).

7. **DTO en `record` — suggestion de la prof, pas obligatoire, à réfléchir.** Les DTO actuels (`SectorDto`, `TrackDto`, `CourseDto`, etc.) sont des classes Lombok `@Data`/`@NoArgsConstructor`/`@Builder`. La prof a mentionné la possibilité de les écrire en `record` Java à la place — non imposé. Les annotations de validation Jakarta (`@NotBlank`, `@NotNull`...) fonctionnent sur les composants d'un `record` (portées sur le paramètre du constructeur canonique), donc compatible avec le point 6 : si ce changement se fait, le faire en même temps que l'ajout des validations plutôt qu'en deux passes. Point de vigilance côté binôme : un `record` est immuable, pas de setters ni de `@Builder` — à voir si ça convient aux usages actuels (`SectorDto.builder()...` dans les mappers et les tests) sans réécrire ces appels. Vérifié : aucun `record` dans `Spring avancé/Démo/demo` (`fr.eni.demo`, 51 fichiers Java), le cours de référence n'utilise que des classes Lombok pour ses DTO/BO — s'écarter de ce pattern ici serait donc un choix assumé, pas une convention à recopier.

Ordre proposé pour reprendre le développement : ~~rôle/classe (point 3, requalifié le 2026-09-15 en simple clarification du point 4)~~ → ~~cookie `HttpOnly` + CORS/CSRF (points 1-2, ✅ faits, vérifiés 2026-09-15 et 2026-09-14)~~ → sécurité par rôle sur les contrôleurs + validation sur les DTO, DTO en `record` ou non (points 5-6-7, à traiter ensemble au fil de l'écriture des contrôleurs de gestion) → ~~nettoyage du test d'héritage (point 4, ✅ fait le 2026-09-14)~~. Seuls les points 5-6-7 restent ouverts.
