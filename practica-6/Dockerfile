FROM gradle:8.12.1-jdk21-alpine AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean vaadinBuildFrontend -Pvaadin.productionMode=true bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

EXPOSE 8080
ENV APP_PORT=${APP_PORT:-8080}

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/practica-4.jar
ENTRYPOINT ["java", "-Dvaadin.productionMode=true", "-jar", "/app/practica-4.jar"]