package com.jskno.domain.booking;

import lombok.Builder;

@Builder
public record GwyBooking(
    String id,
    String branch,
    String category
) {

}
