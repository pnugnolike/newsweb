package com.example.news.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {

        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("se.json");

        if (serviceAccount == null) {
            System.err.println("Firebase Service Account file not found. Firebase Auth/Firestore will not work.");
            return null;
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                // 🎯 อัปเดต Project ID: news-397d5
                .setDatabaseUrl("https://news-397d5.firebaseio.com")
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        } else {
            return FirebaseApp.getInstance();
        }
    }
}