package com.mft.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;


@AllArgsConstructor
@Getter
@ToString
public class BookRQ implements Serializable {
    private final String name;
    private final String author;
    private final LocalDate releaseDate;
    private final Integer pageCount;
}