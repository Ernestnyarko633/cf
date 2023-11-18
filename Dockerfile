# Author: Ahmad Bilesanmi <ahmad@completefarmer.com>
# Dockerfile to create images
# Stage 1: Build the Spring Boot application
FROM maven:3.8.4 AS build
WORKDIR /app
COPY . /app
RUN mvn clean package -DskipTests

# Stage 2: Create a lightweight Docker image
FROM maven:3.8.5-openjdk-17
WORKDIR /app
ARG NEOBANK_SONARQUBE_ANALYSIS
ENV NEOBANK_SONARQUBE_ANALYSIS=${NEOBANK_SONARQUBE_ANALYSIS}
COPY --from=build /app/target/*.jar app.jar
EXPOSE 3000
EXPOSE 5432
EXPOSE 5671
ENTRYPOINT ["java", "-jar", "app.jar"]
