# Diagramme de classes métier


```mermaid
classDiagram
    direction TB

    %% =========================
    %% COMPTES
    %% =========================

    class User {
        Long id
        String lastName
        String firstName
        String email
        String password
        Role role
    }

    class Role {
        <<enumeration>>
        STUDENT
        TRAINER
        ADMINISTRATIVE_COORDINATOR
        ADMINISTRATOR
    }


    %% =========================
    %% STRUCTURE PEDAGOGIQUE
    %% =========================

    class TrainingTrack {
        Long id
        String name
    }

    class Cursus {
        Long id
        String name
    }

    class Course {
        Long id
        String title
    }

    class CursusCourse {
        Long id
        Integer order
    }


    %% =========================
    %% PLANIFICATION
    %% =========================

    class Cohort {
        Long id
        String name
        LocalDate startDate
        LocalDate endDate
    }

    class ScheduledCourse {
        Long id
        LocalDateTime startDateTime
        LocalDateTime endDateTime
    }


    %% =========================
    %% INSCRIPTIONS
    %% =========================

    class CohortRegistration {
        Long id
        LocalDateTime registrationDate
    }

    class CourseRegistration {
        Long id
        LocalDateTime registrationDate
        Boolean forced
    }


    %% =========================
    %% STRUCTURE PEDAGOGIQUE
    %% =========================

    TrainingTrack "1" --> "0..*" Cursus : regroupe

    Cursus "1" --> "1..*" CursusCourse : contient
    Course "1" --> "0..*" CursusCourse : est utilisé dans


    %% =========================
    %% PLANIFICATION
    %% =========================

    Cursus "1" --> "0..*" Cohort : est planifié en

    Cohort "1" --> "0..*" ScheduledCourse : contient

    CursusCourse "1" --> "0..*" ScheduledCourse : est planifié en


    %% =========================
    %% INSCRIPTIONS
    %% =========================

    User "1" --> "0..*" CohortRegistration : possède
    CohortRegistration "0..*" --> "1" Cohort : concerne

    User "1" --> "0..*" CourseRegistration : possède
    CourseRegistration "0..*" --> "1" ScheduledCourse : concerne


    %% =========================
    %% COULEURS
    %% =========================

    style User fill:#F1F5F9,stroke:#64748B,color:#0F172A
    style Role fill:#F1F5F9,stroke:#64748B,color:#0F172A

    style TrainingTrack fill:#EFF6FF,stroke:#60A5FA,color:#0F172A
    style Cursus fill:#EFF6FF,stroke:#60A5FA,color:#0F172A
    style Course fill:#EFF6FF,stroke:#60A5FA,color:#0F172A
    style CursusCourse fill:#EFF6FF,stroke:#60A5FA,color:#0F172A

    style Cohort fill:#F0FDFA,stroke:#2DD4BF,color:#0F172A
    style ScheduledCourse fill:#F0FDFA,stroke:#2DD4BF,color:#0F172A

    style CohortRegistration fill:#F5F3FF,stroke:#A78BFA,color:#0F172A
    style CourseRegistration fill:#F5F3FF,stroke:#A78BFA,color:#0F172A

```

## Explication du diagramme

Le diagramme représente les principales entités métier de l'application de gestion pédagogique.

### Comptes utilisateurs

`User` représente un compte utilisateur de l'application.

Le type d'utilisateur est défini par `Role`, qui permet de distinguer :

- `STUDENT` : élève ;
- `TRAINER` : formateur ;
- `ADMINISTRATIVE_COORDINATOR` : référente administrative ;
- `ADMINISTRATOR` : administrateur.

---

### Structure pédagogique

`TrainingTrack` représente une filière.

Une filière regroupe plusieurs `Cursus`.

`Course` représente un cours du catalogue pédagogique.

`CursusCourse` permet d'associer un `Course` à un `Cursus` tout en conservant
sa position dans la progression pédagogique grâce à l'attribut `order`.

Cette classe permet notamment à un même cours d'être utilisé dans plusieurs
cursus ou à plusieurs positions différentes.

---

### Planification

Un `Cursus` peut être planifié dans le temps afin de créer une `Cohort`,
qui représente une promotion.

Les cours réellement programmés pour cette promotion sont représentés par
`ScheduledCourse`.

Un `ScheduledCourse` contient notamment une date et une heure de début ainsi
qu'une date et une heure de fin.

---

### Inscriptions pédagogiques

Un élève peut être inscrit de deux manières.

`CohortRegistration` représente l'inscription d'un élève à une promotion
complète.

`CourseRegistration` représente l'inscription d'un élève à un cours planifié
spécifique.

L'attribut `forced` permet d'indiquer qu'une inscription individuelle a été
autorisée malgré le non-respect de l'ordre pédagogique.

---

### Calendrier de l'élève

Le calendrier personnel de l'élève n'est pas stocké comme une entité.

Il est construit à partir de deux sources :

- les `ScheduledCourse` de la `Cohort` à laquelle l'élève est inscrit ;
- les `ScheduledCourse` auxquels l'élève est inscrit individuellement.

Le calendrier peut donc contenir des cours provenant de la promotion,
des cours individuels, ou les deux.