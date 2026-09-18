# FormaGest

Application web de gestion pédagogique pour Form'Avenir : filières, cursus, cours, promotions, inscriptions et calendriers élèves. Projet certificatif Concepteur Développeur d'Applications (ENI), réalisé en binôme.

Le dépôt est un mono-repo : le back-end Java Spring Boot dans `backend/`, le front-end Angular dans `frontend/`, et un `docker-compose.yml` à la racine qui orchestre les deux services avec une base MySQL.

## Stack et versions

| Brique | Version                |
|--------|------------------------|
| Java (JDK) | 21 (LTS)               |
| Build back-end | Gradle 9.4.1 (wrapper) |
| Spring Boot | 4.1.0 |
| MySQL | 8.4 |
| Node | 22 (≥ 22.22.3) |
| Angular | 22.1 |
| TypeScript | 6.0 |
| Angular Material | 22.1 (thème Material 3) |

## Prérequis

Pour le lancement conteneurisé, Docker et le plugin Compose suffisent. Pour développer hors conteneur, il faut en plus un JDK 21, Node 22 (au moins 22.22.3, exigé par le CLI Angular) et un client MySQL.

## Arborescence

```
backend/            code Spring Boot + Dockerfile
frontend/           code Angular + Dockerfile + nginx.conf
docs/               analyse, conception, diagrammes UML
docker-compose.yml  orchestration db + backend + frontend
.env.example        modèle de configuration, à copier en .env
```

## Configuration

Copier le modèle et renseigner les valeurs :

```
cp .env.example .env
```

Le `.env` porte les identifiants MySQL, les ports exposés et le secret de signature des tokens (`APP_JWT_SECRET`). Il n'est pas versionné. Générer un secret solide, par exemple avec `openssl rand -base64 48`.

## Lancement avec Docker

```
docker compose up --build
```

Le front est alors servi sur http://localhost:4200, l'API sur http://localhost:8080 et MySQL sur le port 3306. Compose attend que la base réponde avant de démarrer le back-end.

## Lancement en développement local

Base de données via Docker, back et front lancés à la main :

```
docker compose up db
cd backend && ./gradlew bootRun
cd frontend && npm install && npm start
```

Le serveur de dev Angular tourne sur http://localhost:4200 avec rechargement à chaud. Il appelle l'API sur http://localhost:8080 ; la configuration correspondante se fait dans `frontend/src/environments/`.

Le fichier `.env` n'est lu que par Docker Compose. Pour lancer le back-end directement depuis un IDE (sans passer par `docker compose up db` puis `bootRun`/le bouton Run), il faut renseigner `APP_JWT_SECRET` dans la configuration d'exécution (variables d'environnement), sans quoi l'application ne démarre pas.

## Authentification

Connexion par identifiant (email) et mot de passe, sur `POST /api/auth/login`. La réponse contient le profil de l'utilisateur dans son corps ; le jeton JWT est posé par le back dans un cookie `HttpOnly` (`Set-Cookie`, nom configurable via `APP_JWT_COOKIE_NAME`, `formagest_token` par défaut). Le navigateur le renvoie automatiquement sur les requêtes suivantes : il n'y a pas d'en-tête `Authorization` à positionner à la main.

```
curl -c cookies.txt -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"referente.demo@formagest.fr","password":"Formagest2026!"}'
```

`GET /api/auth/me` renvoie l'utilisateur courant à partir du cookie et sert à vérifier qu'il est toujours valide :

```
curl -b cookies.txt http://localhost:8080/api/auth/me
```

`POST /api/auth/logout` vide le cookie côté serveur :

```
curl -b cookies.txt -X POST http://localhost:8080/api/auth/logout
```