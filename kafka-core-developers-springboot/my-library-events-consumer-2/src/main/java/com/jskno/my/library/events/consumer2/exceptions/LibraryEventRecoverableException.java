package com.jskno.my.library.events.consumer2.exceptions;

public class LibraryEventRecoverableException extends RuntimeException {

    public LibraryEventRecoverableException(String errorsMessage) {
        super(errorsMessage);
    }
}
