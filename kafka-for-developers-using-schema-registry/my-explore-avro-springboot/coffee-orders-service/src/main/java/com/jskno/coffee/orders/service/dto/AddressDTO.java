package com.jskno.coffee.orders.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDTO {

    private String addressLine1;
    private String city;
    private String state;
    private String country;
    private String zip;

}
