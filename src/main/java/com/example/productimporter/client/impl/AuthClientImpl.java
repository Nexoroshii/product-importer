package com.example.productimporter.client.impl;

import com.example.productimporter.client.AuthClient;
import com.example.productimporter.dto.LoginRequest;
import com.example.productimporter.dto.LoginResponse;
import com.example.productimporter.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthClientImpl implements AuthClient {

    private final WebClient webClient;

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            return webClient.post()
                .uri("/login")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LoginResponse.class)
                .block();
        } catch (WebClientResponseException e) {

            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.error(
                    "Invalid credentials for user: {}",
                    request.username()
                );
                throw new InvalidCredentialsException();
            }
            log.error(
                "Login request failed. Status: {}, Body: {}",
                e.getStatusCode(),
                e.getResponseBodyAsString()
            );
            throw e;
        }

    }
}
