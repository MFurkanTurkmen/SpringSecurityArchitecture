package com.mft.springsecurity.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {

    private final AllExceptions exp;

    public AuthenticationException(AllExceptions enumException) {
        super(enumException.message);
        this.exp = enumException;
    }
    public AuthenticationException(AllExceptions enumException, String message) {
        super(message);
        enumException.message = message;
        this.exp = enumException;
    }

}