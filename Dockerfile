# Image để build ứng dụng (JDK 21 + Maven)
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Thư mục làm việc
WORKDIR /app

# Sao chép file pom.xml và tải dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Sao chép toàn bộ source code
COPY src /app/src

# Build ứng dụng
RUN mvn clean package -DskipTests

# Image runtime (chạy app) - chỉ chứa JDK 21
FROM eclipse-temurin:21-jdk

# Thư mục làm việc
WORKDIR /app

# Copy file JAR từ image build vào đây
COPY --from=build /app/target/*.jar clickwork.jar

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "clickwork.jar"]
