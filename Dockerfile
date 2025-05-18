# 1. Build Stage
FROM gradle:8.5-jdk21-alpine AS build
WORKDIR /app

# Copy build scripts first (for better Docker caching)
COPY build.gradle settings.gradle ./
RUN gradle build --no-daemon || return 0

# Then copy the full source
COPY . .
RUN gradle clean build -x test --no-daemon

# 2. Runtime Stage
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Set the timezone to Kigali
ENV TZ=Africa/Kigali

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the app port
EXPOSE 8070

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
