FROM gradle:8.0.2-jdk11-alpine AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar ./app.jar
CMD ["java", "-jar", "app.jar"]