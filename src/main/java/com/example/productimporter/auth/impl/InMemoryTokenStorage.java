package com.example.productimporter.auth.impl;

import com.example.productimporter.auth.TokenStorage;
import org.springframework.stereotype.Service;

@Service
public class InMemoryTokenStorage implements TokenStorage {

    private volatile String accessToken;

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public void setAccessToken(String token) {
        this.accessToken = token;
    }
}
