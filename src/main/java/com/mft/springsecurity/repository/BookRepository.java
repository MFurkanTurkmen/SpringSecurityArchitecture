package com.mft.springsecurity.repository;

import com.mft.springsecurity.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}