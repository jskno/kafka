package com.jskno.domain.booking;

import lombok.Builder;

@Builder
public record SigBooking(
    Long bookingId,
    String station,
    String car
) {

}
