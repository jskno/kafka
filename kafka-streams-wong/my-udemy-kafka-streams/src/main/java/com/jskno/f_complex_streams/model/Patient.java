package com.jskno.f_complex_streams.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Patient {

    private String id;
    private String name;
    private int age;
    private String sickRoomId;
}
