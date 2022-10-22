package com.aberdote.OVPN4ALL.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CustomException extends RuntimeException{
    private String error;
    private HttpStatus httpStatus;
    public CustomException(String error, HttpStatus httpStatus) {
        this.error = error;
        this.httpStatus = httpStatus;
    }
}
