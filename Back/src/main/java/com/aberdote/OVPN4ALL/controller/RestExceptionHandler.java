package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<ErrorDTO> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(ErrorDTO.builder().error(ex.getError()).build(), ex.getHttpStatusCode());
    }
}
