FROM gradle:8.12.1-jdk21-alpine AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

ENV PORT=8080
VOLUME /app/logs

RUN mkdir /app
RUN mkdir /app/logs
COPY --from=build /home/gradle/src/build/libs/*.jar /app/practica-1.jar
ENTRYPOINT ["java", "-jar", "/app/practica-1.jar"]