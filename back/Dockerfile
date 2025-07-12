# Étape 1 : Build Maven
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Copier pom.xml et télécharger les dépendances séparément
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier le code source
COPY src ./src

# Compiler et packager l'application
RUN mvn package

# Étape 2 : Image de production avec JDK uniquement
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copier le jar généré depuis l'étape de build
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]