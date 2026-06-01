# ===== Etapa 1: compilar el JAR con Maven + JDK 21 =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Copiamos primero el pom para cachear la descarga de dependencias
COPY pom.xml .
RUN mvn -B dependency:go-offline
# Ahora el codigo fuente y empaquetamos (sin tests)
COPY src ./src
RUN mvn -B clean package -DskipTests

# ===== Etapa 2: imagen ligera con solo el JRE =====
FROM eclipse-temurin:21-jre
WORKDIR /app
# curl se usa para el healthcheck de docker-compose
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
