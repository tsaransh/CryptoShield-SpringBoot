package com.cryptoshield.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException{

    private HttpStatus httpStatus;
    private String message;

    public ApiException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public ApiException(HttpStatus httpStatus, String message, String message1) {
        super(message1);
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
