package com.example.JWT.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenResponse {
    Long userId;
    private String accessToken;
    private String refreshToken;
}
