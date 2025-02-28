FROM gradle:7.3-jdk11 AS build

WORKDIR /app
COPY . /app/
RUN gradle fatJar

FROM openjdk:11-jre-slim

WORKDIR /app
COPY --from=build /app/build/libs/*-all.jar app.jar

CMD ["java", "-jar", "app.jar"]