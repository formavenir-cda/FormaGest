# Consultation du calendrier d'un élève

## Objectif

Permettre à l'élève de consulter son calendrier personnel, en lecture seule.
Aucune création, modification ou suppression de cours n'est proposée.

Le calendrier réunit les **cours de sa promotion** et les **cours auxquels il
est inscrit individuellement**. Il peut contenir l'une de ces sources, les deux,
ou aucun cours.

## Précondition

L'élève est déjà authentifié et possède un token valide. Le back-end
récupère son identifiant depuis l'utilisateur authentifié. L'authentification
est documentée dans un diagramme séparé.

## Diagramme de séquence

```mermaid
---
config:
  theme: base
  themeVariables:
    fontFamily: "Arial, sans-serif"
    fontSize: "14px"
    primaryColor: "#FFFFFF"
    primaryTextColor: "#1E293B"
    primaryBorderColor: "#94A3B8"
    lineColor: "#64748B"
    actorBkg: "#FFFFFF"
    actorBorder: "#94A3B8"
    actorTextColor: "#0F172A"
    actorLineColor: "#CBD5E1"
    signalColor: "#475569"
    signalTextColor: "#1E293B"
    noteBkgColor: "#F1F5F9"
    noteBorderColor: "#CBD5E1"
    noteTextColor: "#334155"
    labelBoxBkgColor: "#FFFFFF"
    labelBoxBorderColor: "#94A3B8"
    labelTextColor: "#334155"
    loopTextColor: "#334155"
    activationBkgColor: "#E2E8F0"
    activationBorderColor: "#94A3B8"
  sequence:
    mirrorActors: false
    actorMargin: 35
    width: 150
    height: 50
    boxMargin: 12
    boxTextMargin: 10
    noteMargin: 12
    messageMargin: 35
    wrap: true
    rightAngles: true
---
sequenceDiagram
    actor Student as Élève

    box rgb(239, 246, 255) Front - Angular
        participant Page as CalendarPage
        participant Api as CalendarApiService
    end

    box rgb(240, 253, 250) Back - Java / API et métier
        participant Controller as CalendarController
        participant Service as CalendarService
    end

    box rgb(248, 250, 252) Back - Java / Accès aux données
        participant StudentRepo as StudentRepository
        participant PromotionRepo as PromotionRepository
        participant CourseRepo as CourseRepository
    end

    Note over Student,Api: Précondition : élève authentifié<br/>avec un token valide

    Student->>Page: Ouvrir « Mon calendrier »
    Page->>Api: loadCalendar()
    Api->>Controller: GET /api/me/calendar<br/>Authorization: Bearer token

    Note over Controller: Identifiant issu de<br/>l'utilisateur authentifié
    Controller->>Service: getStudentCalendar(studentId)

    Service->>+StudentRepo: findById(studentId)
    StudentRepo-->>-Service: Élève ou aucun résultat

    alt Élève introuvable
        rect rgb(254, 226, 226)
            Service-->>Controller: StudentNotFoundException
            Controller-->>Api: 404 Not Found
            Api-->>Page: Erreur : élève introuvable
            Page-->>Student: Afficher « Élève introuvable »
        end

    else Élève trouvé

        Note over Service,CourseRepo: 1. COURS DE LA PROMOTION<br/>Sans promotion, cette liste reste vide

        Service->>+PromotionRepo: findPromotionByStudentId(studentId)
        PromotionRepo-->>-Service: Promotion ou aucune

        opt Une promotion est trouvée
            Service->>+CourseRepo: findCoursesByPromotionId(promotionId)
            CourseRepo-->>-Service: Cours de la promotion (ou liste vide)
        end

        Note over Service,CourseRepo: 2. COURS INDIVIDUELS<br/>Recherche effectuée avec ou sans promotion

        Service->>+CourseRepo: findCoursesByStudentId(studentId)
        CourseRepo-->>-Service: Cours directement liés à l'élève (ou liste vide)

        Note over Service,CourseRepo: 3. CONSTRUCTION DU CALENDRIER<br/>Regrouper, supprimer les doublons et trier par date

        Service->>Service: mergeCourses(<br/>promotionCourses, individualCourses)
        Service->>Service: removeDuplicates(courses)
        Service->>Service: sortByDate(courses)

        Service-->>Controller: Liste des cours du calendrier
        Controller-->>Api: 200 OK + calendrier
        Api-->>Page: Calendrier de l'élève

        Note over Page,Api: Si la liste est vide :<br/>afficher « Aucun cours planifié »
        Page-->>Student: Afficher le calendrier
    end
```

## Règles de fonctionnement

La recherche des cours individuels est effectuée **avec ou sans promotion**.
L'absence de promotion ou de cours n'est pas une erreur.

Les deux listes sont regroupées. Une même séance planifiée accessible par
les deux sources n'apparaît qu'une fois. Les cours sont ensuite triés par date
et heure de début.

| Situation | Résultat attendu |
| --- | --- |
| Des cours de promotion et des cours individuels | Afficher les deux sources, sans doublons. |
| Uniquement des cours de promotion | Afficher les cours de la promotion. |
| Uniquement des cours individuels | Afficher les cours individuels. |
| Aucun cours dans les deux sources | Renvoyer `200 OK` avec `[]` et afficher « Aucun cours planifié ». |
| Élève introuvable | Arrêter la consultation, renvoyer `404 Not Found` et afficher « Élève introuvable ». |

Seul le cas « Élève introuvable » est représenté comme une erreur dans
ce diagramme. Les autres erreurs techniques sont hors de son périmètre.