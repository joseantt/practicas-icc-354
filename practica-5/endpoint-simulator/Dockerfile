FROM gradle:8.4-jdk21 AS build

WORKDIR /app
COPY . /app/
RUN gradle shadowJar

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]