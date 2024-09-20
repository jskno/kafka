package com.jskno.my.library.events.consumer2.repository;

import com.jskno.my.library.events.consumer2.entity.LibraryEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryEventRepository extends JpaRepository<LibraryEvent, Long> {

}
