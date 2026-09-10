# syntax=docker/dockerfile:1

# FormaGest — image du back-end Spring Boot

# ---- Étape 1 : compilation ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew clean bootJar --no-daemon
# bootJar produit aussi un <nom>-plain.jar sans dépendances : on garde l'autre.
RUN cp "$(find build/libs -name '*.jar' ! -name '*-plain.jar' | head -n 1)" app.jar

# ---- Étape 2 : exécution ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/app.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
