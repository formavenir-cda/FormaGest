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

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 5, 'eleve2.demo@formagest.fr', 'Girard', 'Amélie', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 5);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 6, 'eleve3.demo@formagest.fr', 'Haddad', 'Karim', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 6);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 7, 'eleve4.demo@formagest.fr', 'Fontaine', 'Léa', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 7);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 8, 'formateur2.demo@formagest.fr', 'Moreau', 'Nicolas', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'TEACHER'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 8);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 9, 'formateur3.demo@formagest.fr', 'Lambert', 'Isabelle', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'TEACHER'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 9);

INSERT INTO student (id, birth_date)
SELECT 1, '2000-04-12'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 1);

INSERT INTO student (id, birth_date)
SELECT 5, '1998-11-03'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 5);

INSERT INTO student (id, birth_date)
SELECT 6, '2001-07-22'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 6);

INSERT INTO student (id, birth_date)
SELECT 7, '1999-02-14'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 7);

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

-- Rattache les formateurs à un sector : doit venir après la création des sector
INSERT INTO teacher (id, sector_id)
SELECT 2, s.sector_id FROM sector s WHERE s.name = 'Développement'
AND NOT EXISTS (SELECT 1 FROM teacher WHERE id = 2);

INSERT INTO teacher (id, sector_id)
SELECT 8, s.sector_id FROM sector s WHERE s.name = 'Développement'
AND NOT EXISTS (SELECT 1 FROM teacher WHERE id = 8);

INSERT INTO teacher (id, sector_id)
SELECT 9, s.sector_id FROM sector s WHERE s.name = 'Système et réseaux'
AND NOT EXISTS (SELECT 1 FROM teacher WHERE id = 9);

-- --- Cursus ---
INSERT INTO track (name, sector_id)
SELECT 'D2WM', s.sector_id FROM sector s WHERE s.name = 'Développement'
AND NOT EXISTS (SELECT 1 FROM track WHERE name = 'D2WM');

INSERT INTO track (name, sector_id)
SELECT 'CDA', s.sector_id FROM sector s WHERE s.name = 'Développement'
AND NOT EXISTS (SELECT 1 FROM track WHERE name = 'CDA');

-- --- Cours (catalogue, 37 intitulés uniques partagés entre cursus) ---
INSERT INTO course (name, duration_in_days)
SELECT 'Algorithmique / Pseudo-Code', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Algorithmique / Pseudo-Code');

INSERT INTO course (name, duration_in_days)
SELECT 'Initiation à la Programmation / Java', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Initiation à la Programmation / Java');

INSERT INTO course (name, duration_in_days)
SELECT 'Web Client / HTML & CSS', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Web Client / HTML & CSS');

INSERT INTO course (name, duration_in_days)
SELECT 'JavaScript initiation', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'JavaScript initiation');

INSERT INTO course (name, duration_in_days)
SELECT 'Projet Web / HTML & CSS + JS', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Projet Web / HTML & CSS + JS');

INSERT INTO course (name, duration_in_days)
SELECT 'Programmation Orientée Objet / Java (partie 1)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Programmation Orientée Objet / Java (partie 1)');

INSERT INTO course (name, duration_in_days)
SELECT 'Programmation Orientée Objet / Java (partie 2)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Programmation Orientée Objet / Java (partie 2)');

INSERT INTO course (name, duration_in_days)
SELECT 'Langage SQL / SQL Server', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Langage SQL / SQL Server');

INSERT INTO course (name, duration_in_days)
SELECT 'Notions Complémentaires / Java SE', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Notions Complémentaires / Java SE');

INSERT INTO course (name, duration_in_days)
SELECT 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 1)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 1)');

INSERT INTO course (name, duration_in_days)
SELECT 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 2)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 2)');

INSERT INTO course (name, duration_in_days)
SELECT 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 3)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 3)');

INSERT INTO course (name, duration_in_days)
SELECT 'Projet Web / Java Spring Boot (partie 1)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Projet Web / Java Spring Boot (partie 1)');

INSERT INTO course (name, duration_in_days)
SELECT 'Projet Web / Java Spring Boot (partie 2)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Projet Web / Java Spring Boot (partie 2)');

INSERT INTO course (name, duration_in_days)
SELECT 'Analyse et Conception / Oracle Data Modeler', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Analyse et Conception / Oracle Data Modeler');

INSERT INTO course (name, duration_in_days)
SELECT 'JavaScript avancé + initiation Framework JS / Angular', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'JavaScript avancé + initiation Framework JS / Angular');

INSERT INTO course (name, duration_in_days)
SELECT 'Développement Web côté Serveur avec JavaScript / Node.js et NoSQL', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Développement Web côté Serveur avec JavaScript / Node.js et NoSQL');

INSERT INTO course (name, duration_in_days)
SELECT 'Développement Web côté Serveur (Back-End) / PHP', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Développement Web côté Serveur (Back-End) / PHP');

INSERT INTO course (name, duration_in_days)
SELECT 'Développement Web côté Serveur (Back-End) / Symfony (partie 1)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Développement Web côté Serveur (Back-End) / Symfony (partie 1)');

INSERT INTO course (name, duration_in_days)
SELECT 'Développement Web côté Serveur (Back-End) / Symfony (partie 2)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Développement Web côté Serveur (Back-End) / Symfony (partie 2)');

INSERT INTO course (name, duration_in_days)
SELECT 'Projet Web / Symfony (partie 1)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Projet Web / Symfony (partie 1)');

INSERT INTO course (name, duration_in_days)
SELECT 'Projet Web / Symfony (partie 2)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Projet Web / Symfony (partie 2)');

INSERT INTO course (name, duration_in_days)
SELECT 'CMS / WordPress', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'CMS / WordPress');

INSERT INTO course (name, duration_in_days)
SELECT 'CMS / WordPress + Projet Final', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'CMS / WordPress + Projet Final');

INSERT INTO course (name, duration_in_days)
SELECT 'Algorithmique + Initiation à la Programmation / Java', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Algorithmique + Initiation à la Programmation / Java');

INSERT INTO course (name, duration_in_days)
SELECT 'SQL avancé / Transact SQL et Sécurité', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'SQL avancé / Transact SQL et Sécurité');

INSERT INTO course (name, duration_in_days)
SELECT 'Gestion de projet et Communication', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Gestion de projet et Communication');

INSERT INTO course (name, duration_in_days)
SELECT 'Java Frameworks - API Web (Spring Security, ORM, …) (partie 1)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Java Frameworks - API Web (Spring Security, ORM, …) (partie 1)');

INSERT INTO course (name, duration_in_days)
SELECT 'Java Frameworks - API Web (Spring Security, ORM, …) (partie 2)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Java Frameworks - API Web (Spring Security, ORM, …) (partie 2)');

INSERT INTO course (name, duration_in_days)
SELECT 'Angular avancé / Angular', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Angular avancé / Angular');

INSERT INTO course (name, duration_in_days)
SELECT 'Analyse et Conception / Oracle Data Modeler (approfondissement)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Analyse et Conception / Oracle Data Modeler (approfondissement)');

INSERT INTO course (name, duration_in_days)
SELECT 'Projet Fullstack - Web / Java Spring Boot + Angular (partie 1)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Projet Fullstack - Web / Java Spring Boot + Angular (partie 1)');

INSERT INTO course (name, duration_in_days)
SELECT 'Projet Fullstack - Web / Java Spring Boot + Angular (partie 2)', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Projet Fullstack - Web / Java Spring Boot + Angular (partie 2)');

INSERT INTO course (name, duration_in_days)
SELECT 'Technologie Cross-Platform / Flutter', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Technologie Cross-Platform / Flutter');

INSERT INTO course (name, duration_in_days)
SELECT 'DevOps - Infrastructure et déploiement d''applications', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'DevOps - Infrastructure et déploiement d''applications');

INSERT INTO course (name, duration_in_days)
SELECT 'Intelligence Artificielle / Python', 5
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Intelligence Artificielle / Python');

INSERT INTO course (name, duration_in_days)
SELECT 'IA / Python + Projet Final', 5
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

-- --- Promotions (cohort) et cours planifiés (scheduled_course) : D2WM ---

INSERT INTO cohort (name, start_date, end_date, status, track_id)
SELECT 'D2WM 2026', '2026-01-12', '2026-10-16', 'IN_PROGRESS', t.track_id FROM track t
WHERE t.name = 'D2WM'
AND NOT EXISTS (SELECT 1 FROM cohort WHERE name = 'D2WM 2026');

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-01-12', '2026-01-23'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2026' AND c.name = 'Algorithmique / Pseudo-Code'
AND te.id = au.id AND au.email = 'formateur.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-01-26', '2026-02-13'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2026' AND c.name = 'Initiation à la Programmation / Java'
AND te.id = au.id AND au.email = 'formateur.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-02-16', '2026-02-27'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2026' AND c.name = 'Web Client / HTML & CSS'
AND te.id = au.id AND au.email = 'formateur2.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-03-02', '2026-03-13'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2026' AND c.name = 'JavaScript initiation'
AND te.id = au.id AND au.email = 'formateur2.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-09-07', '2026-09-25'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2026' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 1)'
AND te.id = au.id AND au.email = 'formateur.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-09-28', '2026-10-16'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2026' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 2)'
AND te.id = au.id AND au.email = 'formateur2.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

-- --- Promotions (cohort) et cours planifiés (scheduled_course) : CDA ---

INSERT INTO cohort (name, start_date, end_date, status, track_id)
SELECT 'CDA 2026', '2026-01-12', '2026-10-10', 'IN_PROGRESS', t.track_id FROM track t
WHERE t.name = 'CDA'
AND NOT EXISTS (SELECT 1 FROM cohort WHERE name = 'CDA 2026');

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-01-12', '2026-01-30'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'CDA 2026' AND c.name = 'Algorithmique + Initiation à la Programmation / Java'
AND te.id = au.id AND au.email = 'formateur2.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-02-02', '2026-02-13'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'CDA 2026' AND c.name = 'Web Client / HTML & CSS'
AND te.id = au.id AND au.email = 'formateur.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-02-16', '2026-02-27'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'CDA 2026' AND c.name = 'JavaScript initiation'
AND te.id = au.id AND au.email = 'formateur.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-09-01', '2026-09-19'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'CDA 2026' AND c.name = 'Programmation Orientée Objet / Java (partie 1)'
AND te.id = au.id AND au.email = 'formateur2.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-09-22', '2026-10-10'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'CDA 2026' AND c.name = 'Programmation Orientée Objet / Java (partie 2)'
AND te.id = au.id AND au.email = 'formateur.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

-- =====================================================================
-- Jeu de données étendu (2026-09-16) : 3 promotions, ~15 élèves chacune,
-- formateurs supplémentaires, cours planifiés et inscriptions.
-- =====================================================================

-- --- Élèves supplémentaires (mot de passe commun : Formagest2026!) ---

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 10, 'eleve10.demo@formagest.fr', 'Faure', 'Baptiste', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 10);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 11, 'eleve11.demo@formagest.fr', 'Francois', 'Yasmine', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 11);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 12, 'eleve12.demo@formagest.fr', 'Gaillard', 'Quentin', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 12);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 13, 'eleve13.demo@formagest.fr', 'Gillet', 'Anaïs', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 13);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 14, 'eleve14.demo@formagest.fr', 'Rolland', 'Zoé', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 14);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 15, 'eleve15.demo@formagest.fr', 'Martinez', 'Pauline', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 15);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 16, 'eleve16.demo@formagest.fr', 'Garcia', 'Enzo', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 16);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 17, 'eleve17.demo@formagest.fr', 'Morin', 'Tom', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 17);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 18, 'eleve18.demo@formagest.fr', 'Rousseau', 'Jules', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 18);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 19, 'eleve19.demo@formagest.fr', 'Durand', 'Elodie', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 19);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 20, 'eleve20.demo@formagest.fr', 'Germain', 'Inès', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 20);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 21, 'eleve21.demo@formagest.fr', 'Klein', 'Maxime', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 21);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 22, 'eleve22.demo@formagest.fr', 'Carre', 'Raphaël', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 22);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 23, 'eleve23.demo@formagest.fr', 'Brun', 'Yanis', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 23);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 24, 'eleve24.demo@formagest.fr', 'Barbier', 'Gabriel', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 24);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 25, 'eleve25.demo@formagest.fr', 'Roussel', 'Sofiane', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 25);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 26, 'eleve26.demo@formagest.fr', 'Philippe', 'Emma', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 26);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 27, 'eleve27.demo@formagest.fr', 'Meyer', 'Justine', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 27);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 28, 'eleve28.demo@formagest.fr', 'Duval', 'Bilal', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 28);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 29, 'eleve29.demo@formagest.fr', 'Charrier', 'Sarah', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 29);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 30, 'eleve30.demo@formagest.fr', 'Barre', 'Louis', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 30);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 31, 'eleve31.demo@formagest.fr', 'Roy', 'Nora', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 31);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 32, 'eleve32.demo@formagest.fr', 'Guyot', 'Ethan', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 32);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 33, 'eleve33.demo@formagest.fr', 'Lefevre', 'Salomé', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 33);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 34, 'eleve34.demo@formagest.fr', 'Vasseur', 'Mathieu', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 34);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 35, 'eleve35.demo@formagest.fr', 'Renault', 'Fatima', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 35);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 36, 'eleve36.demo@formagest.fr', 'Jacquet', 'Camille', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 36);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 37, 'eleve37.demo@formagest.fr', 'Colin', 'Romain', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 37);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 38, 'eleve38.demo@formagest.fr', 'Lecomte', 'Sabrina', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 38);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 39, 'eleve39.demo@formagest.fr', 'David', 'Youssef', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 39);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 40, 'eleve40.demo@formagest.fr', 'Fournier', 'Amandine', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 40);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 41, 'eleve41.demo@formagest.fr', 'Roche', 'Nathan', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 41);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 42, 'eleve42.demo@formagest.fr', 'Henry', 'Alexandre', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 42);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 43, 'eleve43.demo@formagest.fr', 'Perrin', 'Nadia', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 43);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 44, 'eleve44.demo@formagest.fr', 'Leroux', 'Mehdi', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 44);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 45, 'eleve45.demo@formagest.fr', 'Villeneuve', 'Clara', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 45);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 46, 'eleve46.demo@formagest.fr', 'Mercier', 'Théo', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 46);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 47, 'eleve47.demo@formagest.fr', 'Bourgeois', 'Pierre', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 47);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 48, 'eleve48.demo@formagest.fr', 'Dupont', 'Arthur', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 48);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 49, 'eleve49.demo@formagest.fr', 'Fabre', 'Victor', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 49);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 50, 'eleve50.demo@formagest.fr', 'Pelletier', 'Lucie', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 50);

-- --- Formateurs supplémentaires (mot de passe commun : Formagest2026!) ---

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 51, 'formateur51.demo@formagest.fr', 'Charpentier', 'Rayan', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'TEACHER'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 51);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 52, 'formateur52.demo@formagest.fr', 'Riviere', 'Liam', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'TEACHER'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 52);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 53, 'formateur53.demo@formagest.fr', 'Dumont', 'Chloé', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'TEACHER'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 53);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 54, 'formateur54.demo@formagest.fr', 'Colas', 'Hugo', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'TEACHER'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 54);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 55, 'formateur55.demo@formagest.fr', 'Leclerc', 'Antoine', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'TEACHER'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 55);

INSERT INTO app_user (id, email, lastname, firstname, password, active, role)
SELECT 56, 'formateur56.demo@formagest.fr', 'Etienne', 'Julie', '$2y$10$kHbMLD9R1wnMcSKfvMgxROTzgWf/4bjsDuAzIHgFp0C.uwW60p3na', TRUE, 'TEACHER'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE id = 56);

-- --- Sous-types (student) des élèves supplémentaires ---

INSERT INTO student (id, birth_date)
SELECT 10, '1996-08-01'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 10);

INSERT INTO student (id, birth_date)
SELECT 11, '2005-12-09'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 11);

INSERT INTO student (id, birth_date)
SELECT 12, '2002-03-17'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 12);

INSERT INTO student (id, birth_date)
SELECT 13, '1995-11-10'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 13);

INSERT INTO student (id, birth_date)
SELECT 14, '2004-09-20'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 14);

INSERT INTO student (id, birth_date)
SELECT 15, '1997-03-12'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 15);

INSERT INTO student (id, birth_date)
SELECT 16, '2006-03-18'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 16);

INSERT INTO student (id, birth_date)
SELECT 17, '2006-09-01'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 17);

INSERT INTO student (id, birth_date)
SELECT 18, '2003-06-16'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 18);

INSERT INTO student (id, birth_date)
SELECT 19, '1994-02-12'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 19);

INSERT INTO student (id, birth_date)
SELECT 20, '2006-05-08'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 20);

INSERT INTO student (id, birth_date)
SELECT 21, '1994-04-19'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 21);

INSERT INTO student (id, birth_date)
SELECT 22, '1995-02-24'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 22);

INSERT INTO student (id, birth_date)
SELECT 23, '2001-02-25'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 23);

INSERT INTO student (id, birth_date)
SELECT 24, '2002-03-05'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 24);

INSERT INTO student (id, birth_date)
SELECT 25, '2004-08-18'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 25);

INSERT INTO student (id, birth_date)
SELECT 26, '1996-05-17'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 26);

INSERT INTO student (id, birth_date)
SELECT 27, '2003-07-07'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 27);

INSERT INTO student (id, birth_date)
SELECT 28, '2002-12-23'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 28);

INSERT INTO student (id, birth_date)
SELECT 29, '1997-12-10'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 29);

INSERT INTO student (id, birth_date)
SELECT 30, '2000-11-21'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 30);

INSERT INTO student (id, birth_date)
SELECT 31, '1999-08-17'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 31);

INSERT INTO student (id, birth_date)
SELECT 32, '2001-02-08'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 32);

INSERT INTO student (id, birth_date)
SELECT 33, '1997-02-11'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 33);

INSERT INTO student (id, birth_date)
SELECT 34, '1994-10-18'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 34);

INSERT INTO student (id, birth_date)
SELECT 35, '1997-10-08'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 35);

INSERT INTO student (id, birth_date)
SELECT 36, '1994-02-23'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 36);

INSERT INTO student (id, birth_date)
SELECT 37, '2004-01-08'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 37);

INSERT INTO student (id, birth_date)
SELECT 38, '1995-01-28'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 38);

INSERT INTO student (id, birth_date)
SELECT 39, '1999-02-17'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 39);

INSERT INTO student (id, birth_date)
SELECT 40, '1997-05-22'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 40);

INSERT INTO student (id, birth_date)
SELECT 41, '2001-04-18'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 41);

INSERT INTO student (id, birth_date)
SELECT 42, '1996-12-19'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 42);

INSERT INTO student (id, birth_date)
SELECT 43, '2003-08-08'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 43);

INSERT INTO student (id, birth_date)
SELECT 44, '2006-08-26'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 44);

INSERT INTO student (id, birth_date)
SELECT 45, '2000-04-04'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 45);

INSERT INTO student (id, birth_date)
SELECT 46, '1995-11-14'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 46);

INSERT INTO student (id, birth_date)
SELECT 47, '1999-07-14'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 47);

INSERT INTO student (id, birth_date)
SELECT 48, '2001-12-02'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 48);

INSERT INTO student (id, birth_date)
SELECT 49, '2004-11-21'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 49);

INSERT INTO student (id, birth_date)
SELECT 50, '1995-01-13'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE id = 50);

-- --- Sous-types (teacher) des formateurs supplémentaires : doit venir après la création des sector ---

INSERT INTO teacher (id, sector_id)
SELECT 51, s.sector_id FROM sector s WHERE s.name = 'Développement'
AND NOT EXISTS (SELECT 1 FROM teacher WHERE id = 51);

INSERT INTO teacher (id, sector_id)
SELECT 52, s.sector_id FROM sector s WHERE s.name = 'Développement'
AND NOT EXISTS (SELECT 1 FROM teacher WHERE id = 52);

INSERT INTO teacher (id, sector_id)
SELECT 53, s.sector_id FROM sector s WHERE s.name = 'Développement'
AND NOT EXISTS (SELECT 1 FROM teacher WHERE id = 53);

INSERT INTO teacher (id, sector_id)
SELECT 54, s.sector_id FROM sector s WHERE s.name = 'Développement'
AND NOT EXISTS (SELECT 1 FROM teacher WHERE id = 54);

INSERT INTO teacher (id, sector_id)
SELECT 55, s.sector_id FROM sector s WHERE s.name = 'Système et réseaux'
AND NOT EXISTS (SELECT 1 FROM teacher WHERE id = 55);

INSERT INTO teacher (id, sector_id)
SELECT 56, s.sector_id FROM sector s WHERE s.name = 'Système et réseaux'
AND NOT EXISTS (SELECT 1 FROM teacher WHERE id = 56);

-- --- Cours planifiés supplémentaires : D2WM 2026 (suite) ---

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-10-19', '2026-11-06'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2026' AND c.name = 'Projet Web / Java Spring Boot (partie 1)'
AND te.id = au.id AND au.email = 'formateur51.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-11-09', '2026-11-27'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2026' AND c.name = 'Projet Web / Java Spring Boot (partie 2)'
AND te.id = au.id AND au.email = 'formateur52.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-11-30', '2026-12-11'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2026' AND c.name = 'Analyse et Conception / Oracle Data Modeler'
AND te.id = au.id AND au.email = 'formateur.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-12-14', '2027-01-08'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2026' AND c.name = 'JavaScript avancé + initiation Framework JS / Angular'
AND te.id = au.id AND au.email = 'formateur2.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, '2027-01-11', '2027-01-29'
FROM cohort co, course c
WHERE co.name = 'D2WM 2026' AND c.name = 'Développement Web côté Serveur avec JavaScript / Node.js et NoSQL'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

UPDATE cohort SET end_date = '2027-01-29' WHERE name = 'D2WM 2026';

-- --- Cours planifiés supplémentaires : CDA 2026 (suite) ---

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-10-13', '2026-10-23'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'CDA 2026' AND c.name = 'Langage SQL / SQL Server'
AND te.id = au.id AND au.email = 'formateur53.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-10-26', '2026-11-06'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'CDA 2026' AND c.name = 'Notions Complémentaires / Java SE'
AND te.id = au.id AND au.email = 'formateur54.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-11-09', '2026-11-27'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'CDA 2026' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 1)'
AND te.id = au.id AND au.email = 'formateur.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2026-11-30', '2026-12-18'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'CDA 2026' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 2)'
AND te.id = au.id AND au.email = 'formateur2.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, '2027-01-04', '2027-01-22'
FROM cohort co, course c
WHERE co.name = 'CDA 2026' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 3)'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

UPDATE cohort SET end_date = '2027-01-22' WHERE name = 'CDA 2026';

-- --- Promotion (cohort) et cours planifiés (scheduled_course) : D2WM 2025 (terminée) ---

INSERT INTO cohort (name, start_date, end_date, status, track_id)
SELECT 'D2WM 2025', '2025-01-13', '2025-08-08', 'COMPLETED', t.track_id FROM track t
WHERE t.name = 'D2WM'
AND NOT EXISTS (SELECT 1 FROM cohort WHERE name = 'D2WM 2025');

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2025-01-13', '2025-01-24'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2025' AND c.name = 'Algorithmique / Pseudo-Code'
AND te.id = au.id AND au.email = 'formateur.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2025-01-27', '2025-02-14'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2025' AND c.name = 'Initiation à la Programmation / Java'
AND te.id = au.id AND au.email = 'formateur2.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2025-02-17', '2025-02-28'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2025' AND c.name = 'Web Client / HTML & CSS'
AND te.id = au.id AND au.email = 'formateur51.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2025-03-03', '2025-03-14'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2025' AND c.name = 'JavaScript initiation'
AND te.id = au.id AND au.email = 'formateur52.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, '2025-03-17', '2025-03-28'
FROM cohort co, course c
WHERE co.name = 'D2WM 2025' AND c.name = 'Projet Web / HTML & CSS + JS'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2025-03-31', '2025-04-18'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2025' AND c.name = 'Programmation Orientée Objet / Java (partie 1)'
AND te.id = au.id AND au.email = 'formateur.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2025-04-21', '2025-05-09'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2025' AND c.name = 'Programmation Orientée Objet / Java (partie 2)'
AND te.id = au.id AND au.email = 'formateur2.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2025-05-12', '2025-05-23'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2025' AND c.name = 'Langage SQL / SQL Server'
AND te.id = au.id AND au.email = 'formateur53.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2025-05-26', '2025-06-06'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2025' AND c.name = 'Notions Complémentaires / Java SE'
AND te.id = au.id AND au.email = 'formateur54.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2025-06-09', '2025-06-27'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2025' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 1)'
AND te.id = au.id AND au.email = 'formateur.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, teacher_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, te.id, '2025-06-30', '2025-07-18'
FROM cohort co, course c, teacher te, app_user au
WHERE co.name = 'D2WM 2025' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 2)'
AND te.id = au.id AND au.email = 'formateur2.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

INSERT INTO scheduled_course (cohort_id, course_id, start_date, end_date)
SELECT co.cohort_id, c.course_id, '2025-07-21', '2025-08-08'
FROM cohort co, course c
WHERE co.name = 'D2WM 2025' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 3)'
AND NOT EXISTS (SELECT 1 FROM scheduled_course sc WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id);

-- --- Inscriptions à une promotion complète (cohort_enrollment) ---

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 1, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve10.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 1);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 1, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 1);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 2, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve11.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 2);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 2, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 2);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 3, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve12.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 3);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 3, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 3);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 4, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve13.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 4);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 4, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 4);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 5, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve14.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 5);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 5, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 5);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 6, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve15.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 6);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 6, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 6);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 7, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve16.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 7);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 7, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 7);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 8, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve17.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 8);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 8, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 8);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 9, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve18.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 9);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 9, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 9);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 10, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve19.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 10);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 10, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 10);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 11, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve20.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 11);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 11, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 11);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 12, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve21.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 12);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 12, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 12);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 13, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve22.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 13);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 13, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 13);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 14, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve23.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 14);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 14, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 14);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 15, '2025-01-10 09:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve24.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 15);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 15, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2025'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 15);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 16, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 16);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 16, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 16);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 17, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve2.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 17);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 17, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 17);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 18, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve3.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 18);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 18, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 18);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 19, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve4.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 19);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 19, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 19);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 20, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve25.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 20);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 20, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 20);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 21, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve26.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 21);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 21, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 21);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 22, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve27.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 22);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 22, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 22);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 23, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve28.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 23);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 23, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 23);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 24, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve29.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 24);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 24, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 24);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 25, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve30.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 25);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 25, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 25);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 26, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve31.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 26);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 26, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 26);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 27, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve32.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 27);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 27, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 27);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 28, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve33.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 28);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 28, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 28);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 29, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve34.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 29);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 29, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 29);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 30, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve35.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 30);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 30, co.cohort_id FROM cohort co WHERE co.name = 'D2WM 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 30);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 31, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve36.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 31);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 31, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 31);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 32, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve37.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 32);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 32, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 32);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 33, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve38.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 33);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 33, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 33);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 34, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve39.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 34);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 34, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 34);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 35, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve40.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 35);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 35, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 35);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 36, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve41.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 36);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 36, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 36);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 37, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve42.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 37);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 37, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 37);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 38, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve43.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 38);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 38, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 38);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 39, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve44.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 39);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 39, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 39);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 40, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve45.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 40);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 40, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 40);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 41, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve46.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 41);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 41, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 41);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 42, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve47.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 42);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 42, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 42);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 43, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve48.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 43);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 43, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 43);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 44, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve49.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 44);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 44, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 44);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 45, '2026-01-08 09:00:00', 'IN_PROGRESS', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve50.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 45);

INSERT INTO cohort_enrollment (enrollment_id, cohort_id)
SELECT 45, co.cohort_id FROM cohort co WHERE co.name = 'CDA 2026'
AND NOT EXISTS (SELECT 1 FROM cohort_enrollment WHERE enrollment_id = 45);

-- --- Inscriptions à un cours spécifique (scheduled_course_enrollment) ---

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 46, '2026-01-09 10:00:00', 'COMPLETED', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 46);

INSERT INTO scheduled_course_enrollment (enrollment_id, scheduled_course_id, forced)
SELECT 46, sc.id, FALSE
FROM scheduled_course sc, cohort co, course c
WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id AND co.name = 'CDA 2026' AND c.name = 'Algorithmique + Initiation à la Programmation / Java'
AND NOT EXISTS (SELECT 1 FROM scheduled_course_enrollment WHERE enrollment_id = 46);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 47, '2026-09-10 10:00:00', 'NEW', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve25.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 47);

INSERT INTO scheduled_course_enrollment (enrollment_id, scheduled_course_id, forced)
SELECT 47, sc.id, FALSE
FROM scheduled_course sc, cohort co, course c
WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id AND co.name = 'CDA 2026' AND c.name = 'Notions Complémentaires / Java SE'
AND NOT EXISTS (SELECT 1 FROM scheduled_course_enrollment WHERE enrollment_id = 47);

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, student_id, created_by)
SELECT 48, '2026-09-12 11:00:00', 'NEW', au_s.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve30.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 48);

INSERT INTO scheduled_course_enrollment (enrollment_id, scheduled_course_id, forced, justification_forced)
SELECT 48, sc.id, TRUE, 'Inscription forcée par la référente : niveau du stagiaire jugé suffisant malgré l''absence des prérequis.'
FROM scheduled_course sc, cohort co, course c
WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id AND co.name = 'CDA 2026' AND c.name = 'Développement Web côté Serveur (Back-End) / Java Spring Boot (partie 3)'
AND NOT EXISTS (SELECT 1 FROM scheduled_course_enrollment WHERE enrollment_id = 48);

-- --- Exemple d'inscription annulée ---

INSERT INTO enrollment (enrollment_id, enrollment_date, enrollment_status, cancelled_date, cancelled_reason, student_id, created_by, cancelled_by)
SELECT 49, '2026-09-01 09:00:00', 'NEW', '2026-09-08 14:30:00', 'Réorientation du stagiaire vers un autre cursus.', au_s.id, au_am.id, au_am.id
FROM app_user au_s, app_user au_am
WHERE au_s.email = 'eleve40.demo@formagest.fr' AND au_am.email = 'referente.demo@formagest.fr'
AND NOT EXISTS (SELECT 1 FROM enrollment WHERE enrollment_id = 49);

INSERT INTO scheduled_course_enrollment (enrollment_id, scheduled_course_id, forced)
SELECT 49, sc.id, FALSE
FROM scheduled_course sc, cohort co, course c
WHERE sc.cohort_id = co.cohort_id AND sc.course_id = c.course_id AND co.name = 'D2WM 2026' AND c.name = 'Analyse et Conception / Oracle Data Modeler'
AND NOT EXISTS (SELECT 1 FROM scheduled_course_enrollment WHERE enrollment_id = 49);
