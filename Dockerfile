# -----------------------------------------------------
# Stage 1: Build the application using Maven
# -----------------------------------------------------
FROM maven:3.9.7-eclipse-temurin-21 AS build

LABEL maintainer="akash"

WORKDIR /build

COPY pom.xml .

# Download dependencies (cached if pom.xml unchanged)
RUN mvn dependency:go-offline -B

COPY src ./src

# Build the application (skip tests - run separately in CI)
RUN mvn clean package -DskipTests -B

# -----------------------------------------------------
# Stage 2: Create the runtime image
# -----------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="akash"
LABEL application="embedqa"
LABEL version="1.0.0"

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy the application JAR from build stage
COPY --from=build /build/target/embedqa-*.jar app.jar


# Execute the application
ENTRYPOINT ["java", "-jar", "app.jar"]