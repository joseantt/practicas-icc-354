FROM gradle:jdk21-alpine AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

ENV env_port 8081
EXPOSE 8081

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/practica-1.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/practica-1.jar"]