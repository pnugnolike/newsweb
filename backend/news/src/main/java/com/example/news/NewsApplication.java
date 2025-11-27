package com.example.news;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class NewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsApplication.class, args);
	}

	// การตั้งค่า CORS (ส่วนเดิม)
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**")
						// ตรวจสอบ Port ของ Vue.js (ค่าเริ่มต้นของ Vite คือ 5173)
						.allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
			}
		};
	}

	// 🟢 NEW: กำหนด WebClient Bean พร้อม Timeout
	@Bean
	public WebClient webClient() {
		// 1. ตั้งค่า HttpClient โดยใช้ Netty
		HttpClient httpClient = HttpClient.create()
				// กำหนด Connection Timeout: รอ 5 วินาที สำหรับการสร้างการเชื่อมต่อ
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
				.responseTimeout(Duration.ofSeconds(30)) // กำหนด Response Timeout รวม (30s)
				.doOnConnected(conn -> conn
						// กำหนด Read Timeout: ไม่มีการรับข้อมูลนานเกิน 15 วินาที
						.addHandlerLast(new ReadTimeoutHandler(15, TimeUnit.SECONDS)));

		// 2. สร้าง WebClient โดยใช้ Connector ที่กำหนด HttpClient แล้ว
		return WebClient.builder()
				.baseUrl("https://newsapi.org/v2") // กำหนด Base URL ตรงนี้
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();
	}
}