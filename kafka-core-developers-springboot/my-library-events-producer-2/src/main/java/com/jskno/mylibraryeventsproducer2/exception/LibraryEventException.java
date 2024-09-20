package com.jskno.mylibraryeventsproducer2.exception;

public class LibraryEventException extends RuntimeException {

    public LibraryEventException(String message, Exception ex) {
        super(message, ex);
    }

    public LibraryEventException(String message) {
        this(message, null);
    }
}
