package com.mft.springsecurity.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
@AllArgsConstructor
public enum AllExceptions {

    ERROR_400(400, "bilinmeyen hata",HttpStatus.BAD_REQUEST),;

    int code;
    String message;
    HttpStatus httpStatus;
}
