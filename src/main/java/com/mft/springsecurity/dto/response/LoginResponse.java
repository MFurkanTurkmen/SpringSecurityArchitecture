// src/main/java/com/mft/springsecurity/dto/LoginResponse.java
package com.mft.springsecurity.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class LoginResponse {
    private String bearer;

}