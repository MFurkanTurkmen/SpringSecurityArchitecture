package com.mft.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * DTO for {@link com.mft.springsecurity.entity.User}
 */
@AllArgsConstructor
@Getter
@ToString
public class UserRQ implements Serializable {
    private final String name;
    private final String surname;
}