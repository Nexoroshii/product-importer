package com.example.productimporter.client;

import com.example.productimporter.dto.LoginRequest;
import com.example.productimporter.dto.LoginResponse;

public interface AuthClient {

    LoginResponse login(LoginRequest request);
}
