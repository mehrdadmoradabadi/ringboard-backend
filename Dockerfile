FROM ubuntu:latest
LABEL authors="Mehrdad"

FROM maven:latest as build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar wallboard.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/wallboard.jar"]
