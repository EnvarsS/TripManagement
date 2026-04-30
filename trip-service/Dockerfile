# Етап 1: Збірка проєкту за допомогою Gradle
FROM gradle:7.6-jdk17 AS build
WORKDIR /app

# Копіюємо конфіги gradle для кешування залежностей
COPY build.gradle settings.gradle /app/
RUN gradle build --no-daemon > /dev/null 2>&1 || true

# Копіюємо вихідний код і збираємо jar
COPY src /app/src
RUN gradle bootJar --no-daemon

# Етап 2: Створення фінального образу з JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Додаємо curl для healthcheck (корисно в docker-compose)
RUN apk add --no-cache curl

# Копіюємо зібраний jar з попереднього етапу
COPY --from=build /app/build/libs/*.jar app.jar

# Налаштування пам'яті для JVM в контейнері
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]

EXPOSE 8082