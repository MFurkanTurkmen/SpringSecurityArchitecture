// src/main/java/com/mft/springsecurity/exception/AuthenticationException.java
package com.mft.springsecurity.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthenticationException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public AuthenticationException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}