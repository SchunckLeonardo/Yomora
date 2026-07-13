FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle gradle
COPY src src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S yomora && adduser -S yomora -G yomora
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
USER yomora
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

