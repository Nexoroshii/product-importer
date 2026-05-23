package com.example.productimporter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(

    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("refresh_token")
    String refreshToken,

    @JsonProperty("expires_in")
    Long expiresIn
) {
}
