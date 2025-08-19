# === СТАДИЯ 1: "ГРЯЗНЫЙ ЦЕХ" (Сборка с помощью Maven) ===
#
# maven:<версия>-[jdk|eclipse-temurin]-<версия jdk>
#
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Рабочая директория
WORKDIR /build

# Копируем СНАЧАЛА pom.xml, чтобы Maven скачал зависимости
COPY pom.xml .
RUN mvn dependency:go-offline

# Теперь копируем остальной исходный код
COPY src ./src

# Запускаем сборку. Пропускаем тесты.
RUN mvn clean package -DskipTests

# === СТАДИЯ 2: "ЧИСТАЯ КОМНАТА" (Финальный, легкий образ) ===
# А вот здесь, мы ВСЕ РАВНО используем маленький образ, чтобы финальный
# контейнер был легким.
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Копируем "замедлялку"
COPY wait-for-it.sh .
RUN chmod +x ./wait-for-it.sh

# Копируем .jar ИЗ ПЕРВОЙ СТАДИИ ("builder")
COPY --from=builder /build/target/ticket-analyzer-1.0.0.jar app.jar

# Указываем, что запускать
ENTRYPOINT ["./wait-for-it.sh"]

# А в docker-compose.yml мы передадим команду, которую этот скрипт должен запустить ПОСЛЕ
# CMD ${MONGO_HOST} -- java -jar app.jar ${APP_ARGS}