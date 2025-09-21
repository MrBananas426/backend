# syntax=docker/dockerfile:1

FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package && \
    JAR_PATH=$(ls -1 target/*.jar | grep -v '\.original$' | head -n 1) && \
    cp "$JAR_PATH" /workspace/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV PORT=8080
COPY --from=build /workspace/app.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
