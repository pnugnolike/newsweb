# 1. Stage: Build the application (ใช้ Maven ในการ Build JAR)
FROM maven:3.9-eclipse-temurin-17 AS build
# ตั้งค่าโฟลเดอร์ทำงานภายในคอนเทนเนอร์
WORKDIR /app
# คัดลอกไฟล์ที่เกี่ยวข้องกับ Build (pom.xml)
COPY pom.xml .
# ดาวน์โหลด Dependencies ทั้งหมด (เพื่อใช้ Layer Cache)
RUN mvn dependency:go-offline
# คัดลอก Source Code
COPY src ./src
# Build โปรเจกต์ และสร้างไฟล์ JAR
RUN mvn package -DskipTests

# 2. Stage: Run the application (ใช้ JRE Minimal เพื่อให้ Image เล็กที่สุด)
FROM eclipse-temurin:17-jre-focal
# ตั้งค่า Environment Variable สำหรับ Spring
ENV JAVA_TOOL_OPTIONS="-XX:+ExitOnOutOfMemoryError -Duser.timezone=Asia/Bangkok"
# สร้างโฟลเดอร์สำหรับ JAR
WORKDIR /app
# คัดลอกไฟล์ JAR ที่ Build เสร็จแล้วจาก Stage แรก
COPY --from=build /app/target/*.jar app.jar

# Spring Boot จะรันบน Port 8080 เป็นค่าเริ่มต้น
EXPOSE 8080

# คำสั่งเริ่มต้นรันแอปพลิเคชันเมื่อคอนเทนเนอร์เริ่มทำงาน
ENTRYPOINT ["java", "-jar", "app.jar"]