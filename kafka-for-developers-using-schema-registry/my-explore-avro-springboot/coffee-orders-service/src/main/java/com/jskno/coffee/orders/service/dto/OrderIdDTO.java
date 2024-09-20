package com.jskno.coffee.orders.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderIdDTO {

    private Long businessId;
    @JsonProperty("UUID")
    private UUID UUID;

}
