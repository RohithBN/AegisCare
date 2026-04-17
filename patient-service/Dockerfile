FROM gradle:9.4-jdk26 AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./
RUN gradle build -x test || true

COPY src ./src
RUN gradle clean build -x test

# ---- Runtime ----
FROM eclipse-temurin:26-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]