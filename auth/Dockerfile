FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build.gradle ./
COPY src ./src

RUN ./gradlew build -x test

EXPOSE 8090

CMD ["java", "-jar", "build/libs/auth-0.0.1-SNAPSHOT.jar"]