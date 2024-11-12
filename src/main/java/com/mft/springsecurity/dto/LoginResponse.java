// src/main/java/com/mft/springsecurity/dto/LoginResponse.java
package com.mft.springsecurity.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Instant expiresAt;
    private String username;
    private List<String> roles;
    private UserInfo userInfo;

    @Data
    @Builder
    public static class UserInfo {
        private Long id;
        private String name;
        private String surname;
        private String email;
        private String lastLoginDate;
        private String lastLoginIp;
    }
}