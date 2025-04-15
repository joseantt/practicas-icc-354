FROM gradle:8.12.1-jdk21-alpine AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean vaadinBuildFrontend -Pvaadin.productionMode=true bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

EXPOSE 8080
ENV APP_PORT=8080

ENV TZ=${TIME_ZONE:-"America/Santo_Domingo"}

RUN mkdir /app && ln -snf /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime && echo ${TIME_ZONE} > /etc/timezone
COPY --from=build /home/gradle/src/build/libs/*.jar /app/practica-8.jar
ENTRYPOINT ["java", "-Duser.timezone=${TZ}", "-Dvaadin.productionMode=true", "-jar", "/app/practica-8.jar"]