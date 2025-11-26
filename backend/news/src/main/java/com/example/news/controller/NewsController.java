package com.example.news.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.SocketException;
import java.time.Duration;

@RestController
@RequestMapping("/api")
public class NewsController {

    private final String NEWS_API_KEY = "9344dce55dfd47a0aa1198d38f18f157";

    // 🟢 NEW: Inject WebClient ที่กำหนดค่าไว้แล้ว
    private final WebClient webClient;

    @Autowired // Spring จะ Inject WebClient Bean ที่เราสร้างใน NewsApplication.java
    public NewsController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/external-news")
    public Mono<Object> getExternalNews() {

        String url = String.format("/top-headlines?country=us&apiKey=%s", NEWS_API_KEY);

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class).map(Exception::new))
                .bodyToMono(Object.class)

                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(3))
                        .filter(e -> e instanceof WebClientRequestException || e.getCause() instanceof SocketException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {

                            System.err.println("Failed to fetch news after multiple retries due to network issue.");
                            return new RuntimeException("External News API connection failed after retries.", retrySignal.failure());
                        })
                );
    }

    @GetMapping("/search-news")
    public Mono<Object> searchNews(@org.springframework.web.bind.annotation.RequestParam String query) {
        String url = String.format("/everything?q=%s&apiKey=%s", query, NEWS_API_KEY);

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class).map(Exception::new))
                .bodyToMono(Object.class)
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(3))
                        .filter(e -> e instanceof WebClientRequestException || e.getCause() instanceof SocketException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            System.err.println("Failed to search news after multiple retries due to network issue.");
                            return new RuntimeException("External News API connection failed after retries.", retrySignal.failure());
                        })
                );
    }
}