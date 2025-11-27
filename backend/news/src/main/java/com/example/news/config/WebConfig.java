package com.example.news.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * กำหนดค่า Global CORS สำหรับ Spring Boot Application.
 * ไฟล์นี้ใช้เพื่อแก้ไขปัญหา 'Access-Control-Allow-Origin' Header
 * ที่ Frontend (http://localhost:4173) ถูกบล็อกไม่ให้เข้าถึง Backend (http://localhost:8080).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // อนุญาตให้ทุก path (/**) สามารถถูกเข้าถึงได้จากทุก Origin (*)
        // นี่คือการตั้งค่าที่ง่ายและรวดเร็วสำหรับช่วง Development/Testing
        registry.addMapping("/**")
                .allowedOrigins("*") // 1. อนุญาตทุกโดเมน/พอร์ต
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 2. อนุญาต HTTP Methods ที่จำเป็น
                .allowedHeaders("*"); // 3. อนุญาต Header ทั้งหมด

        // 🚨 คำแนะนำ: ใน Production ควรเปลี่ยน "*" ให้เป็น URL ที่แน่นอนของ Frontend
        // เช่น .allowedOrigins("https://news-397d5.web.app")
    }
}