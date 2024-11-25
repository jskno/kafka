package com.jskno.productsapp.rest;

import com.jskno.productsapp.domain.ErrorMessage;
import com.jskno.productsapp.exception.ProductException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.ProtocolException;
import java.time.OffsetDateTime;

@ControllerAdvice
@Slf4j
public class ProductControllerAdvice {

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<ErrorMessage> handleProductException(ProductException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(OffsetDateTime.now(), ex.getErrorCode(), ex.getMessage()));
    }
}
