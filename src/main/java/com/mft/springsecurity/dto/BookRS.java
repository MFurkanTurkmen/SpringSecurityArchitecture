package com.mft.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.mft.springsecurity.entity.Book}
 */
@AllArgsConstructor
@Getter
@ToString
public class BookRS implements Serializable {
    private final Integer id;
    private final String name;
    private final String author;
    private final LocalDate releaseDate;
    private final Integer pageCount;
}