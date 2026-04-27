# СТУПІНЬ 1: Збірка (Build)
FROM gradle:8.5-jdk17-alpine AS build
# Встановлюємо робочу директорію
WORKDIR /app
# Копіюємо тільки файли залежностей для кэшування
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
# Завантажуємо залежності (це значно прискорить наступні збірки)
RUN gradle dependencies --no-daemon

# Копіюємо вихідний код
COPY ./graphql-gateway/src ./src
# Збираємо JAR (skip tests для швидкості, краще робити в CI/CD)
RUN gradle build -x test --no-daemon

# СТУПІНЬ 2: Запуск (Production)
FROM eclipse-temurin:17-jre-alpine
# Створюємо користувача для безпеки (не root)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring
# Робоча директорія
WORKDIR /app
# Копіюємо JAR з першого етапу
COPY --from=build /app/build/libs/*.jar app.jar
# Відкриваємо порт
EXPOSE 8080
# Команда запуску з оптимізацією для контейнерів
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
