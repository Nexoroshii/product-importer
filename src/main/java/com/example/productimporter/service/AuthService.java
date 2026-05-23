package com.example.productimporter.service;

import com.example.productimporter.auth.TokenStorage;
import com.example.productimporter.client.AuthClient;
import com.example.productimporter.config.ApiProperties;
import com.example.productimporter.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthClient authClient;
    private final TokenStorage tokenStorage;
    private final ApiProperties properties;

    public void login() {

        var response = authClient.login(
            new LoginRequest(
                properties.username(),
                properties.password()
            )
        );

        tokenStorage.setAccessToken(
            response.accessToken()
        );
    }
}
