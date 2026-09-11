-- ============================================================
-- Jeu de données de démarrage (fixtures)
-- ============================================================

-- ============================================================
-- Utilisateurs de démonstration (un par acteur du sujet)
-- Mot de passe commun de démo : Formagest2026!
-- ============================================================

INSERT IGNORE INTO app_user (id, email, lastname, firstname, password, active) VALUES
    (1, 'eleve.demo@formagest.fr', 'Martin', 'Lucas', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE),
    (2, 'formateur.demo@formagest.fr', 'Bernard', 'Claire', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE),
    (3, 'referente.demo@formagest.fr', 'Dubois', 'Sophie', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE),
    (4, 'admin.demo@formagest.fr', 'Petit', 'Thomas', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE);

INSERT IGNORE INTO student (id, birth_date) VALUES (1, '2000-04-12');
INSERT IGNORE INTO teacher (id) VALUES (2);
INSERT IGNORE INTO administrative_manager (id) VALUES (3);
INSERT IGNORE INTO administrator (id) VALUES (4);

-- ============================================================
-- Jeu de données de référence : filière, cursus, cours (D2WM, CDA)
-- ============================================================

-- --- Filière ---
INSERT IGNORE INTO sector (name) VALUES ('Développement');

-- --- Cursus ---
INSERT IGNORE INTO track (name, sector_id)
SELECT 'D2WM', s.sector_id FROM sector s WHERE s.name = 'Développement';
INSERT IGNORE INTO track (name, sector_id)
SELECT 'CDA', s.sector_id FROM sector s WHERE s.name = 'Développement';

-- --- Cours (catalogue, 37 intitulés uniques partagés entre cursus) ---
INSERT IGNORE INTO course (name) VALUES
    ('Algorithmique / Pseudo-Code'),
    ('Initiation à la Programmation / Java'),
    ('Web Client / HTML & CSS'),
    ('JavaScript initiation'),
    ('Projet Web / HTML & CSS + JS'),
    ('Programmation Orientée Objet / Java (partie 1)'),
    ('Programmation Orientée Objet / Java (partie 2)'),
    ('Langage SQL / SQL Server'),
    ('Notions Complémentaires / Java SE'),
    ('Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 1)'),
    ('Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 2)'),
    ('Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 3)'),
    ('Projet Web / Java Spring Boot (partie 1)'),
    ('Projet Web / Java Spring Boot (partie 2)'),
    ('Analyse et Conception / Oracle Data Modeler'),
    ('JavaScript avancé + initiation Framework JS / Angular'),
    ('Développement Web côté Serveur avec JavaScript / Node.js et NoSQL'),
    ('Développement Web côté Serveur (Back-End) / PHP'),
    ('Développement Web côté Serveur (Back-End) / Symfony (partie 1)'),
    ('Développement Web côté Serveur (Back-End) / Symfony (partie 2)'),
    ('Projet Web / Symfony (partie 1)'),
    ('Projet Web / Symfony (partie 2)'),
    ('CMS / WordPress'),
    ('CMS / WordPress + Projet Final'),
    ('Algorithmique + Initiation à la Programmation / Java'),
    ('SQL avancé / Transact SQL et Sécurité'),
    ('Gestion de projet et Communication'),
    ('Java Frameworks - API Web (Spring Security, ORM, …) (partie 1)'),
    ('Java Frameworks - API Web (Spring Security, ORM, …) (partie 2)'),
    ('Angular avancé / Angular'),
    ('Analyse et Conception / Oracle Data Modeler (approfondissement)'),
    ('Projet Fullstack - Web / Java Spring Boot + Angular (partie 1)'),
    ('Projet Fullstack - Web / Java Spring Boot + Angular (partie 2)'),
    ('Technologie Cross-Platform / Flutter'),
    ('DevOps - Infrastructure et déploiement d''applications'),
    ('Intelligence Artificielle / Python'),
    ('IA / Python + Projet Final');

-- --- Progression pédagogique : D2WM ---
INSERT IGNORE INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 1 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Algorithmique / Pseudo-Code'
UNION ALL
SELECT t.track_id, c.course_id, 2 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Initiation à la Programmation / Java'
UNION ALL
SELECT t.track_id, c.course_id, 3 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Web Client / HTML & CSS'
UNION ALL
SELECT t.track_id, c.course_id, 4 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'JavaScript initiation'
UNION ALL
SELECT t.track_id, c.course_id, 5 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Projet Web / HTML & CSS + JS'
UNION ALL
SELECT t.track_id, c.course_id, 6 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Programmation Orientée Objet / Java (partie 1)'
UNION ALL
SELECT t.track_id, c.course_id, 7 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Programmation Orientée Objet / Java (partie 2)'
UNION ALL
SELECT t.track_id, c.course_id, 8 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Langage SQL / SQL Server'
UNION ALL
SELECT t.track_id, c.course_id, 9 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Notions Complémentaires / Java SE'
UNION ALL
SELECT t.track_id, c.course_id, 10 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 1)'
UNION ALL
SELECT t.track_id, c.course_id, 11 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 2)'
UNION ALL
SELECT t.track_id, c.course_id, 12 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 3)'
UNION ALL
SELECT t.track_id, c.course_id, 13 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Projet Web / Java Spring Boot (partie 1)'
UNION ALL
SELECT t.track_id, c.course_id, 14 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Projet Web / Java Spring Boot (partie 2)'
UNION ALL
SELECT t.track_id, c.course_id, 15 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Analyse et Conception / Oracle Data Modeler'
UNION ALL
SELECT t.track_id, c.course_id, 16 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'JavaScript avancé + initiation Framework JS / Angular'
UNION ALL
SELECT t.track_id, c.course_id, 17 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Développement Web côté Serveur avec JavaScript / Node.js et NoSQL'
UNION ALL
SELECT t.track_id, c.course_id, 18 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Développement Web côté Serveur (Back-End) / PHP'
UNION ALL
SELECT t.track_id, c.course_id, 19 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Développement Web côté Serveur (Back-End) / Symfony (partie 1)'
UNION ALL
SELECT t.track_id, c.course_id, 20 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Développement Web côté Serveur (Back-End) / Symfony (partie 2)'
UNION ALL
SELECT t.track_id, c.course_id, 21 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Projet Web / Symfony (partie 1)'
UNION ALL
SELECT t.track_id, c.course_id, 22 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'Projet Web / Symfony (partie 2)'
UNION ALL
SELECT t.track_id, c.course_id, 23 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'CMS / WordPress'
UNION ALL
SELECT t.track_id, c.course_id, 24 FROM track t, course c WHERE t.name = 'D2WM' AND c.name = 'CMS / WordPress + Projet Final';

-- --- Progression pédagogique : CDA ---
INSERT IGNORE INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 1 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Algorithmique + Initiation à la Programmation / Java'
UNION ALL
SELECT t.track_id, c.course_id, 2 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Web Client / HTML & CSS'
UNION ALL
SELECT t.track_id, c.course_id, 3 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'JavaScript initiation'
UNION ALL
SELECT t.track_id, c.course_id, 4 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Programmation Orientée Objet / Java (partie 1)'
UNION ALL
SELECT t.track_id, c.course_id, 5 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Programmation Orientée Objet / Java (partie 2)'
UNION ALL
SELECT t.track_id, c.course_id, 6 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Langage SQL / SQL Server'
UNION ALL
SELECT t.track_id, c.course_id, 7 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Notions Complémentaires / Java SE'
UNION ALL
SELECT t.track_id, c.course_id, 8 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 1)'
UNION ALL
SELECT t.track_id, c.course_id, 9 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 2)'
UNION ALL
SELECT t.track_id, c.course_id, 10 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 3)'
UNION ALL
SELECT t.track_id, c.course_id, 11 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Projet Web / Java Spring Boot (partie 1)'
UNION ALL
SELECT t.track_id, c.course_id, 12 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Projet Web / Java Spring Boot (partie 2)'
UNION ALL
SELECT t.track_id, c.course_id, 13 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Analyse et Conception / Oracle Data Modeler'
UNION ALL
SELECT t.track_id, c.course_id, 14 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'JavaScript avancé + initiation Framework JS / Angular'
UNION ALL
SELECT t.track_id, c.course_id, 15 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Développement Web côté Serveur avec JavaScript / Node.js et NoSQL'
UNION ALL
SELECT t.track_id, c.course_id, 16 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Développement Web côté Serveur (Back-End) / PHP'
UNION ALL
SELECT t.track_id, c.course_id, 17 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Développement Web côté Serveur (Back-End) / Symfony (partie 1)'
UNION ALL
SELECT t.track_id, c.course_id, 18 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Développement Web côté Serveur (Back-End) / Symfony (partie 2)'
UNION ALL
SELECT t.track_id, c.course_id, 19 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Projet Web / Symfony (partie 1)'
UNION ALL
SELECT t.track_id, c.course_id, 20 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Projet Web / Symfony (partie 2)'
UNION ALL
SELECT t.track_id, c.course_id, 21 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'SQL avancé / Transact SQL et Sécurité'
UNION ALL
SELECT t.track_id, c.course_id, 22 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Gestion de projet et Communication'
UNION ALL
SELECT t.track_id, c.course_id, 23 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Java Frameworks - API Web (Spring Security, ORM, …) (partie 1)'
UNION ALL
SELECT t.track_id, c.course_id, 24 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Java Frameworks - API Web (Spring Security, ORM, …) (partie 2)'
UNION ALL
SELECT t.track_id, c.course_id, 25 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Angular avancé / Angular'
UNION ALL
SELECT t.track_id, c.course_id, 26 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Analyse et Conception / Oracle Data Modeler (approfondissement)'
UNION ALL
SELECT t.track_id, c.course_id, 27 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Projet Fullstack - Web / Java Spring Boot + Angular (partie 1)'
UNION ALL
SELECT t.track_id, c.course_id, 28 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Projet Fullstack - Web / Java Spring Boot + Angular (partie 2)'
UNION ALL
SELECT t.track_id, c.course_id, 29 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Technologie Cross-Platform / Flutter'
UNION ALL
SELECT t.track_id, c.course_id, 30 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'DevOps - Infrastructure et déploiement d''applications'
UNION ALL
SELECT t.track_id, c.course_id, 31 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'Intelligence Artificielle / Python'
UNION ALL
SELECT t.track_id, c.course_id, 32 FROM track t, course c WHERE t.name = 'CDA' AND c.name = 'IA / Python + Projet Final';
