FROM maven:3.8.2-openjdk-17 AS build
LABEL authors="Mehrdad"
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar RingBoard.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/RingBoard.jar"]