package com.jskno.mylibraryeventsproducer2.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookDTO(
    @NotNull
    Long id,
    @NotBlank
    String name,
    @NotBlank
    String author
) {

}
