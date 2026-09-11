-- Fixtures de démarrage, rejouées à chaque lancement.
-- Repartir d'une base vierge : docker compose down -v.

-- --- Utilisateurs de démonstration (mot de passe commun : Formagest2026!) ---

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 1, 'eleve.demo@formagest.fr', 'Martin', 'Lucas', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 1);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 2, 'formateur.demo@formagest.fr', 'Bernard', 'Claire', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'TEACHER'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 2);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 3, 'referente.demo@formagest.fr', 'Dubois', 'Sophie', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'ADMINISTRATIVE_MANAGER'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 3);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 4, 'admin.demo@formagest.fr', 'Petit', 'Thomas', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'ADMINISTRATOR'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 4);

INSERT INTO student (id, birth_date)
SELECT 1, '2000-04-12'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 1);

INSERT INTO administrative_manager (id)
SELECT 3
WHERE NOT EXISTS (SELECT 1 FROM administrative_manager WHERE id = 3);

INSERT INTO administrator (id)
SELECT 4
WHERE NOT EXISTS (SELECT 1 FROM administrator WHERE id = 4);

-- --- Filière, cursus, cours (D2WM, CDA) ---

INSERT INTO sector (name)
SELECT 'Développement'
WHERE NOT EXISTS (SELECT 1 FROM sector WHERE name = 'Développement');

INSERT INTO sector (name)
SELECT 'Système et réseaux'
WHERE NOT EXISTS (SELECT 1 FROM sector WHERE name = 'Système et réseaux');

-- Rattache le formateur à un sector : doit venir après la création des sector
INSERT INTO teacher (id, sector_id)
SELECT 2, s.sector_id FROM sector s WHERE s.name = 'Développement'
AND NOT EXISTS (SELECT 1 FROM teacher WHERE id = 2);

-- --- Cursus ---
INSERT INTO track (name, sector_id)
SELECT 'D2WM', s.sector_id FROM sector s WHERE s.name = 'Développement'
AND NOT EXISTS (SELECT 1 FROM track WHERE name = 'D2WM');

INSERT INTO track (name, sector_id)
SELECT 'CDA', s.sector_id FROM sector s WHERE s.name = 'Développement'
AND NOT EXISTS (SELECT 1 FROM track WHERE name = 'CDA');

-- --- Cours (catalogue, 37 intitulés uniques partagés entre cursus) ---
INSERT INTO course (name)
SELECT 'Algorithmique / Pseudo-Code'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Algorithmique / Pseudo-Code');

INSERT INTO course (name)
SELECT 'Initiation à la Programmation / Java'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Initiation à la Programmation / Java');

INSERT INTO course (name)
SELECT 'Web Client / HTML & CSS'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Web Client / HTML & CSS');

INSERT INTO course (name)
SELECT 'JavaScript initiation'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'JavaScript initiation');

INSERT INTO course (name)
SELECT 'Projet Web / HTML & CSS + JS'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Projet Web / HTML & CSS + JS');

INSERT INTO course (name)
SELECT 'Programmation Orientée Objet / Java (partie 1)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Programmation Orientée Objet / Java (partie 1)');

INSERT INTO course (name)
SELECT 'Programmation Orientée Objet / Java (partie 2)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Programmation Orientée Objet / Java (partie 2)');

INSERT INTO course (name)
SELECT 'Langage SQL / SQL Server'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Langage SQL / SQL Server');

INSERT INTO course (name)
SELECT 'Notions Complémentaires / Java SE'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Notions Complémentaires / Java SE');

INSERT INTO course (name)
SELECT 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 1)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 1)');

INSERT INTO course (name)
SELECT 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 2)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 2)');

INSERT INTO course (name)
SELECT 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 3)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 3)');

INSERT INTO course (name)
SELECT 'Projet Web / Java Spring Boot (partie 1)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Projet Web / Java Spring Boot (partie 1)');

INSERT INTO course (name)
SELECT 'Projet Web / Java Spring Boot (partie 2)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Projet Web / Java Spring Boot (partie 2)');

INSERT INTO course (name)
SELECT 'Analyse et Conception / Oracle Data Modeler'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Analyse et Conception / Oracle Data Modeler');

INSERT INTO course (name)
SELECT 'JavaScript avancé + initiation Framework JS / Angular'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'JavaScript avancé + initiation Framework JS / Angular');

INSERT INTO course (name)
SELECT 'Développement Web côté Serveur avec JavaScript / Node.js et NoSQL'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Développement Web côté Serveur avec JavaScript / Node.js et NoSQL');

INSERT INTO course (name)
SELECT 'Développement Web côté Serveur (Back-End) / PHP'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Développement Web côté Serveur (Back-End) / PHP');

INSERT INTO course (name)
SELECT 'Développement Web côté Serveur (Back-End) / Symfony (partie 1)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Développement Web côté Serveur (Back-End) / Symfony (partie 1)');

INSERT INTO course (name)
SELECT 'Développement Web côté Serveur (Back-End) / Symfony (partie 2)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Développement Web côté Serveur (Back-End) / Symfony (partie 2)');

INSERT INTO course (name)
SELECT 'Projet Web / Symfony (partie 1)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Projet Web / Symfony (partie 1)');

INSERT INTO course (name)
SELECT 'Projet Web / Symfony (partie 2)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Projet Web / Symfony (partie 2)');

INSERT INTO course (name)
SELECT 'CMS / WordPress'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'CMS / WordPress');

INSERT INTO course (name)
SELECT 'CMS / WordPress + Projet Final'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'CMS / WordPress + Projet Final');

INSERT INTO course (name)
SELECT 'Algorithmique + Initiation à la Programmation / Java'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Algorithmique + Initiation à la Programmation / Java');

INSERT INTO course (name)
SELECT 'SQL avancé / Transact SQL et Sécurité'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'SQL avancé / Transact SQL et Sécurité');

INSERT INTO course (name)
SELECT 'Gestion de projet et Communication'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Gestion de projet et Communication');

INSERT INTO course (name)
SELECT 'Java Frameworks - API Web (Spring Security, ORM, …) (partie 1)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Java Frameworks - API Web (Spring Security, ORM, …) (partie 1)');

INSERT INTO course (name)
SELECT 'Java Frameworks - API Web (Spring Security, ORM, …) (partie 2)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Java Frameworks - API Web (Spring Security, ORM, …) (partie 2)');

INSERT INTO course (name)
SELECT 'Angular avancé / Angular'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Angular avancé / Angular');

INSERT INTO course (name)
SELECT 'Analyse et Conception / Oracle Data Modeler (approfondissement)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Analyse et Conception / Oracle Data Modeler (approfondissement)');

INSERT INTO course (name)
SELECT 'Projet Fullstack - Web / Java Spring Boot + Angular (partie 1)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Projet Fullstack - Web / Java Spring Boot + Angular (partie 1)');

INSERT INTO course (name)
SELECT 'Projet Fullstack - Web / Java Spring Boot + Angular (partie 2)'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Projet Fullstack - Web / Java Spring Boot + Angular (partie 2)');

INSERT INTO course (name)
SELECT 'Technologie Cross-Platform / Flutter'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Technologie Cross-Platform / Flutter');

INSERT INTO course (name)
SELECT 'DevOps - Infrastructure et déploiement d''applications'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'DevOps - Infrastructure et déploiement d''applications');

INSERT INTO course (name)
SELECT 'Intelligence Artificielle / Python'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Intelligence Artificielle / Python');

INSERT INTO course (name)
SELECT 'IA / Python + Projet Final'
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'IA / Python + Projet Final');

-- --- Progression pédagogique : D2WM ---
INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 1 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Algorithmique / Pseudo-Code'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 2 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Initiation à la Programmation / Java'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 3 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Web Client / HTML & CSS'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 4 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'JavaScript initiation'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 5 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Projet Web / HTML & CSS + JS'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 6 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Programmation Orientée Objet / Java (partie 1)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 7 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Programmation Orientée Objet / Java (partie 2)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 8 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Langage SQL / SQL Server'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 9 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Notions Complémentaires / Java SE'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 10 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 1)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 11 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 2)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 12 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 3)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 13 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Projet Web / Java Spring Boot (partie 1)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 14 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Projet Web / Java Spring Boot (partie 2)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 15 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Analyse et Conception / Oracle Data Modeler'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 16 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'JavaScript avancé + initiation Framework JS / Angular'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 17 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Développement Web côté Serveur avec JavaScript / Node.js et NoSQL'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 18 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Développement Web côté Serveur (Back-End) / PHP'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 19 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Développement Web côté Serveur (Back-End) / Symfony (partie 1)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 20 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Développement Web côté Serveur (Back-End) / Symfony (partie 2)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 21 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Projet Web / Symfony (partie 1)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 22 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'Projet Web / Symfony (partie 2)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 23 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'CMS / WordPress'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 24 FROM track t, course c
WHERE t.name = 'D2WM' AND c.name = 'CMS / WordPress + Projet Final'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

-- --- Progression pédagogique : CDA ---
INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 1 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Algorithmique + Initiation à la Programmation / Java'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 2 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Web Client / HTML & CSS'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 3 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'JavaScript initiation'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 4 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Programmation Orientée Objet / Java (partie 1)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 5 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Programmation Orientée Objet / Java (partie 2)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 6 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Langage SQL / SQL Server'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 7 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Notions Complémentaires / Java SE'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 8 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 1)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 9 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 2)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 10 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 3)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 11 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Projet Web / Java Spring Boot (partie 1)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 12 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Projet Web / Java Spring Boot (partie 2)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 13 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Analyse et Conception / Oracle Data Modeler'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 14 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'JavaScript avancé + initiation Framework JS / Angular'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 15 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Développement Web côté Serveur avec JavaScript / Node.js et NoSQL'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 16 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Développement Web côté Serveur (Back-End) / PHP'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 17 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Développement Web côté Serveur (Back-End) / Symfony (partie 1)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 18 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Développement Web côté Serveur (Back-End) / Symfony (partie 2)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 19 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Projet Web / Symfony (partie 1)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 20 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Projet Web / Symfony (partie 2)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 21 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'SQL avancé / Transact SQL et Sécurité'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 22 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Gestion de projet et Communication'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 23 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Java Frameworks - API Web (Spring Security, ORM, …) (partie 1)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 24 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Java Frameworks - API Web (Spring Security, ORM, …) (partie 2)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 25 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Angular avancé / Angular'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 26 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Analyse et Conception / Oracle Data Modeler (approfondissement)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 27 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Projet Fullstack - Web / Java Spring Boot + Angular (partie 1)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 28 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Projet Fullstack - Web / Java Spring Boot + Angular (partie 2)'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 29 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Technologie Cross-Platform / Flutter'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 30 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'DevOps - Infrastructure et déploiement d''applications'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 31 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'Intelligence Artificielle / Python'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);

INSERT INTO track_course (track_id, course_id, position)
SELECT t.track_id, c.course_id, 32 FROM track t, course c
WHERE t.name = 'CDA' AND c.name = 'IA / Python + Projet Final'
AND NOT EXISTS (SELECT 1 FROM track_course tc WHERE tc.track_id = t.track_id AND tc.course_id = c.course_id);
