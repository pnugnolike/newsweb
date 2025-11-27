package com.example.news.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        
        InputStream serviceAccount = null;
        
        // 1. เช็คก่อนว่ารันอยู่บน Render หรือไม่? (Render จะเก็บไฟล์ไว้ที่ /etc/secrets/...)
        File renderFile = new File("/etc/secrets/serviceAccountKey.json");
        
        if (renderFile.exists()) {
            System.out.println("✅ Loading Firebase Key from Render Secret Path (/etc/secrets/...)");
            serviceAccount = new FileInputStream(renderFile);
        } else {
            // 2. ถ้าไม่เจอ (เช่น รันในเครื่องตัวเอง Local) ให้หาจาก Classpath ปกติ
            System.out.println("⚠️ Render secret not found. Loading from Classpath (Local)...");
            serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
        }

        // ถ้าหาไม่เจอทั้ง 2 ที่
        if (serviceAccount == null) {
            System.err.println("❌ Error: Firebase Service Account file not found!");
            System.err.println("Please check: 1. Render Secret Files OR 2. src/main/resources");
            return null;
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://news-397d5.firebaseio.com")
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        } else {
            return FirebaseApp.getInstance();
        }
    }
}