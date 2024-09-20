package com.jskno.my.library.events.consumer2.domain;

import com.jskno.my.library.events.consumer2.entity.LibraryEventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;

@Builder
public record LibraryEventDTO(
    @NotNull(groups = UpdateOperation.class)
    @Null(groups = NewOperation.class)
    Long id,
    @NotNull(groups = {NewOperation.class, UpdateOperation.class})
    LibraryEventType type,
    @NotNull(groups = {NewOperation.class, UpdateOperation.class})
    @Valid
    BookDTO book
) {

}
