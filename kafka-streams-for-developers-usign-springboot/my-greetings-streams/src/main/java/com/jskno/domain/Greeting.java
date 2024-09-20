package com.jskno.domain;

import java.time.OffsetDateTime;

public record Greeting(
    String message,
    OffsetDateTime timeStamp
) {

}
