package com.mft.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.mft.springsecurity.entity.User}
 */
@AllArgsConstructor
@Getter
@ToString
public class UserRS implements Serializable {
    private final Long id;
    private final String name;
    private final String surname;
    private final LocalDate birthdate;
    private final Integer age;
    private final String city;
}