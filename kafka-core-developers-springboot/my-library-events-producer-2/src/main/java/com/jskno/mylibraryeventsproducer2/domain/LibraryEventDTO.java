package com.jskno.mylibraryeventsproducer2.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record LibraryEventDTO(
    @NotNull(groups = UpdateOperation.class)
    Long id,
    LibraryEventType type,
    @NotNull
    @Valid
    BookDTO book
) {

}
