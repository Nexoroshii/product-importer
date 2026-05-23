package com.example.productimporter.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final ApiProperties properties;

    @Bean
    public WebClient webClient() {

        HttpClient httpClient = HttpClient.create()
            .compress(true);

        ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(configurer ->
                configurer.defaultCodecs()
                    .maxInMemorySize(50 * 1024 * 1024)
            )
            .build();

        return WebClient.builder()
            .baseUrl(properties.baseUrl())
            .defaultHeader(
                HttpHeaders.ACCEPT,
                "application/json"
            )
            .clientConnector(
                new ReactorClientHttpConnector(httpClient)
            )
            .exchangeStrategies(strategies)
            .build();
    }
}
