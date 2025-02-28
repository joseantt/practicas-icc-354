FROM gradle:8.4-jdk21 AS build

WORKDIR /app
COPY . /app/

RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/build/libs/server.jar app.jar

# Crear directorios para datos y logs
RUN mkdir -p /app/data /app/logs

CMD ["java", "-jar", "app.jar"]