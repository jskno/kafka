package com.jskno.my.library.events.producer.controller;

import com.jskno.my.library.events.producer.exceptions.LibraryIdNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class LibraryEventsControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
            .map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
            .sorted()
            .collect(Collectors.joining(", "));
        log.info("Error message: {}", errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LibraryIdNotFoundException.class)
    public ResponseEntity<?> handleLibraryIdNotFoundException(LibraryIdNotFoundException ex) {
        String message = ex.getMessage();
//        String errorMessage = fieldErrors.stream()
//            .map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
//            .sorted()
//            .collect(Collectors.joining(", "));
        log.info("Error message: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(Exception ex) {
        String message = ex.getMessage();
//        String errorMessage = fieldErrors.stream()
//            .map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
//            .sorted()
//            .collect(Collectors.joining(", "));
        log.info("Error message: {}", message);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }



}
