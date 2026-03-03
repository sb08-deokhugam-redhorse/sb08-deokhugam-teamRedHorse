package com.redhorse.deokhugam.domain.book.repository;

import com.redhorse.deokhugam.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
}
