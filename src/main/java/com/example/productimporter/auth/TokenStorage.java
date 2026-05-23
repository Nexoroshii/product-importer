package com.example.productimporter.auth;

public interface TokenStorage {

    String getAccessToken();

    void setAccessToken(String token);
}
