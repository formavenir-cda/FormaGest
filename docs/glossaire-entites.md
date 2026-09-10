# Glossaire des entités — correspondance code / métier

État du modèle JPA au 2026-09-10. 14 entités concrètes, 14 tables.

## Utilisateurs — package `bo.users`

| Classe | Table | Métier |
|--------|-------|--------|
| `User` | `APP_USER` | Utilisateur |
| `Student` | `STUDENT` | Élève |
| `Teacher` | `TEACHER` | Formateur |
| `AdministrativeManager` | `ADMINISTRATIVE_MANAGER` | Référente administrative |
| `Administrator` | `ADMINISTRATOR` | Administrateur |

## Structure pédagogique et planification — package `bo.training`

| Classe | Table | Métier |
|--------|-------|--------|
| `Sector` | `SECTOR` | Filière |
| `Track` | `TRACK` | Cursus |
| `Course` | `COURSE` | Cours (catalogue) |
| `TrackCourse` | `TRACK_COURSE` | CursusCours |
| `Cohort` | `COHORT` | Promotion |
| `ScheduledCourse` | `SCHEDULED_COURSE` | Cours planifié |
| `CohortStatus` | — | Statut de promotion |

`CohortStatus` est un enum, pas une table : valeurs `UPCOMING` / `IN_PROGRESS` / `COMPLETED`.

## Inscriptions — package `bo.enrollment`

| Classe | Table | Métier |
|--------|-------|--------|
| `Enrollment` | `ENROLLMENT` | Inscription |
| `CohortEnrollment` | `COHORT_ENROLLMENT` | InscriptionPromotion |
| `ScheduledCourseEnrollment` | `SCHEDULED_COURSE_ENROLLMENT` | InscriptionCours |
