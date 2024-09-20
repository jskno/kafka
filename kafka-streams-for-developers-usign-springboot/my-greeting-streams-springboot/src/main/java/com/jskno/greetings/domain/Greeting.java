package com.jskno.greetings.domain;

import com.jskno.greetings.constants.GreetingType;
import java.time.OffsetDateTime;

public record Greeting(
    GreetingType type,
    String message,
    OffsetDateTime dateTime
) {

}
