# ===============================
# Stage 1: Build the application
# ===============================
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (better caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN mvn clean package -DskipTests


# ===============================
# Stage 2: Run the application
# ===============================
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/KFCC_Backend-0.0.1-SNAPSHOT.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
