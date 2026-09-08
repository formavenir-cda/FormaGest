# Consultation du calendrier d'un élève

## Objectif

Permettre à un élève authentifié de consulter son calendrier personnel en
lecture seule. Le calendrier regroupe les cours de sa promotion et les cours
auxquels il est inscrit individuellement.

## Précondition

L'élève est authentifié et possède un token JWT valide. Le système retrouve son
identité à partir du contexte de sécurité, sans recevoir d'identifiant d'élève
dans l'URL.

## Diagramme de séquence

```mermaid
---
config:
  theme: base
  themeVariables:
    background: "#FFFFFF"
    mainBkg: "#FFFFFF"
    textColor: "#0F172A"
    fontFamily: "Arial, sans-serif"
    fontSize: "14px"
    primaryColor: "#F8F7FF"
    primaryTextColor: "#312E81"
    primaryBorderColor: "#A78BFA"
    lineColor: "#8B5CF6"
    actorBkg: "#F8F7FF"
    actorBorder: "#8B5CF6"
    actorTextColor: "#312E81"
    actorLineColor: "#A78BFA"
    signalColor: "#475569"
    signalTextColor: "#334155"
    noteBkgColor: "#F5F3FF"
    noteBorderColor: "#A78BFA"
    noteTextColor: "#3730A3"
    labelBoxBkgColor: "#F8F7FF"
    labelBoxBorderColor: "#A78BFA"
    labelTextColor: "#3730A3"
    loopTextColor: "#3730A3"
    activationBkgColor: "#EDE9FE"
    activationBorderColor: "#8B5CF6"
  sequence:
    mirrorActors: true
    actorMargin: 60
    width: 180
    height: 50
    boxMargin: 30
    noteMargin: 15
    messageMargin: 40
    wrap: true
    rightAngles: true
---
sequenceDiagram
    box rgb(250, 250, 255) Utilisateur
        actor Student as Élève
    end

    box rgb(239, 246, 255) Front
        participant Page as CalendarPage
    end

    box rgb(245, 243, 255) Back
        participant Controller as CalendarController
        participant Service as CalendarService
    end

    Note over Student,Page: Précondition<br/>Élève authentifié avec un JWT valide

    Student->>Page: Ouvrir « Mon calendrier »
    activate Page
    Page->>+Controller: GET /api/me/calendar<br/>Authorization: Bearer JWT

    Controller->>+Service: getCalendar(studentId)

    Note over Controller,Service: Requête unique avec jointures<br/>Promotion + inscriptions individuelles<br/>Sans doublon · Tri par date de début

    Service-->>-Controller: Cours planifiés triés et sans doublon
    Controller-->>-Page: 200 OK + calendrier

    alt Calendrier disponible
        Page-->>Student: Afficher les cours chronologiquement
    else Calendrier vide
        Page-->>Student: Afficher « Aucun cours planifié »
    end

    deactivate Page
```

## Règles de fonctionnement

Le système exécute une seule requête de lecture. Ses jointures permettent de
récupérer simultanément :

- les cours planifiés de la promotion de l'élève ;
- les cours planifiés auxquels l'élève est inscrit individuellement.

Le résultat de cette requête est directement dédoublonné et trié par date et
heure de début. Le diagramme montre la responsabilité de chaque couche jusqu'au
service métier, sans détailler les repositories ni les traitements techniques
internes d'accès aux données.

Le token JWT est transmis dans l'en-tête HTTP `Authorization` avec le schéma
`Bearer`. Sa validité est contrôlée avant l'exécution du cas d'utilisation. Le
contrôleur utilise ensuite l'identité authentifiée issue du contexte de sécurité,
ce qui empêche l'élève de demander le calendrier d'un autre utilisateur en
modifiant un identifiant dans l'URL.

| Situation | Résultat attendu |
| --- | --- |
| Cours de promotion et cours individuels | Afficher les deux sources, sans doublon et par ordre chronologique. |
| Uniquement des cours de promotion | Afficher les cours de la promotion. |
| Uniquement des cours individuels | Afficher les cours individuels. |
| Aucun cours | Afficher « Aucun cours planifié ». |
