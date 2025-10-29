# ==========================
# STAGE 1: Build application
# ==========================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Tạo thư mục làm việc
WORKDIR /app

# Copy file cấu hình và mã nguồn
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

# Build ứng dụng (tạo file jar)
RUN mvn clean package -DskipTests

# ==========================
# STAGE 2: Run application
# ==========================
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy file .jar từ stage build
COPY --from=builder /app/target/*.jar app.jar

# Expose port 9090
EXPOSE 9090

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
