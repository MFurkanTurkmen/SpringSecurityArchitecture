package com.mft.springsecurity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@AllArgsConstructor
@Getter
@ToString
public class SignupRequest {
    private String name;
    private String username;
    private String password;
    private Set<String> role;
}
