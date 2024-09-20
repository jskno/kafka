package com.jskno.my.library.events.consumer2.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record BookDTO(
    @NotNull(groups = {NewOperation.class, UpdateOperation.class})
    Long id,
    @NotBlank(groups = {NewOperation.class, UpdateOperation.class})
    @Size(min = 4, message = "Too short book name", groups = {NewOperation.class, UpdateOperation.class})
    String name,
    @NotBlank(groups = {NewOperation.class, UpdateOperation.class})
    String author
) {

}
