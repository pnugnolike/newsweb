package com.example.news.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:5173",             // 1. อนุญาต Localhost (เผื่อรัน Front ในเครื่อง)
                    "http://localhost:4173",             // 1.1 เผื่อ Localhost port อื่น
                    "https://news-397d5.web.app",        // 2. อนุญาต Firebase Hosting (URL หลัก)
                    "https://news-397d5.firebaseapp.com" // 3. อนุญาต Firebase Hosting (URL สำรอง)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // อนุญาตให้ส่ง Cookies/Auth Headers ข้ามโดเมนได้
    }
}