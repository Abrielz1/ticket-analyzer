# === СТАДИЯ 1: "ГРЯЗНЫЙ ЦЕХ" (Сборка с помощью Maven) ===
# Берем образ, в котором уже есть и Java, и Maven
FROM eclipse-temurin:17-jdk-jammy as builder

# Рабочая директория
WORKDIR /build

# Копируем СНАЧАЛА pom.xml, чтобы Maven скачал зависимости
# Этот слой будет кэшироваться, если зависимости не меняются.
COPY pom.xml .
RUN mvn dependency:go-offline

# Теперь копируем остальной исходный код
COPY src ./src

# Запускаем сборку. Пропускаем тесты, т.к. их можно гонять отдельно в CI.
RUN mvn clean package -DskipTests

# === СТАДИЯ 2: "ЧИСТАЯ КОМНАТА" (Финальный, легкий образ) ===
# Берем САМЫЙ, МАЛЕНЬКИЙ образ, где есть ТОЛЬКО Java для запуска.
FROM eclipse-temurin:17-jre-alpine

# Рабочая директория в финальном образе
WORKDIR /app

# --- ШАГ 1: КОПИРУЕМ "ИНСТРУМЕНТЫ" ---

# Сначала, КОПИРУЕМ "ЗАМЕДЛЯЛКУ"
# Docker будет искать wait-for-it.sh РЯДОМ с Dockerfile
COPY wait-for-it.sh .

# ТЕПЕРЬ, ДЕЛАЕМ ЕГО ИСПОЛНЯЕМЫМ! ЭТО КРИТИЧЕСКИ ВАЖНО!
RUN chmod +x ./wait-for-it.sh
# --- ШАГ 2: КОПИРУЕМ НАШЕ "ОРУЖИЕ" ---
#  Теперь копируем .jar-ник из первой стадии
COPY --from=builder /build/target/ticket-analyzer-1.0.0.jar app.jar

# --- ШАГ 3: УКАЗЫВАЕМ, ЧТО ЗАПУСКАТЬ ---
# ENTRYPOINT указывает НА ГЛАВНЫЙ ИНСТРУМЕНТ - на "замедлялку"
ENTRYPOINT ["./wait-for-it.sh"]

# А в docker-compose.yml мы передадим команду, которую этот скрипт должен запустить ПОСЛЕ
# Пример для docker-compose -> command: > mongo:27017 -- java -jar app.jar "arg1" "arg2"