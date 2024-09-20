package com.jskno.my.library.events.consumer2.repository;

import com.jskno.my.library.events.consumer2.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
