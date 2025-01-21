FROM openjdk:22-jdk-slim as builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src/

RUN chmod +x gradlew

RUN ./gradlew build --no-daemon

FROM openjdk:22-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/bookapp-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
