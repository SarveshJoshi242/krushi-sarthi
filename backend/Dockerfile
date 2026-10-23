# Build stage using JDK 17
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY gradle gradle
COPY gradlew .
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true
COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# Run stage with JRE 17
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENV PORT=8080
EXPOSE 8080
# Tune memory for Render Free Tier (512MB RAM)
ENTRYPOINT ["sh", "-c", "java -XX:+UseSerialGC -Xmx350m -Dserver.port=${PORT} -jar app.jar"]
