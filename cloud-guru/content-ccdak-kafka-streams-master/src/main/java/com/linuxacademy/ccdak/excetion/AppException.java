package com.linuxacademy.ccdak.excetion;

public class AppException extends RuntimeException {

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable ex) {
        super(message, ex);
    }
}
