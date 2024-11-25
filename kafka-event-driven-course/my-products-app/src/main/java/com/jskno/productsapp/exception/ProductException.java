package com.jskno.productsapp.exception;

import com.jskno.productsapp.domain.ErrorCode;
import lombok.Builder;
import lombok.Data;
import lombok.Generated;
import lombok.Getter;

@Getter
@Builder
public class ProductException extends RuntimeException {

    public ProductException( ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public ProductException( ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
    }

    private ErrorCode errorCode;
    private String message;
}