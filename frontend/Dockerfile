# syntax=docker/dockerfile:1

# ─────────────────────────────────────────────────────────────
# FormaGest — image du front-end Angular
# ─────────────────────────────────────────────────────────────

# ---- Étape 1 : compilation ----
FROM node:22-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# ---- Étape 2 : service des fichiers statiques ----
FROM nginx:alpine AS runtime

# Configuration avec repli SPA vers index.html.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Angular place la sortie du build dans dist/<projet>/browser.
# <projet> = nom du projet dans angular.json / package.json (ici : frontend).
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80
