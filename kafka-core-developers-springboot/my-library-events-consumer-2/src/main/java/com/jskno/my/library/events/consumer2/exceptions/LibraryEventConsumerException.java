package com.jskno.my.library.events.consumer2.exceptions;

public class LibraryEventConsumerException extends RuntimeException {

    public LibraryEventConsumerException(String errorsMessage) {
        super(errorsMessage);
    }
}
