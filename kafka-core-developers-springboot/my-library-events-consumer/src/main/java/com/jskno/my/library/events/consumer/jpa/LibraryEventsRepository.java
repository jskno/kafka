package com.jskno.my.library.events.consumer.jpa;

import com.jskno.my.library.events.consumer.entity.LibraryEvent;
import org.springframework.data.repository.CrudRepository;

public interface LibraryEventsRepository extends CrudRepository<LibraryEvent, Integer> {

}
