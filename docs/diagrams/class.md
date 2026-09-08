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

Lecture du modèle

Comptes. User représente un compte de l'application. Role distingue
les élèves, les formateurs, la référente administrative et l'administrateur.
Les associations vers les inscriptions concernent uniquement les utilisateurs
ayant le rôle STUDENT : elles représentent l'élève inscrit, pas la personne
qui effectue l'inscription.

Pédagogie. TrainingTrack regroupe les cursus. Course définit un cours
du catalogue et CursusCourse sa place dans un cursus, grâce à order.

Planification. Cohort représente une promotion. ScheduledCourse
représente une occurrence datée d'un cours pour cette promotion.

Inscriptions. CohortRegistration donne accès aux cours planifiés
de la promotion. CourseRegistration associe directement l'élève à un
cours planifié, sans l'inscrire à la promotion complète. Le calendrier
regroupe les deux sources et n'est pas une entité persistée dans ce modèle.

Responsabilités

L'administrateur gère les comptes utilisateurs de l'application.
La référente administrative gère les données pédagogiques, la planification
et les inscriptions des élèves à une promotion ou à un cours.
L'élève dispose uniquement d'un accès en consultation.

Contraintes métier retenues

Double inscription : un élève ne peut pas être inscrit deux fois au
même ScheduledCourse, y compris en combinant les deux sources. Le contrôle
doit s'appliquer quel que soit l'ordre des inscriptions ; supprimer un
doublon du calendrier ne remplace pas ce contrôle.

Ordre pédagogique : une inscription individuelle respecte l'ordre
défini par CursusCourse.order, sauf forçage par la référente
administrative (forced = true). Le forçage n'autorise jamais un doublon.

Cohérence de planification : le CursusCourse d'un ScheduledCourse
appartient au même Cursus que sa Cohort.

Ces contraintes complètent les associations du diagramme et doivent être
contrôlées dans l'application.

Choix de cardinalité

Une promotion peut être créée avant la planification de ses cours : elle
contient donc 0..* cours planifiés. Le cursus représenté est un programme
constitué d'au moins une occurrence de cours (1..*).

La cardinalité 0..* des inscriptions aux promotions conserve le modèle
retenu. Elle ne fixe pas à elle seule une limite de promotions simultanées.