package com.jskno.my.library.events.producer.exceptions;

public class LibraryIdNotFoundException extends RuntimeException{

    private static final String MESSAGE = "Please pass the library event id";

    public LibraryIdNotFoundException() {
        super(MESSAGE);
    }
}
