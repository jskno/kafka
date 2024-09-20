package com.jskno.my.library.events.producer.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Book {

    @NotNull
    private Integer bookId;
    @NotBlank
    private String title;
    @NotBlank
    private String author;

}
