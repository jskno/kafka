package com.jskno.my.library.events.consumer2.mappers;

import com.jskno.my.library.events.consumer2.domain.LibraryEventDTO;
import com.jskno.my.library.events.consumer2.entity.LibraryEvent;
import org.mapstruct.Mapper;

@Mapper
public interface LibraryEventMapper {

    LibraryEvent mapToEntity(LibraryEventDTO libraryEventDTO);

}
