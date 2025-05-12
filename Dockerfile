# Image để build ứng dụng (JDK 21 + Maven)
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src /app/src

RUN mvn clean package -DskipTests

# Image runtime (chạy app) - chỉ chứa JDK 21
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy file JAR từ image build
COPY --from=build /app/target/*.jar clickwork.jar

# ✅ Copy thư mục uploads (nếu nó nằm cùng cấp với Dockerfile)
COPY uploads/ uploads/


ENTRYPOINT ["java", "-jar", "clickwork.jar"]
