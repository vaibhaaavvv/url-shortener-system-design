# Use Maven image to build the application
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy parent pom first
COPY pom.xml ./pom.xml

# Copy service pom and source
COPY redirect-analytics-service/pom.xml ./redirect-analytics-service/pom.xml
COPY redirect-analytics-service/src ./redirect-analytics-service/src

# Change to service directory
WORKDIR /app/redirect-analytics-service

# Build the application
RUN mvn clean package -DskipTests

# Use Eclipse Temurin for runtime
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/redirect-analytics-service/target/redirect-analytics-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8082

# Run the application
CMD ["java", "-jar", "app.jar"]