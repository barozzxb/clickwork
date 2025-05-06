# Sử dụng hình ảnh Java 21 làm nền tảng
FROM openjdk:21-jdk-slim AS build

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép file pom.xml và tải các phụ thuộc
COPY pom.xml .
RUN mvn dependency:go-offline

# Sao chép mã nguồn vào container
COPY src /app/src

# Biên dịch và đóng gói ứng dụng
RUN mvn clean package -DskipTests

# Chạy ứng dụng Spring Boot
FROM openjdk:21-jdk-slim
COPY --from=build /app/target/your-app-name.jar /your-app-name.jar
CMD ["java", "-jar", "/clickwork-1.1.0.jar"]
