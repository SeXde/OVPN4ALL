package com.aberdote.OVPN4ALL.exception;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data @AllArgsConstructor @EqualsAndHashCode(callSuper=false)
public class CustomException extends RuntimeException{


    @NotNull
    private String error;
    @NotNull
    private HttpStatus httpStatusCode;

}
