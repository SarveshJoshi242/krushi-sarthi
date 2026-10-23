# Build stage using JDK 17 on Ubuntu Jammy (has full glibc & native toolchain support)
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app
COPY gradle gradle
COPY gradlew .
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true
COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# Run stage with lightweight JRE 17
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENV PORT=8080
EXPOSE 8080
# Tune memory for Render Free Tier (512MB RAM)
ENTRYPOINT ["sh", "-c", "java -XX:+UseSerialGC -Xmx350m -Dserver.port=${PORT} -jar app.jar"]
